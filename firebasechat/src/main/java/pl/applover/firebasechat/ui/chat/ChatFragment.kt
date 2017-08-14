package pl.applover.firebasechat.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chat.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Message

/**
 * Created by sp0rk on 11/08/17.
 */
class ChatFragment : Fragment() {
    var listener: ChatListener? = null
    val recyclerView: RecyclerView by lazy { chat_recycler_view }
    val input: EditText by lazy { chat_input_field }
    val send: ImageView by lazy { chat_send_btn }
    val channelId by lazy { arguments.getString("channelId") }
    val currentUserId by lazy { arguments.getString("currentUserId") }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_chat, container, false)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MessageHolder.getAdapter(channelId, currentUserId)

        send.setOnClickListener {
            val db = FirebaseDatabase.getInstance().reference
            with(db.child("channels").child(channelId).child("messages").push()) {
                ref.setValue(Message(key, currentUserId, System.currentTimeMillis(), input.text.toString()))
            }
        }
    }

    fun withListener(listener: ChatListener) = this.also { this.listener = listener }

    companion object {
        fun newInstance(channelId: String, currentUserId: String): ChatFragment {
            val fragment = ChatFragment()
            with(Bundle()) {
                putString("channelId", channelId)
                putString("currentUserId", currentUserId)
                fragment.arguments = this
            }
            return fragment
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (recyclerView.adapter as? FirebaseRecyclerAdapter<*,*>)?.cleanup()
    }

    interface ChatListener
}