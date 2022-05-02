package com.saverio.finapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.ApiClient.BASE_URL
import com.saverio.finapp.api.ApiInterface
import com.saverio.finapp.api.messages.AllMessagesList
import com.saverio.finapp.api.messages.MessagesSectionsList
import com.saverio.finapp.db.DatabaseHandler
import com.saverio.finapp.db.SectionsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LoadMessages {
    lateinit var notificationReceiver: NotificationReceiver

    lateinit var globalContext: Context

    fun loadSections(context: Context, notificationReceiver: NotificationReceiver? = null) {
        globalContext = context
        val call: Call<MessagesSectionsList> =
            ApiClient.client.getUserMessagesSectionsInfo(userid = getUserid())
        call.enqueue(object : Callback<MessagesSectionsList> {

            override fun onResponse(
                call: Call<MessagesSectionsList>?,
                response: Response<MessagesSectionsList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val responseList = response.body()!!

                    var sectionsJoined = ArrayList<String>()
                    responseList.sections?.forEach {
                        sectionsJoined.add(it.section)
                        checkMessages(it.section)
                    }
                }
            }

            override fun onFailure(call: Call<MessagesSectionsList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
            }
        })
    }

    fun checkMessages(section: String) {
        val call: Call<AllMessagesList> =
            ApiClient.client.getAllMessagesInfo(section = section)
        call.enqueue(object : Callback<AllMessagesList> {

            override fun onResponse(
                call: Call<AllMessagesList>?,
                response: Response<AllMessagesList>?
            ) {
                //println("Response:\n" + response!!.body()!!)

                if (response!!.isSuccessful && response.body() != null) {
                    val responseList = response.body()!!

                    val currentDatetime = responseList.lastUpdate?.datetime.toString()

                    if (getVariable("datetime_$section") != currentDatetime) {
                        setDatetime(
                            value = currentDatetime,
                            section = section
                        )
                        //get notification number
                        var notificationNumber = globalContext.getSharedPreferences(
                            "notificationNumber",
                            Context.MODE_PRIVATE
                        ).getInt("notificationNumber", 0)

                        //send the push notification
                        notificationReceiver.sendNow(
                            title = globalContext.getString(R.string.new_message_notification),
                            text = globalContext.getString(R.string.open_the_app_to_read_message_notification),
                            number = notificationNumber
                        )
                    }
                }
            }

            override fun onFailure(call: Call<AllMessagesList>?, t: Throwable?) {
                //progerssProgressDialog.dismiss()
                Log.v("Error", t.toString())
            }
        })
    }

    fun setDatetime(value: String, section: String) {
        globalContext.getSharedPreferences("QuizNuotoPreferences", Context.MODE_PRIVATE).edit()
            .putString("datetime_" + section, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return globalContext.getSharedPreferences(
            "QuizNuotoPreferences",
            Context.MODE_PRIVATE
        ).getString(variable, null)
    }

    fun checkLogged(): Boolean {
        return (getVariable("userid") != "" && getVariable("userid") != null)
    }

    fun getUserid(): String {
        return (if (checkLogged()) getVariable("userid")!! else "")
    }
}