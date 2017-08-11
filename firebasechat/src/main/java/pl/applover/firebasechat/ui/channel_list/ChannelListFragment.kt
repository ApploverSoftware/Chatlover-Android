package pl.applover.firebasechat.ui.channel_list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_channel_list.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.Message
import pl.applover.firebasechat.ui.channel_list.ChannelHolder.OnChannelClickListener

/**
 * Created by sp0rk on 10/08/17.
 */
class ChannelListFragment : Fragment(), OnChannelClickListener {
    var listener: ChannelListListener? = null
    val recyclerView: RecyclerView by lazy { list_recycler_view }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_channel_list, container, false)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ChannelHolder.getAdapter(this)
    }

    override fun onClick(channelId: String) {
        listener?.onChatRequested(channelId)
    }

    fun withListener(listener: ChannelListListener) = this.also { this.listener = listener }

    companion object {
        fun newInstance() = ChannelListFragment()
    }

    interface ChannelListListener {
        fun onChatRequested(channelId: String)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (recyclerView.adapter as? FirebaseRecyclerAdapter<*, *>)?.cleanup()
    }
}