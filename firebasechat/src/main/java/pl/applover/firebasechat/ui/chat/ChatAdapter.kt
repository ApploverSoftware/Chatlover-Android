package pl.applover.firebasechat.ui.chat

import android.graphics.PorterDuff
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_day_header.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.Message
import pl.applover.firebasechat.ui.HeaderedFirebaseAdapter
import pl.applover.firebasechat.ui.chat.ChatAdapter.DayHeaderHolder
import pl.applover.firebasechat.ui.chat.ChatAdapter.MessageHolder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sp0rk on 18/08/17.
 */
class ChatAdapter(val channel: Channel,
                  val currentUserId: String,
                  val layoutManager: LinearLayoutManager,
                  val recyclerView: RecyclerView)
    : HeaderedFirebaseAdapter<MessageHolder, DayHeaderHolder, Message>(
        FirebaseDatabase.getInstance().reference.child("channels").child(channel.id).child("messages"),
        Message::class.java, MessageHolder::class.java, DayHeaderHolder::class.java,
        R.layout.item_message, R.layout.item_day_header,
        createDayHeaderDecider()
) {

    override fun populateItem(holder: MessageHolder, previous: Message?, model: Message, next: Message?, position: Int) {
        holder.bind(model, channel, position, currentUserId == model.sender)
    }
    override fun populateHeader(holder: DayHeaderHolder, previous: Message?, next: Message?, position: Int) {
        holder.bind(createDayHeaderDecider().getHeader(previous,next)?:"New messages")
    }

    fun withAutoscroll() = this.also {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val messageCount = getItemCount()
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1
                        || positionStart >= messageCount - 1
                        && lastVisiblePosition == positionStart - 1) {
                    recyclerView.scrollToPosition(positionStart)
                }
            }
        })
    }

    companion object {
        fun createDayHeaderDecider() = object : HeaderDecider<Message> {
            override fun getHeader(previous: Message?, next: Message?): String? {
                val p = previous?.calendar!!
                val n = next?.calendar!!
                val sameYear = p.get(Calendar.YEAR) == n.get(Calendar.YEAR)
                if (n.get(Calendar.DAY_OF_YEAR) == p.get(Calendar.DAY_OF_YEAR)) return null
                else return SimpleDateFormat("EEE, d MMM ${if (sameYear) "" else "''yy"}",
                        Locale.getDefault()).format(n.time)
            }
        }
    }

    class MessageHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val body = itemView?.item_message_body
        val bubble = itemView?.item_message_bubble
        val lr = itemView?.item_message_lr
        val avatar = itemView?.item_message_avatar
        val time = itemView?.item_message_time

        fun bind(message: Message, channel: Channel, position: Int, isOwnMsg: Boolean) {
            body?.text = message.body

            var label = "${SimpleDateFormat("EEE HH:mm", Locale.getDefault()).format(message.time)}"
            channel.users[message.sender]?.let {
                label+=", ${it.name}"
            }
            time?.text = label
            setType(isOwnMsg)
        }

        private fun setType(isOwnMsg: Boolean) {
            if (isOwnMsg) {
                avatar?.visibility = View.GONE
                lr?.gravity = Gravity.END
                bubble?.background?.setColorFilter(bubble.resources.getColor(R.color.chat_item_bubble_own), PorterDuff.Mode.SRC_IN)
            } else {
                avatar?.visibility = View.VISIBLE
                Glide.with(avatar?.context)
                        .load(R.drawable.avatar_placeholder)
                        .into(avatar)
                lr?.gravity = Gravity.START
                bubble?.background?.setColorFilter(bubble.resources.getColor(R.color.chat_item_bubble_other), PorterDuff.Mode.SRC_IN)
            }

        }
    }

    class DayHeaderHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val label = itemView?.day_header_label
        fun bind(text: String) {
            label?.text = text
        }
    }
}