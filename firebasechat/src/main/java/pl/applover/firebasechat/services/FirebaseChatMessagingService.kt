package pl.applover.firebasechat.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.json.JSONObject
import pl.applover.firebasechat.R
import pl.applover.firebasechat.config.NotificationsConfig
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.ChatUser
import pl.applover.firebasechat.model.Message

/**
 * Created by sp0rk on 15/08/17.
 */
class FirebaseChatMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        handleChatMessage(remoteMessage, this)
    }

    companion object {
        fun handleChatMessage(remoteMessage: RemoteMessage?, context: Context) {
            if (NotificationsConfig.areNotificationsEnabled ?: true) {
                val data = JSONObject(remoteMessage!!.data)

                val intent = Intent(context, NotificationsConfig.notificationTarget)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(context, 1410,
                        intent, PendingIntent.FLAG_ONE_SHOT)

                val notification = NotificationsConfig.notificationCreator?.invoke(
                        remoteMessage.notification.title,
                        remoteMessage.notification.body,
                        Gson().fromJson(data.getString("message"), Message::class.java),
                        Gson().fromJson(data.getString("channel"), Channel::class.java),
                        Gson().fromJson(data.getString("sender"), ChatUser::class.java)
                ) ?: NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_location)
                        .setContentTitle(remoteMessage.notification.title)
                        .setContentText(remoteMessage.notification.body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent).build()

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.notify(1410, notification)
            }
        }
    }
}