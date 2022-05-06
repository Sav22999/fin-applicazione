package com.saverio.finapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.saverio.finapp.R
import com.saverio.finapp.ui.messages.MessagesActivity

class NotificationReceiver : BroadcastReceiver() {

    lateinit var title: String
    lateinit var text: String

    lateinit var context: Context

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context

        //println("load messages notification")
        var loadMessages = LoadMessagesNotification()
        loadMessages.loadSections(context, notificationReceiver = this)
    }

    fun sendNow(title: String, text: String, number: Int, section: String) {
        this.title = title
        this.text = text
        //println("send now notification")
        sendNotification(context, title, text, true, number, section)
    }

    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        autoCancel: Boolean = true,
        notificationNumber: Int,
        section: String
    ) {
        println("trying to sending notification || section: $section")
        val NOTIFICATION_CHANNEL_ID =
            "${context.packageName.replace(".", "_")}_notification_${notificationNumber}"
        val NOTIFICATION_CHANNEL_NAME = "${context.packageName}_notification".replace(".", "_")
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

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
        intent.putExtra("source", "notifications")
        intent.putExtra("section_id", section)
        println("section_id: $section || notification: $NOTIFICATION_CHANNEL_ID")

        val requestCodeUnique: Int = (System.currentTimeMillis() and 0xfffffff).toInt()
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCodeUnique,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

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
                .setAutoCancel(autoCancel)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .addAction(
                    R.drawable.ic_forward,
                    context.getString(R.string.see_conversation_notification),
                    pendingIntent
                );

        notificationManager!!.notify(
            notificationNumber,
            notificationBuilder.build()
        )
        incrementNotificationNumber(context, notificationNumber)
        //println("notification should be sent")
    }

    fun incrementNotificationNumber(context: Context, notificationNumber: Int) {
        //increment notification number
        var notificationNumberTemp = notificationNumber + 1
        context.getSharedPreferences("notifications", Context.MODE_PRIVATE).edit()
            .putInt("notificationNumber", notificationNumberTemp).apply()
    }
}