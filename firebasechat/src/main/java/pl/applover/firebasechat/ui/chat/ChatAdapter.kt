package pl.applover.firebasechat.ui.chat

import android.graphics.PorterDuff
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.Gravity
import android.view.View
import android.widget.CalendarView
import android.widget.FrameLayout
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_day_header.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Message
import pl.applover.firebasechat.ui.HeaderedFirebaseAdapter
import pl.applover.firebasechat.ui.chat.ChatAdapter.DayHeaderHolder
import pl.applover.firebasechat.ui.chat.ChatAdapter.MessageHolder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sp0rk on 18/08/17.
 */
class ChatAdapter(channelId: String,
                  val currentUserId: String,
                  val layoutManager: LinearLayoutManager,
                  val recyclerView: RecyclerView)
    : HeaderedFirebaseAdapter<MessageHolder, DayHeaderHolder, Message>(
        FirebaseDatabase.getInstance().reference.child("channels").child(channelId).child("messages"),
        Message::class.java, MessageHolder::class.java, DayHeaderHolder::class.java,
        R.layout.item_message, R.layout.item_day_header,
        createDayHeaderDecider()
) {
    override fun populateItem(holder: MessageHolder, previous: Message?, model: Message, next: Message?, position: Int) {
        holder.bind(model, position, currentUserId == model.sender)
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

        fun bind(message: Message, position: Int, isOwnMsg: Boolean) {
            body?.text = message.body
            setType(isOwnMsg)
        }

        private fun setType(isOwnMsg: Boolean) {
            with(bubble!!) {
                val p = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                if (isOwnMsg) {
                    p.gravity = Gravity.END
                    background.setColorFilter(resources.getColor(R.color.chat_item_bubble_own), PorterDuff.Mode.SRC_IN)
                } else {
                    p.gravity = Gravity.START
                    background.setColorFilter(resources.getColor(R.color.chat_item_bubble_other), PorterDuff.Mode.SRC_IN)
                }
                layoutParams = p
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