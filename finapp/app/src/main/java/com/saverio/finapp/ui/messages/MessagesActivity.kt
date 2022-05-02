package com.saverio.finapp.ui.messages

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.saverio.finapp.R


class MessagesActivity : AppCompatActivity() {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    var currentRunnable: Runnable? = null
    val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        val buttonSend: Button = findViewById(R.id.buttonSendMessage)
        val editTextMessage: EditText = findViewById(R.id.editTextMessageText)
        editTextMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (editTextMessage.text.toString().replace(" ", "")
                        .replace("\n", "") != ""
                ) buttonSend.isGone = false
                else buttonSend.isGone = true
            }
        })

        buttonSend.setOnClickListener {
            //send message action
            if (editTextMessage.text.toString().replace(" ", "").replace("\n", "") != "") {

                editTextMessage.setText("")
            }
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutActivityMessages)
        swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )

        swipeRefreshLayout.setOnRefreshListener {
            getAllMessages()
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

    fun getAllMessages() {
        swipeRefreshLayout.isRefreshing = false

        //swipeRefreshLayout.isRefreshing=false

        if (currentRunnable == null) {
            currentRunnable = Runnable { getAllMessages() }
            mainHandler.postDelayed(
                currentRunnable!!,
                60000
            ) //every 1 minutes (1000 milliseconds * 1 minute (->60 seconds) = 60000)
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
}