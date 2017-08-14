package pl.applover.firebasechat.ui.chat

import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
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
    val bubble = itemView?.item_message_bubble

    fun bind(message: Message, position: Int, isOwnMsg: Boolean) {
        body?.text = message.body
        setType(isOwnMsg)
    }

    private fun setType(isOwnMsg: Boolean) {
        with(bubble!!){
            val p = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            if(isOwnMsg) {
                p.gravity = Gravity.END
                background.setColorFilter(resources.getColor(R.color.chat_item_bubble_own), PorterDuff.Mode.SRC_IN)
            } else {
                p.gravity = Gravity.START
                background.setColorFilter(resources.getColor(R.color.chat_item_bubble_other), PorterDuff.Mode.SRC_IN)
            }
            layoutParams = p
        }
    }

    companion object {
        fun getAdapter(channelId: String, currentUserId: String) = object : FirebaseRecyclerAdapter<Message, MessageHolder>(
                Message::class.java,
                R.layout.item_message,
                MessageHolder::class.java,
                FirebaseDatabase.getInstance().reference.child("channels").child(channelId).child("messages")) {
            override fun populateViewHolder(viewHolder: MessageHolder?, model: Message?, position: Int) {
                model?.let { viewHolder?.bind(model, position, currentUserId == model.sender) }
            }
        }
    }
}