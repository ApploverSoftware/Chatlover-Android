package pl.applover.firebasechat.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_chat.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.MockData

/**
 * Created by sp0rk on 11/08/17.
 */
class ChatFragment : Fragment() {
    var listener: ChatListener? = null
    val recyclerView: RecyclerView by lazy { chat_recycler_view }
    val messages by lazy {
        MockData.channels.find { it.id == arguments.getString("channelId") }?.messages
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_chat, container, false)
        Toast.makeText(context, arguments.getString("channelId"), Toast.LENGTH_SHORT).show()
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = ChatAdapter(messages)
    }


    fun withListener(listener: ChatListener) = this.also { this.listener = listener }

    companion object {
        fun newInstance(channelId: String): ChatFragment {
            val fragment = ChatFragment()
            with(Bundle()) {
                putString("channelId", channelId)
                fragment.arguments = this
            }
            return fragment
        }

    }

    interface ChatListener
}