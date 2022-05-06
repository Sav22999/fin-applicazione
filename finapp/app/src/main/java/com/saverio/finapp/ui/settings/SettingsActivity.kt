package com.saverio.finapp.ui.settings

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.saverio.finapp.BuildConfig
import com.saverio.finapp.MainActivity
import com.saverio.finapp.MainActivity.Companion.NOTIFICATIONS
import com.saverio.finapp.MainActivity.Companion.PREFERENCES_NAME
import com.saverio.finapp.R
import com.saverio.finapp.db.DatabaseHandler

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val cardViewNotification: CardView = findViewById(R.id.cardViewSettingsNotifications)
        val switchNotification: Switch = findViewById(R.id.switchNotifications)
        cardViewNotification.setOnClickListener {
            switchNotification.performClick()
        }
        switchNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            setVariable("notifications", isChecked)
            println(isChecked)
        }
        switchNotification.isChecked = getVariable("notifications", true)

        val cardViewShowTutorialAgain: CardView =
            findViewById(R.id.cardViewSettingsShowAgainTutorial)
        cardViewShowTutorialAgain.setOnClickListener {
            setVariable(MainActivity.FIRST_RUN_APP, true)
            setVariable(MainActivity.FIRST_RUN_QUIZ, true)
            setVariable(MainActivity.FIRST_RUN_SIMULATION, true)
            setVariable(MainActivity.FIRST_RUN_MESSAGES, true)

            Toast.makeText(
                this,
                getString(R.string.shown_first_runs_again_settings_toast), Toast.LENGTH_LONG
            ).show()
        }

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val textViewReleaseBuildSettings: TextView = findViewById(R.id.textViewReleaseBuildSettings)
        textViewReleaseBuildSettings.text = getString(
            R.string.release_version_code_text_settings,
            versionName,
            versionCode.toString()
        )

        val cardViewDeleteData: CardView = findViewById(R.id.cardViewSettingsResetData)
        cardViewDeleteData.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.delete_all_device_data_title_settings)
            builder.setMessage(R.string.continue_delete_all_device_data_message_dialog_settings)
            builder.setCancelable(true)
            builder.setPositiveButton(
                R.string.continue_alert_dialog_button,
                DialogInterface.OnClickListener { dialog, which ->
                    //delete all preferences
                    getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit().clear()
                        .apply()
                    getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE).edit().clear().apply()
                    val databaseHandler = DatabaseHandler(this)
                    databaseHandler.deleteAllNews()
                    databaseHandler.deleteAllQuizzes()
                    databaseHandler.deleteAllSections()
                    databaseHandler.deleteAllStatistics()
                    databaseHandler.close()

                    setVariable("reset", true)

                    //close Settings
                    finish()
                })
            builder.setNegativeButton(
                R.string.cancel_alert_dialog_button,
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            builder.create().show() //create and show the alert dialog
        }

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            //show the back button in the action bar
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = getString(R.string.title_settings)
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

    private fun setVariable(variable: String, value: String?) {
        getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
            .putString(variable, value).apply()
    }

    private fun setVariable(variable: String, value: Boolean) {
        getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(variable, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ).getString(variable, null)
    }

    private fun getVariable(variable: String, default: Boolean): Boolean {
        return getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(
            variable,
            default
        )
    }
}