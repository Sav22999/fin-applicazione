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
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.saverio.finapp.MainActivity
import com.saverio.finapp.R
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.PostMessageResponseList
import com.saverio.finapp.api.messages.AllMessagesItemsList
import com.saverio.finapp.api.messages.AllMessagesList
import com.saverio.finapp.api.messages.MessagesSectionsList
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SectionsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MessagesActivity : AppCompatActivity() {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var section_id: String

    var currentRunnable: Runnable? = null
    val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val bundle = intent.extras

        if (bundle != null) {
            section_id = bundle.getString("section_id")!!
        }

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
                    sendMessage(reply_to = -1, text = editTextMessage.text.toString())
                }
            }

            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutActivityMessages)
            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            setDatetime(value = "", section = section_id)

            swipeRefreshLayout.setOnRefreshListener {
                getAllMessages(startTask = false)
            }
            getAllMessages(startTask = true)
        } else {
            //no logged
            onBackPressed()
        }

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
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

    fun sendMessage(reply_to: Int = -1, text: String) {
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
                    editTextMessage.setText("")

                    getAllMessages(startTask = false)
                }
            }

            override fun onFailure(call: Call<PostMessageResponseList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())

                editTextMessage.isEnabled = true
                buttonSend.isEnabled = true
                editTextMessage.setText("")
            }

        })
    }

    fun getAllMessages(startTask: Boolean = false) {
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

                    if (getVariable("datetime_$section_id") != currentDatetime) {
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
            }

            override fun onFailure(call: Call<AllMessagesList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
                editTextMessage.isEnabled = true
                buttonSend.isEnabled = true
                editTextMessage.setText("")
            }

        })

        if (startTask) {
            currentRunnable = Runnable { getAllMessages(startTask = true) }
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

    override fun onDestroy() {
        if (currentRunnable != null) mainHandler.removeCallbacks(currentRunnable!!)
        super.onDestroy()
    }

    fun setupRecyclerView(
        clear: Boolean = false,
        messages: List<AllMessagesItemsList>?
    ) {
        val messagesItemsList: RecyclerView = findViewById(R.id.messages_conversation_items_list)
        val noMessagesAvailableText: TextView = findViewById(R.id.no_messages_available_here_text)
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
            messagesItemsList.smoothScrollToPosition(itemAdapter.itemCount - 1);
        } else {
            messagesItemsList.visibility = View.GONE
            noMessagesAvailableText.isGone = false
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
        getSharedPreferences("QuizNuotoPreferences", Context.MODE_PRIVATE).edit()
            .putString("datetime_" + section, value).apply()
    }

    private fun setVariable(variable: String, value: String?) {
        getSharedPreferences("QuizNuotoPreferences", Context.MODE_PRIVATE).edit()
            .putString(variable, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return getSharedPreferences(
            "QuizNuotoPreferences",
            Context.MODE_PRIVATE
        ).getString(variable, null)
    }
}