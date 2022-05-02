package com.saverio.finapp.ui.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.widget.NestedScrollView
import com.saverio.finapp.R
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.PostResponseList
import com.saverio.finapp.api.statistics.StatisticsList
import com.saverio.finapp.api.users.SignupPostList
import com.saverio.finapp.api.users.UsersList
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.StatisticsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.util.*

class ProfileActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
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
        val logoutButton: Button = findViewById(R.id.buttonLogoutProfile)

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
            loadUserDetails()
        } else {
            noLoggedConstraintLayout.isGone = false
        }

        logoutButton.setOnClickListener {
            setVariable("userid", null)
            val databaseHandler = DatabaseHandler(this)
            databaseHandler.deleteAllStatistics()
            databaseHandler.close()

            noLoggedConstraintLayout.isGone = false
            loginConstraintLayout.isGone = true
            signupConstraintLayout.isGone = true
            loggedConstraintLayout.isGone = true
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

            val buttonLoginSectionLoginProfile: Button =
                findViewById(R.id.buttonLoginSectionLoginProfile)
            buttonLoginSectionLoginProfile.setOnClickListener {
                val username_or_email =
                    findViewById<EditText>(R.id.editTextProfileLoginUsernameOrEmail)
                val password = findViewById<EditText>(R.id.editTextProfileLoginPassword)

                if (username_or_email.text.toString() != "" && password.text.toString() != "") {
                    login(
                        user = username_or_email.text.toString(),
                        password = password.text.toString()
                    )
                } else {
                    if (username_or_email.text.toString() == "") username_or_email.setError(
                        getString(R.string.cannot_be_empty_text)
                    )
                    if (password.text.toString() == "") password.setError(getString(R.string.cannot_be_empty_text))
                }
            }

            val buttonResetPasswordSectionLoginProfileResetPassword: Button =
                findViewById(R.id.buttonResetPasswordSectionLoginProfileResetPassword)
            buttonResetPasswordSectionLoginProfileResetPassword.setOnClickListener {
                val username_or_email =
                    findViewById<EditText>(R.id.editTextProfileLoginResetPasswordUsernameOrEmail)
                val new_password =
                    findViewById<EditText>(R.id.editTextProfileLoginResetPasswordNewPassword)

                if (new_password.text.toString() != "" && username_or_email.text.toString() != "") {
                    resetPassword(
                        user = username_or_email.text.toString(),
                        password = new_password.text.toString()
                    )
                } else {
                    if (username_or_email.text.toString() == "") username_or_email.setError(
                        getString(R.string.cannot_be_empty_text)
                    )
                    if (new_password.text.toString() == "") new_password.setError(getString(R.string.cannot_be_empty_text))
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

            val born = findViewById<TextView>(R.id.textViewProfileSignupBorn)
            val today = Calendar.getInstance()
            born.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        born.setText(
                            "$year-$month-$dayOfMonth"
                        )
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                datePickerDialog.show()
            }

            val buttonSignupSectionSignupProfile: Button =
                findViewById(R.id.buttonSignupSectionSignupProfile)
            buttonSignupSectionSignupProfile.setOnClickListener {
                val name = findViewById<EditText>(R.id.textViewProfileSignupName)
                val surname = findViewById<EditText>(R.id.textViewProfileSignupSurname)
                val username = findViewById<EditText>(R.id.textViewProfileSignupUsername)
                val email = findViewById<EditText>(R.id.textViewProfileSignupEmail)
                val password = findViewById<EditText>(R.id.textViewProfileSignupPassword)
                val repeat_password =
                    findViewById<EditText>(R.id.textViewProfileSignupRepeatPassword)

                if (name.text.toString() != "" && surname.text.toString() != "" && username.text.toString() != "" && password.text.toString() != "" && password.text.toString() == repeat_password.text.toString() && born.text.toString() != "") {
                    createAccount(
                        SignupPostList(
                            username = username.text.toString(),
                            name = name.text.toString(),
                            surname = surname.text.toString(),
                            email = email.text.toString(),
                            password = password.text.toString(),
                            born = born.text.toString()
                        )
                    )
                } else {
                    if (name.text.toString() == "") name.setError(getString(R.string.cannot_be_empty_text))
                    if (surname.text.toString() == "") surname.setError(getString(R.string.cannot_be_empty_text))
                    if (username.text.toString() == "") username.setError(getString(R.string.cannot_be_empty_text))
                    if (email.text.toString() == "") email.setError(getString(R.string.cannot_be_empty_text))
                    if (password.text.toString() == "") password.setError(getString(R.string.cannot_be_empty_text))
                    if (repeat_password.text.toString() == "") repeat_password.setError(getString(R.string.cannot_be_empty_text))
                    if (born.text.toString() == "") born.setError(getString(R.string.cannot_be_empty_text))

                    if (password.text.toString() != repeat_password.text.toString()) {
                        password.setError(getString(R.string.password_should_match_text))
                        repeat_password.setError(getString(R.string.password_should_match_text))
                    }
                }
            }
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
            //from noLogged or Logged
            finish()
            super.onBackPressed()
        } else {
            //or Login or Signup

            val loginCardView: CardView =
                findViewById(R.id.cardViewProfileLogin)
            val resetPasswordCardView: CardView =
                findViewById(R.id.cardViewProfileLoginResetPassword)

            if (!loginConstraintLayout.isGone && !resetPasswordCardView.isGone) {
                //from reset password
                loginCardView.isGone = false
                resetPasswordCardView.isGone = true
            } else {
                //from signup or login
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

    fun createAccount(user: SignupPostList) {
        val username = findViewById<EditText>(R.id.textViewProfileSignupUsername)
        val email = findViewById<EditText>(R.id.textViewProfileSignupEmail)

        val call: Call<PostResponseList> =
            ApiClient.client.signupUserInfo(
                username = user.username,
                name = user.name,
                surname = user.surname,
                email = user.email,
                password = user.password,
                born = user.born
            )
        call.enqueue(object : Callback<PostResponseList> {

            override fun onResponse(
                call: Call<PostResponseList>?,
                response: Response<PostResponseList>?
            ) {
                println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val responseList = response.body()!!

                    if (responseList.code == 200) {
                        println("${responseList.code} || ${responseList.description} || ${responseList.userid}")

                        //everything ok
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.user_signed_up_correctly_toast),
                            Toast.LENGTH_LONG
                        ).show()

                        onBackPressed()
                    } else {
                        if (responseList.code == 401) username.setError(getString(R.string.username_already_taken_text))
                        else if (responseList.code == 403) email.setError(getString(R.string.email_already_taken_text))
                        Log.v("Error", responseList.description)
                    }
                }
            }

            override fun onFailure(call: Call<PostResponseList>?, t: Throwable?) {
                Log.v("Error", t.toString())
            }

        })
    }

    fun login(user: String, password: String) {
        val call: Call<PostResponseList> =
            ApiClient.client.loginUserInfo(
                username_or_email = user,
                password = password
            )
        call.enqueue(object : Callback<PostResponseList> {

            override fun onResponse(
                call: Call<PostResponseList>?,
                response: Response<PostResponseList>?
            ) {
                println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val responseList = response.body()!!

                    if (responseList.code == 200) {
                        //println("${responseList.code} || ${responseList.description} || ${responseList.userid}")

                        setUseridLogged(responseList.userid!!)
                        loadUserDetails()
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.user_logged_in_successfully_toast),
                            Toast.LENGTH_LONG
                        ).show()

                        checkStatistics()

                        onBackPressed()
                    } else {
                        val username_or_email: EditText =
                            findViewById(R.id.editTextProfileLoginUsernameOrEmail)
                        val password: EditText = findViewById(R.id.editTextProfileLoginPassword)
                        if (responseList.code == 402) {
                            username_or_email.setError(getString(R.string.username_or_email_or_password_are_wrong_text))
                            password.setError(getString(R.string.username_or_email_or_password_are_wrong_text))
                        }
                        Log.v("Error", responseList.description)
                    }
                }
            }

            override fun onFailure(call: Call<PostResponseList>?, t: Throwable?) {
                Log.v("Error", t.toString())
            }

        })
    }

    fun resetPassword(user: String, password: String) {
        val call: Call<PostResponseList> =
            ApiClient.client.resetPasswordUserInfo(
                username_or_email = user,
                password = password
            )
        call.enqueue(object : Callback<PostResponseList> {

            override fun onResponse(
                call: Call<PostResponseList>?,
                response: Response<PostResponseList>?
            ) {
                println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val responseList = response.body()!!

                    if (responseList.code == 200) {
                        //println("${responseList.code} || ${responseList.description} || ${responseList.userid}")

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.password_changed_correctly_toast),
                            Toast.LENGTH_LONG
                        ).show()

                        onBackPressed()
                    } else {
                        Log.v("Error", responseList.description)
                    }
                }
            }

            override fun onFailure(call: Call<PostResponseList>?, t: Throwable?) {
                Log.v("Error", t.toString())
            }

        })
    }

    fun loadUserDetails() {
        val cardViewImage: CardView = findViewById(R.id.cardViewProfileLoggedImage)
        val cardViewName: CardView = findViewById(R.id.cardViewProfileLoggedName)
        val cardViewSurname: CardView = findViewById(R.id.cardViewProfileLoggedSurname)
        val cardViewUsername: CardView = findViewById(R.id.cardViewProfileLoggedUsername)
        val cardViewEmail: CardView = findViewById(R.id.cardViewProfileLoggedEmail)
        val cardViewBorn: CardView = findViewById(R.id.cardViewProfileLoggedBorn)

        val textViewName: TextView = findViewById(R.id.textViewProfileLoggedName)
        val textViewSurname: TextView = findViewById(R.id.textViewProfileLoggedSurname)
        val textViewUsername: TextView = findViewById(R.id.textViewProfileLoggedUsername)
        val textViewEmail: TextView = findViewById(R.id.textViewProfileLoggedEmail)
        val textViewBorn: TextView = findViewById(R.id.textViewProfileLoggedBorn)

        val imageViewProfile: ImageView = findViewById(R.id.imageViewProfileImage)

        val call: Call<UsersList> =
            ApiClient.client.getUserDetailsInfo(
                userid = getUserid()
            )
        call.enqueue(object : Callback<UsersList> {

            override fun onResponse(
                call: Call<UsersList>?,
                response: Response<UsersList>?
            ) {
                println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val responseList = response.body()!!

                    if (responseList.code == 200) {
                        //println("${responseList.code} || ${responseList.description} || ${responseList.userid}")
                        val imageUrl =
                            "https://www.gravatar.com/avatar/${responseList.user_details.email.toMD5()}?s=1000&r=g"
                        DownloadImageFromInternet(imageViewProfile).execute(imageUrl)
                        cardViewImage.isGone = false
                        textViewName.text = responseList.user_details.name
                        cardViewName.isGone = false
                        textViewSurname.text = responseList.user_details.surname
                        cardViewSurname.isGone = false
                        textViewUsername.text = responseList.user_details.username
                        cardViewUsername.isGone = false
                        textViewEmail.text = responseList.user_details.email
                        cardViewEmail.isGone = false
                        textViewBorn.text = responseList.user_details.born
                        cardViewBorn.isGone = false
                    } else {
                        Log.v("Error", responseList.description)
                    }
                }
            }

            override fun onFailure(call: Call<UsersList>?, t: Throwable?) {
                Log.v("Error", t.toString())
            }

        })
    }

    fun String.toMD5(): String {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.toHex()
    }

    fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

    private inner class DownloadImageFromInternet(var imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {
        init {
            //initial phase of download image
            imageView.isGone = true
        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            println(imageURL)
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                imageView.isGone = true
            }
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
            imageView.isGone = false
        }
    }

    fun checkStatistics() {
        //check new data from server
        if (checkLogged()) {
            //logged

            val call: Call<StatisticsList> =
                ApiClient.client.getStatisticsInfo(userid = getUserid())
            call.enqueue(object : Callback<StatisticsList> {

                override fun onResponse(
                    call: Call<StatisticsList>?,
                    response: Response<StatisticsList>?
                ) {
                    //println("Response:\n" + response!!.body()!!)

                    if (response!!.isSuccessful && response.body() != null) {
                        val responseList = response.body()!!

                        if (responseList != null) {
                            val databaseHandler = DatabaseHandler(this@ProfileActivity)

                            responseList.statistics!!.forEach {
                                val statistics = StatisticsModel(
                                    id = databaseHandler.getNewIdStatistics(),
                                    type = it.type,
                                    question_id = it.question_id,
                                    datetime = it.datetime,
                                    correct_answer = it.correct_answer,
                                    user_answer = it.user_answer,
                                    milliseconds = it.milliseconds
                                )
                                if (databaseHandler.checkStatistics(it.question_id, it.type)) {
                                    //update
                                    databaseHandler.updateStatistics(statistics)
                                } else {
                                    //insert
                                    databaseHandler.addStatistics(statistics)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<StatisticsList>?, t: Throwable?) {
                    //progerssProgressDialog.dismiss()
                    Log.v("Error", t.toString())
                }

            })
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