package com.saverio.finapp.ui.theory

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saverio.finapp.MainActivity
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SectionsModel
import com.saverio.finapp.ui.messages.MessagesActivity
import com.saverio.finapp.ui.profile.ProfileActivity


class SectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        val bundle = intent.extras

        var chapterId: Int? = null
        var sectionId: String = ""

        val databaseHandler = DatabaseHandler(this)

        if (bundle != null) {
            chapterId = bundle.getInt("chapter_id")
            sectionId = bundle.getString("section_id")!!
        }
        val getSection = databaseHandler.getSection(section = sectionId)
        setupSection(chapter = chapterId, section = sectionId, getSection = getSection)

        val buttonStartConversation: Button = findViewById(R.id.buttonStartConversation)
        buttonStartConversation.setOnClickListener {
            if (!checkLogged()) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.no_logged_login_now_alert_dialog_title)
                builder.setMessage(R.string.no_logged_login_cancel_alert_dialog_text)
                builder.setCancelable(true)
                builder.setPositiveButton(
                    R.string.no_logged_login_now_alert_dialog_button,
                    DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.putExtra("source", "section")
                        startActivity(intent)
                    })
                builder.setNegativeButton(
                    R.string.cancel_alert_dialog_button,
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                builder.create().show() //create and show the alert dialog
            } else {
                val intent = Intent(this, MessagesActivity::class.java)
                intent.putExtra("section_id", sectionId)
                startActivity(intent)
            }
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

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun setupSection(chapter: Int? = null, section: String, getSection: SectionsModel) {
        val text: TextView = findViewById(R.id.textViewSectionTextDedicated)
        val title: TextView = findViewById(R.id.textViewSectionTitleDedicated)
        val author: TextView = findViewById(R.id.textViewSectionAuthorDedicated)
        val sectionTitle: TextView = findViewById(R.id.titleSectionDedicated)

        text.text = getSection.text
        title.text = getSection.title
        author.text = getString(R.string.author_text, getSection.author.toString())
        sectionTitle.text = getString(R.string.section_id_text, section)
    }

    override fun onResume() {
        //supportActionBar?.hide()
        super.onResume()
    }

    fun checkLogged(): Boolean {
        return (getVariable("userid") != "" && getVariable("userid") != null)
    }

    private fun getVariable(variable: String): String? {
        return getSharedPreferences(
            MainActivity.PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ).getString(variable, null)
    }
}