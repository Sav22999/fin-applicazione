package com.saverio.finapp.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.widget.NestedScrollView
import com.saverio.finapp.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val bundle = intent.extras

        var source: String? = null

        if (bundle != null) {
            source = bundle.getString("source")
        }

        if (source != null && source == "messages") {
            //from "messages"
        }

        val loginButton: Button = findViewById(R.id.buttonLoginProfile)
        val signupButton: TextView = findViewById(R.id.sign_up_text)

        val titleProfile: TextView = findViewById(R.id.titleProfile)

        val noLoggedConstraintLayout: ConstraintLayout =
            findViewById(R.id.constraintLayoutProfileNoLogged)
        val loginConstraintLayout: ConstraintLayout =
            findViewById(R.id.constraintLayoutProfileLogin)
        val signupConstraintLayout: NestedScrollView =
            findViewById(R.id.nestedScrollViewProfileSignup)
        val loggedConstraintLayout: NestedScrollView =
            findViewById(R.id.nestedScrollViewProfileLoggedDetails)

        noLoggedConstraintLayout.isGone = true
        loginConstraintLayout.isGone = true
        signupConstraintLayout.isGone = true
        loggedConstraintLayout.isGone = true
        if (checkLogged()) {
            loggedConstraintLayout.isGone = false
        } else {
            noLoggedConstraintLayout.isGone = false
        }

        loginButton.setOnClickListener {
            //login
            titleProfile.text = getString(R.string.title_profile_login)

            noLoggedConstraintLayout.isGone = true
            loginConstraintLayout.isGone = false
            signupConstraintLayout.isGone = true
            loggedConstraintLayout.isGone = true

            val passwordForgottedText: TextView = findViewById(R.id.passwordForgottedText)
            passwordForgottedText.setOnClickListener {
                val loginCardView: CardView =
                    findViewById(R.id.cardViewProfileLogin)
                val resetPasswordCardView: CardView =
                    findViewById(R.id.cardViewProfileLoginResetPassword)
                resetPasswordCardView.isGone = false
                loginCardView.isGone = true

                val backResetPassword: Button =
                    findViewById(R.id.buttonGoBackProfileLoginResetPassword)
                backResetPassword.setOnClickListener {
                    resetPasswordCardView.isGone = true
                    loginCardView.isGone = false
                }
            }
        }

        signupButton.setOnClickListener {
            //signup
            titleProfile.text = getString(R.string.title_profile_signup)

            noLoggedConstraintLayout.isGone = true
            loginConstraintLayout.isGone = true
            signupConstraintLayout.isGone = false
            loggedConstraintLayout.isGone = true
        }

        /*
            noLoggedConstraintLayout.isGone = true
            loginConstraintLayout.isGone = true
            signupConstraintLayout.isGone = true
            loggedConstraintLayout.isGone = true
        */

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
        val noLoggedConstraintLayout: ConstraintLayout =
            findViewById(R.id.constraintLayoutProfileNoLogged)
        val loginConstraintLayout: ConstraintLayout =
            findViewById(R.id.constraintLayoutProfileLogin)
        val signupConstraintLayout: NestedScrollView =
            findViewById(R.id.nestedScrollViewProfileSignup)
        val loggedConstraintLayout: NestedScrollView =
            findViewById(R.id.nestedScrollViewProfileLoggedDetails)

        if (!noLoggedConstraintLayout.isGone || !loggedConstraintLayout.isGone) {
            finish()
            super.onBackPressed()
        } else {
            //or Login or Signup

            val loginCardView: CardView =
                findViewById(R.id.cardViewProfileLogin)
            val resetPasswordCardView: CardView =
                findViewById(R.id.cardViewProfileLoginResetPassword)

            if (!loginConstraintLayout.isGone && !resetPasswordCardView.isGone) {
                //reset password
                loginCardView.isGone = false
                resetPasswordCardView.isGone = true
            } else {
                //signup or login
                val titleProfile: TextView = findViewById(R.id.titleProfile)
                titleProfile.text = getString(R.string.title_profile)

                noLoggedConstraintLayout.isGone = true
                loginConstraintLayout.isGone = true
                signupConstraintLayout.isGone = true
                loggedConstraintLayout.isGone = true

                if (checkLogged()) {
                    loggedConstraintLayout.isGone = false
                } else {
                    noLoggedConstraintLayout.isGone = false
                }
            }
        }
    }

    fun checkLogged(): Boolean {
        return (getVariable("userid") != "" && getVariable("userid") != null)
    }

    fun setUseridLogged(userid: String) {
        setVariable("userid", userid)
    }

    fun getUserid(): String {
        return (if (checkLogged()) getVariable("userid")!! else "")
    }

    private fun setVariable(variable: String, value: String?) {
        getPreferences(MODE_PRIVATE).edit().putString(variable, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return getPreferences(MODE_PRIVATE).getString(variable, null)
    }
}