package com.saverio.finapp.ui.messages

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.saverio.finapp.MainActivity
import com.saverio.finapp.MainActivity.Companion.NOTIFICATIONS
import com.saverio.finapp.MainActivity.Companion.PREFERENCES_NAME
import com.saverio.finapp.R
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.PostMessageResponseList
import com.saverio.finapp.api.messages.AllMessagesItemsList
import com.saverio.finapp.api.messages.AllMessagesList
import com.saverio.finapp.api.messages.MessagePostList
import com.saverio.finapp.api.messages.MessagesSectionsList
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SectionsModel
import com.saverio.finapp.db.StatisticsModel
import com.saverio.finapp.internet.NetworkConnection
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MessagesActivity : AppCompatActivity() {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    var currentRunnable: Runnable? = null
    val mainHandler = Handler(Looper.getMainLooper())

    var connected: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val bundle = intent.extras

        var section_id = ""
        var source = ""
        if (bundle != null) {
            section_id = bundle.getString("section_id")!!
            if (bundle.containsKey("source")) source = bundle.getString("source")!!
        }

        val databaseHandler = DatabaseHandler(this)
        val sectionDetails = databaseHandler.getSection(section_id)
        databaseHandler.close()

        load(section_id = section_id, section_details = sectionDetails)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = getString(R.string.title_discussion_section, section_id)
            actionBar.subtitle = sectionDetails.title
        }
    }

    fun load(section_id: String, section_details: SectionsModel) {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            val editTextMessage: EditText = findViewById(R.id.editTextMessageText)
            val buttonSend: Button = findViewById(R.id.buttonSendMessage)
            val constraintNoConnection: ConstraintLayout =
                findViewById(R.id.constraintLayoutNoInternetConnectionActivityMessages)
            connected = isConnected
            if (isConnected) {
                //connected
                editTextMessage.isEnabled = true
                buttonSend.isEnabled = true

                constraintNoConnection.isGone = true
            } else {
                //not connected
                editTextMessage.isEnabled = false
                buttonSend.isEnabled = false

                constraintNoConnection.isGone = false
                swipeRefreshLayout.isRefreshing = false
            }
        })

        if (checkLogged()) {
            //logged
            val buttonSend: Button = findViewById(R.id.buttonSendMessage)
            val editTextMessage: EditText = findViewById(R.id.editTextMessageText)
            editTextMessage.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (editTextMessage.text.toString().replace(" ", "")
                            .replace("\n", "") != ""
                    ) buttonSend.isGone = false
                    else buttonSend.isGone = true
                }
            })

            buttonSend.setOnClickListener {
                //send message action
                if (editTextMessage.text.toString().replace(" ", "").replace("\n", "") != "") {
                    sendMessage(
                        reply_to = -1,
                        text = editTextMessage.text.toString(),
                        section_id = section_id
                    )
                }
            }

            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutActivityMessages)
            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE).edit()
                .putBoolean("force_update", true).apply()

            swipeRefreshLayout.setOnRefreshListener {
                setDatetime("", section = section_id)
                getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE).edit()
                    .putBoolean("force_update", true).apply()
                getAllMessages(startTask = false, section_id = section_id)
            }
            getAllMessages(startTask = true, section_id = section_id)
        } else {
            //no logged
            onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //add the back button event in the actionbar
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun sendMessage(reply_to: Int = -1, text: String, section_id: String) {
        val editTextMessage: EditText = findViewById(R.id.editTextMessageText)
        val buttonSend: Button = findViewById(R.id.buttonSendMessage)
        editTextMessage.isEnabled = false
        buttonSend.isEnabled = false
        val call: Call<PostMessageResponseList> =
            ApiClient.client.postMessageInfo(
                userid = getUserid(),
                reply_to = reply_to,
                section = section_id,
                text = text
            )
        call.enqueue(object : Callback<PostMessageResponseList> {

            override fun onResponse(
                call: Call<PostMessageResponseList>?,
                response: Response<PostMessageResponseList>?
            ) {
                //println("Response:\n" + response?.body().toString())

                if (response!!.isSuccessful && response.body() != null) {
                    val responseList = response.body()!!

                    if (responseList.code == 200) editTextMessage.setText("")
                    else editTextMessage.setError(responseList.description)

                    editTextMessage.isEnabled = true
                    buttonSend.isEnabled = true

                    getAllMessages(startTask = false, section_id = section_id)
                }
            }

            override fun onFailure(call: Call<PostMessageResponseList>?, t: Throwable?) {
                Log.v("Error", t.toString())

                editTextMessage.isEnabled = true
                buttonSend.isEnabled = true
                editTextMessage.setText("")
            }

        })
    }

    fun getAllMessages(startTask: Boolean = false, section_id: String) {
        if (connected) {
            val editTextMessage: EditText = findViewById(R.id.editTextMessageText)
            val buttonSend: Button = findViewById(R.id.buttonSendMessage)

            if (!startTask) {
                swipeRefreshLayout.isRefreshing = true
            }

            val call: Call<AllMessagesList> =
                ApiClient.client.getAllMessagesInfo(section = section_id)
            call.enqueue(object : Callback<AllMessagesList> {

                override fun onResponse(
                    call: Call<AllMessagesList>?,
                    response: Response<AllMessagesList>?
                ) {

                    //println("Response:\n" + response!!.body()!!)

                    if (response!!.isSuccessful && response.body() != null) {
                        val responseList = response.body()!!

                        swipeRefreshLayout.isRefreshing = false

                        val currentDatetime = responseList.lastUpdate?.datetime.toString()
                        val forcedUpdated = getSharedPreferences(
                            NOTIFICATIONS,
                            Context.MODE_PRIVATE
                        ).getBoolean("force_update", true)

                        if (forcedUpdated || getDatetime("datetime_$section_id") != currentDatetime) {
                            setDatetime(
                                value = currentDatetime,
                                section = section_id
                            )

                            setupRecyclerView(
                                clear = true,
                                messages = responseList.messages
                            )
                            editTextMessage.isEnabled = true
                            buttonSend.isEnabled = true
                            editTextMessage.setText("")
                        }
                    }
                    getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE).edit()
                        .putBoolean("force_update", false).apply()
                }

                override fun onFailure(call: Call<AllMessagesList>?, t: Throwable?) {
                    //progerssProgressDialog.dismiss()
                    Log.v("Error", t.toString())
                    editTextMessage.isEnabled = true
                    buttonSend.isEnabled = true
                    editTextMessage.setText("")
                    getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE).edit()
                        .putBoolean("force_update", false).apply()
                }

            })
        }

        if (startTask) {
            currentRunnable = Runnable { getAllMessages(startTask = true, section_id = section_id) }
            mainHandler.postDelayed(
                currentRunnable!!,
                5000
            ) //every 5 seconds
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onResume() {
        //println("Resuming")
        val bundle = intent.extras

        var section_id = ""
        var source = ""
        if (bundle != null) {
            section_id = bundle.getString("section_id")!!
            if (bundle.containsKey("source")) source = bundle.getString("source")!!
        }

        val databaseHandler = DatabaseHandler(this)
        val sectionDetails = databaseHandler.getSection(section_id)
        databaseHandler.close()

        load(section_id = section_id, section_details = sectionDetails)
        super.onResume()
    }

    override fun onDestroy() {
        if (currentRunnable != null) mainHandler.removeCallbacks(currentRunnable!!)
        super.onDestroy()
    }

    fun setupRecyclerView(
        clear: Boolean = false,
        messages: List<AllMessagesItemsList>?
    ) {
        try {
            val messagesItemsList: RecyclerView =
                findViewById(R.id.messages_conversation_items_list)
            val noMessagesAvailableText: TextView =
                findViewById(R.id.no_messages_available_here_text)

            if (clear) messagesItemsList.adapter = null

            if (messages != null && messages.isNotEmpty()) {
                messagesItemsList.visibility = View.VISIBLE
                noMessagesAvailableText.isGone = true

                //messagesItemsList.layoutManager = LinearLayoutManager(this)
                //binding.newsItemsList.setHasFixedSize(true)
                val itemAdapter = AllMessagesItemAdapter(
                    context = this,
                    items = messages,
                    username = getUsername()
                )
                messagesItemsList.adapter = itemAdapter
                messagesItemsList.scrollToPosition(itemAdapter.itemCount - 1);
            } else {
                messagesItemsList.visibility = View.GONE
                noMessagesAvailableText.text = getString(R.string.no_messages_available_here_text)
                noMessagesAvailableText.isGone = false
            }
        } catch (e: Exception) {
            Log.v("Exception", e.toString())
        }
    }

    fun checkLogged(): Boolean {
        return (getVariable("userid") != "" && getVariable("userid") != null)
    }

    fun getUserid(): String {
        return (if (checkLogged()) getVariable("userid")!! else "")
    }

    fun getUsername(): String {
        return (if (checkLogged()) getVariable("username")!! else "")
    }

    fun setDatetime(value: String, section: String) {
        //println("Set datetime of $section with $value")
        getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE).edit()
            .putString("datetime_" + section, value).apply()
    }

    private fun setVariable(variable: String, value: String?) {
        getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
            .putString(variable, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getString(
            variable,
            null
        )
    }

    private fun getDatetime(variable: String): String? {
        return getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE).getString(variable, null)
    }
}