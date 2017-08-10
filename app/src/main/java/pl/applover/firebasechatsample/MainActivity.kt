package pl.applover.firebasechatsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.applover.firebasechat.ui.ChannelListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, ChannelListFragment.newInstance())
                .commit()
    }
}
