package pl.applover.firebasechat.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_channel_list.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.MockData

/**
 * Created by sp0rk on 10/08/17.
 */
class ChannelListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root =  inflater?.inflate(R.layout.fragment_channel_list, container, false)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_recycler_view.layoutManager = LinearLayoutManager(context)
        list_recycler_view.adapter = ChannelListAdapter(MockData.channels)
    }

    companion object {
        fun newInstance() = ChannelListFragment()
    }

}