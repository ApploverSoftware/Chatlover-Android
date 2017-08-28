package pl.applover.firebasechat.ui.chat

import android.graphics.PorterDuff
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_day_header.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.config.ChatViewConfig
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.ChatUser
import pl.applover.firebasechat.model.Message
import pl.applover.firebasechat.ui.CircleTransformation
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
        holder.bind(
                model,
                channel,
                position,
                currentUserId == model.sender,
                FirebaseStorage.getInstance().reference.child("chatlover")
                        .child("chat_user"))
    }

    override fun populateHeader(holder: DayHeaderHolder, previous: Message?, next: Message?, position: Int) {
        holder.bind(createDayHeaderDecider().getHeader(previous, next) ?: "New messages")
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
                val p = previous?.provideCalendar()!!
                val n = next?.provideCalendar()!!
                val sameYear = p.get(Calendar.YEAR) == n.get(Calendar.YEAR)
                if (n.get(Calendar.DAY_OF_YEAR) == p.get(Calendar.DAY_OF_YEAR)) return null
                else return SimpleDateFormat("${ChatViewConfig.headerTimeFormat
                        ?: "EEE, d MMM"} ${if (sameYear) "" else "''yy"}",
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
        val placeholder = ChatViewConfig.avatarPlaceholder ?: itemView!!.context?.getDrawable(R.drawable.user_placeholder)

        fun bind(message: Message, channel: Channel, position: Int, isOwnMsg: Boolean, storage: StorageReference) {
            body?.text = message.body

            var label = SimpleDateFormat(ChatViewConfig.labelTimeFormat
                    ?: "EEE HH:mm", Locale.getDefault()).format(message.time)
            if (!isOwnMsg)
                channel.users[message.sender]?.let {
                    label += ", ${it.name}"
                }
            time?.text = label

            designWithConfig()

            setType(channel.users[message.sender], isOwnMsg, storage)
        }

        fun designWithConfig() {
            ChatViewConfig.avatarSize?.let {
                avatar?.layoutParams = ViewGroup.LayoutParams(it, it)
            }
            with(time!!) {
                setTextColor(ChatViewConfig.labelColour ?: context.resources.getColor(R.color.chat_item_label))
                textSize = ChatViewConfig.labelSize
                        ?: context.resources.getDimension(R.dimen.item_message_label_text_size)
                if (!(ChatViewConfig.labelIsShown
                        ?: true)) visibility = View.GONE
            }
            with(body!!) {
                textSize = ChatViewConfig.textSize
                        ?: context.resources.getDimension(R.dimen.item_message_text_size)
                setTextColor(ChatViewConfig.textColour ?: context.resources.getColor(R.color.chat_item_text))
                val m = ChatViewConfig.textPadding
                        ?: R.dimen.item_message_text_padding
                (layoutParams as FrameLayout.LayoutParams).setMargins(m, m, m, m)
            }
            with(bubble!!) {
                background = ChatViewConfig.bubbleShape ?: context.resources.getDrawable(R.drawable.chat_bubble)
            }
        }

        private fun setType(user: ChatUser?, isOwnMsg: Boolean, storage: StorageReference) {
            if (isOwnMsg) {
                avatar?.visibility = View.GONE
                lr?.gravity = Gravity.END
                bubble?.background?.setColorFilter(ChatViewConfig.bubbleColourOwn
                        ?: bubble.resources.getColor(R.color.chat_item_bubble_own), PorterDuff.Mode.SRC_IN)
            } else {
                if (ChatViewConfig.avatarIsShown ?: true) {
                    avatar?.visibility = View.VISIBLE
                    user?.avatar?.let {
                        Glide.with(avatar?.context)
                                .using(FirebaseImageLoader())
                                .load(storage.child(user.uid).child(user.avatar))
                                .placeholder(placeholder)
                                .bitmapTransform(CircleTransformation(avatar!!.context))
                                .into(avatar)
                    }
                }
                lr?.gravity = Gravity.START
                bubble?.background?.setColorFilter(ChatViewConfig.bubbleColourOther
                        ?: bubble.resources.getColor(R.color.chat_item_bubble_other), PorterDuff.Mode.SRC_IN)
            }

        }
    }

    class DayHeaderHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val label = itemView?.day_header_label
        fun bind(text: String) {
            if (!(ChatViewConfig.headerIsShown ?: true))
                itemView.visibility = View.GONE
            label?.text = text
            label?.textSize = ChatViewConfig.headerSize
                    ?: label!!.context.resources.getDimension(R.dimen.header_text_size)
            label?.setTextColor(ChatViewConfig.headerColour
                    ?: label.context.resources.getColor(R.color.chat_day_header_color))
        }
    }
}