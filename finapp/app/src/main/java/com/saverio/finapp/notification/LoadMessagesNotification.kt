package com.saverio.finapp.notification

import android.content.Context
import android.util.Log
import com.saverio.finapp.R
import com.saverio.finapp.api.ApiClient
import com.saverio.finapp.api.messages.AllMessagesList
import com.saverio.finapp.api.messages.MessagesSectionsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoadMessagesNotification {
    lateinit var notificationReceiver: NotificationReceiver

    lateinit var globalContext: Context

    fun loadSections(context: Context, notificationReceiver: NotificationReceiver? = null) {
        this.notificationReceiver = notificationReceiver!!

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

                    val currentDatetime =
                        responseList.lastUpdate?.datetime.toString()//global datetime
                    var currentSectionDatetime =
                        responseList.messages?.get(responseList.messages.size - 1)?.datetime//section datetime || we took the datetime of the last element
                    if (currentSectionDatetime == null) currentSectionDatetime = currentDatetime

                    println(
                        "section: $section || currentDate: ${currentSectionDatetime} || datetimeSaved: ${
                            getVariable("datetime_$section")
                        }"
                    )

                    if (getVariable("datetime_$section") != currentSectionDatetime) {
                        setDatetime(
                            value = currentSectionDatetime!!,
                            section = section
                        )
                        //get notification number
                        var notificationNumber = globalContext.getSharedPreferences(
                            "notifications",
                            Context.MODE_PRIVATE
                        ).getInt("notificationNumber", 0)

                        //send the push notification
                        if (notificationReceiver != null) {
                            val tempMessage = responseList.messages?.get(responseList.messages.size - 1)
                            notificationReceiver.sendNow(
                                title = globalContext.getString(
                                    R.string.new_message_notification,
                                    section
                                ),
                                text = globalContext.getString(
                                    R.string.preview_message_notification,
                                    tempMessage?.text
                                ),
                                number = notificationNumber,
                                section = section
                            )
                        }
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