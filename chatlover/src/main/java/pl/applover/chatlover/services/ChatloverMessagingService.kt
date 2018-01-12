package pl.applover.chatlover.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject
import pl.applover.chatlover.R
import pl.applover.chatlover.config.NotificationsConfig
import pl.applover.chatlover.model.Channel
import pl.applover.chatlover.model.ChatUser
import pl.applover.chatlover.model.Message

/**
 * Created by sp0rk on 15/08/17.
 */
class ChatloverMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        handleChatMessage(remoteMessage, this)
    }

    companion object {
        fun handleChatMessage(remoteMessage: RemoteMessage?, context: Context) {
            if (NotificationsConfig.areNotificationsEnabled ?: true) {
                val data = JSONObject(remoteMessage!!.data)

                val channel = Gson().fromJson(data.getString("channel"), Channel::class.java)

                val intent = Intent(context.applicationContext, NotificationsConfig.notificationTarget)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.putExtra("channelIdToLaunch", channel.id)

                val pendingIntent = PendingIntent.getActivity(context.applicationContext, System.currentTimeMillis().toInt(),
                        intent, PendingIntent.FLAG_ONE_SHOT)

                val notification = NotificationsConfig.notificationCreator?.invoke(
                        remoteMessage.notification?.title,
                        remoteMessage.notification?.body,
                        Gson().fromJson(data.getString("message"), Message::class.java),
                        channel,
                        Gson().fromJson(data.getString("sender"), ChatUser::class.java)
                ) ?: NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_location)
                        .setContentTitle(remoteMessage.notification?.title)
                        .setContentText(remoteMessage.notification?.body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent).build()

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.notify(1410, notification)
            }
        }
    }
}