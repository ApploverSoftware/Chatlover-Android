package pl.applover.firebasechatsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.applover.firebasechat.ui.channel_list.ChannelListFragment
import pl.applover.firebasechat.ui.channel_list.ChannelListFragment.ChannelListListener
import pl.applover.firebasechat.ui.chat.ChatFragment
import pl.applover.firebasechat.ui.chat.ChatFragment.ChatListener

class MainActivity : AppCompatActivity(), ChannelListListener, ChatListener {
    override fun onChatRequested(channelId: String) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, ChatFragment.newInstance(channelId).withListener(this))
                .addToBackStack("asdads")
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, ChannelListFragment.newInstance().withListener(this))
                .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount>0)
            supportFragmentManager.popBackStack()
    }
}
