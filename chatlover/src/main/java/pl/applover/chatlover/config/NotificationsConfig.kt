package pl.applover.chatlover.config

import android.app.Activity
import android.app.Notification
import pl.applover.chatlover.model.Channel
import pl.applover.chatlover.model.ChatUser
import pl.applover.chatlover.model.Message

/**
 * Created by sp0rk on 15/08/17.
 */
object NotificationsConfig {
    var notificationTarget: Class<out Activity>? = null
    var areNotificationsEnabled: Boolean? = null
    var notificationCreator: ((title: String?, body: String?, message: Message,
                               channel: Channel, sender: ChatUser) -> Notification)? = null
    var onTokenRefresh: (() -> Unit)? = null

}