package pl.applover.firebasechat.services

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import pl.applover.firebasechat.model.ChatUser

/**
 * Created by sp0rk on 15/08/17.
 */
class FirebaseChatInstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        ChatUser.refreshCurrentToken()
    }
}