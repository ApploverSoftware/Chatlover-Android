package pl.applover.chatlover.services

import com.google.firebase.iid.FirebaseInstanceIdService
import pl.applover.chatlover.config.NotificationsConfig
import pl.applover.chatlover.model.ChatUser

/**
 * Created by sp0rk on 15/08/17.
 */
class ChatloverInstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        NotificationsConfig.onTokenRefresh?.invoke() ?: ChatUser.refreshCurrentToken()
    }
}