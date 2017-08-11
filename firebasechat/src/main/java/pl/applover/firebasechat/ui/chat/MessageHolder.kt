package pl.applover.firebasechat.ui.chat

import android.support.v7.widget.RecyclerView
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_message.view.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Message

/**
 * Created by sp0rk on 11/08/17.
 */
class MessageHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val body = itemView?.item_message_body
    fun bind(message: Message, position: Int) {
        body?.text = message.body
    }

    companion object {
        fun getAdapter(channelId: String) = object : FirebaseRecyclerAdapter<Message, MessageHolder>(
                Message::class.java,
                R.layout.item_message,
                MessageHolder::class.java,
                FirebaseDatabase.getInstance().reference.child("channels").child(channelId).child("messages")) {
            override fun populateViewHolder(viewHolder: MessageHolder?, model: Message?, position: Int) {
                model?.let { viewHolder?.bind(model, position) }
            }
        }
    }
}