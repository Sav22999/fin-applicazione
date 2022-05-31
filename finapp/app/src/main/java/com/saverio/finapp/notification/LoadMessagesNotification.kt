package com.saverio.finapp.notification

import android.content.Context
import android.util.Log
import com.saverio.finapp.MainActivity.Companion.NOTIFICATIONS
import com.saverio.finapp.MainActivity.Companion.PREFERENCES_NAME
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

                    /*println(
                        "section: $section || currentDate: ${currentSectionDatetime} || datetimeSaved: ${
                            getDatetime(
                                "datetime_$section"
                            )
                        }"
                    )*/
                    val tempMessage = responseList.messages?.get(responseList.messages.size - 1)
                    //println("tempMessage?.username = ${tempMessage?.username}")
                    if (getDatetime("datetime_$section") != currentSectionDatetime && getUsername() != tempMessage?.username) {
                        //send notification only if the saved date is different to the current date AND the username is not the same of login (so, it's not a message from the same user)
                        //println("^^^^ New message in section $section ^^^^")
                        setDatetime(
                            value = currentSectionDatetime!!,
                            section = section
                        )
                        //get notification number
                        val notificationNumber = globalContext.getSharedPreferences(
                            "notifications",
                            Context.MODE_PRIVATE
                        ).getInt("notificationNumber", 0)

                        //send the push notification
                        if (notificationReceiver != null && getVariable("notifications", true)) {
                            //println("Sending section $section notification")
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
                Log.v("Error", t.toString())
            }
        })
    }

    fun setDatetime(value: String, section: String) {
        globalContext.getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE).edit()
            .putString("datetime_" + section, value).apply()
    }

    private fun getVariable(variable: String): String? {
        return globalContext.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        ).getString(variable, null)
    }

    private fun getVariable(variable: String, default: Boolean): Boolean {
        return globalContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getBoolean(
                variable,
                default
            )
    }

    private fun getDatetime(variable: String): String? {
        return globalContext.getSharedPreferences(NOTIFICATIONS, Context.MODE_PRIVATE)
            .getString(variable, null)
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
}