package com.saverio.finapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.saverio.finapp.ui.messages.MessagesActivity
import java.util.*


class NotificationReceiver : BroadcastReceiver() {

    lateinit var title: String
    lateinit var text: String

    val hour_show = 10

    lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        var loadMessages = LoadMessages()
        loadMessages.loadSections(context, notificationReceiver = this)
    }

    fun sendNow(title: String, text: String, number: Int) {
        this.title = title
        this.text = text
        sendNotification(context, title, text, true, number)
    }

    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        autoCancel: Boolean = true,
        notificationNumber: Int
    ) {
        val NOTIFICATION_CHANNEL_ID =
            "${context.packageName.replace(".", "_")}_notification_${notificationNumber}"
        val NOTIFICATION_CHANNEL_NAME = "${context.packageName}_notification".replace(".", "_")
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(context, MessagesActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var notificationBuilder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_swimming_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setAutoCancel(autoCancel) //.setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val savedDate = getNotificationDate(context)

        val c = Calendar.getInstance()

        val currentDate =
            "${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH + 1)}-${c.get(Calendar.DAY_OF_MONTH)}"

        if (currentDate != savedDate || getSavedMessage(context) != title || getSavedMessage(context) == "") {
            notificationManager!!.notify(
                notificationNumber,
                notificationBuilder.build()
            )
            setNotificationDate(context, currentDate)
            incrementNotificationNumber(context, notificationNumber)
        } else {
            //Notification already sent
        }

        setSavedMessage(context, title)
    }

    fun getSavedMessage(context: Context): String? {
        return context.getSharedPreferences("messageNotification", Context.MODE_PRIVATE)
            .getString("messageNotification", "")
    }

    fun setSavedMessage(context: Context, word: String) {
        context.getSharedPreferences("messageNotification", Context.MODE_PRIVATE).edit()
            .putString("messageNotification", word).apply()
    }

    fun getNotificationDate(context: Context): String {
        return context.getSharedPreferences(
            "lastNotificationDate",
            Context.MODE_PRIVATE
        ).getString("lastNotificationDate", "").toString()
    }

    fun setNotificationDate(context: Context, currentDate: String) {
        context.getSharedPreferences("lastNotificationDate", Context.MODE_PRIVATE).edit()
            .putString("lastNotificationDate", currentDate).apply()
    }

    fun incrementNotificationNumber(context: Context, notificationNumber: Int) {
        //increment notification number
        var notificationNumberTemp = notificationNumber + 1
        context.getSharedPreferences(
            "notificationNumber",
            Context.MODE_PRIVATE
        ).edit()
            .putInt("notificationNumber", notificationNumberTemp).apply()
    }
}