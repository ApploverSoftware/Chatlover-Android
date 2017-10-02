package pl.applover.firebasechat.ui.channel_list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import pl.applover.firebasechat.R
import pl.applover.firebasechat.config.ChannelListConfig
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.ui.channel_list.ChannelAdapter.ChannelHolder.OnChannelClickListener

/**
 * Created by sp0rk on 10/08/17.
 */
class ChannelListFragment : Fragment(), OnChannelClickListener {
    var listener: ChannelListListener? = null
    lateinit var recyclerView: RecyclerView
    private var channelId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelId = arguments.getString("channelId")
        arguments.remove("channelId")
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_channel_list, container, false)
        recyclerView = root!!.findViewById<RecyclerView>(R.id.list_recycler_view)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ChannelAdapter(this)

        ChannelListConfig.onFragmentViewCreated?.invoke(view)

        if (channelId?.isNotEmpty()?:false) {
            Channel.provideChannel(channelId!!) {
                if (it != null) listener?.onChatRequested(it)
                channelId = null
            }
        }
    }

    override fun onClick(channel: Channel) {
        listener?.onChatRequested(channel)
    }

    fun withListener(listener: ChannelListListener) = this.also { this.listener = listener }

    companion object {
        fun newInstance(channelId: String? = null): ChannelListFragment {
            val fragment = ChannelListFragment()
            with(Bundle()) {
                putString("channelId", channelId)
                fragment.arguments = this
            }
            return fragment
        }
    }

    interface ChannelListListener {
        fun onChatRequested(channel: Channel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (recyclerView.adapter as ChannelAdapter).destroy()
    }
}