package pl.applover.firebasechat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by sp0rk on 10/08/17.
 */
class ChannelListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_channel_list, container, false)
    }

    companion object {
        fun newInstance() = ChannelListFragment()
    }

}