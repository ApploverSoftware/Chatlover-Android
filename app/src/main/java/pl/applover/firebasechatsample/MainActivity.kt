package pl.applover.firebasechatsample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ResultCodes
import com.google.firebase.auth.FirebaseAuth
import pl.applover.firebasechat.config.NotificationsConfig
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.ChatUser
import pl.applover.firebasechat.ui.channel_list.ChannelListFragment
import pl.applover.firebasechat.ui.channel_list.ChannelListFragment.ChannelListListener
import pl.applover.firebasechat.ui.chat.ChatFragment
import pl.applover.firebasechat.ui.chat.ChatFragment.ChatListener


class MainActivity : AppCompatActivity(), ChannelListListener, ChatListener {
    override fun onChatRequested(channel: Channel) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, ChatFragment.newInstance(channel, ChatUser.current?.uid ?: "").withListener(this))
                .addToBackStack("asdads")
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        config()
        setContentView(R.layout.activity_main)
        init()
    }

    private fun config() {
        NotificationsConfig.notificationTarget = MainActivity::class.java
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            AuthUI.getInstance().signOut(this).addOnCompleteListener {
                finish()
            }
    }

    fun init() {
        FirebaseAuth.getInstance().currentUser?.let {
            ChatUser.loginWithUid(it.uid, it.displayName!!) {
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.container, ChannelListFragment.newInstance().withListener(this))
                        .commit()
            }
        } ?: startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(false).build(), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == ResultCodes.OK) {
                FirebaseAuth.getInstance().currentUser?.let {
                    ChatUser.loginWithUid(it.uid, it.displayName!!) {
                        init()
                    }
                }
            } else {
                Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT).show()
                init()
            }
        }
    }
}
