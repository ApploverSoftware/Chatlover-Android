package pl.applover.firebasechat.ui.chat

import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_day_header.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_message_loc.view.*
import kotlinx.android.synthetic.main.item_message_txt.view.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.config.ChatViewConfig
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.ChatUser
import pl.applover.firebasechat.model.Message
import pl.applover.firebasechat.model.Message.Type.*
import pl.applover.firebasechat.toAddressAsync
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
        val textBody = itemView?.item_message_body
        val txtBlock = itemView?.item_message_block_txt
        val locBlock = itemView?.item_message_block_loc
        val locTitle = itemView?.item_message_loc_title
        val locAddress = itemView?.item_message_loc_address
        val bubble = itemView?.item_message_bubble
        val lr = itemView?.item_message_lr
        val avatar = itemView?.item_message_avatar
        val time = itemView?.item_message_time
        val placeholder = ChatViewConfig.avatarPlaceholder ?: itemView!!.context?.getDrawable(R.drawable.user_placeholder)

        fun bind(message: Message, channel: Channel, position: Int, isOwnMsg: Boolean, storage: StorageReference) {
            var label = SimpleDateFormat(ChatViewConfig.labelTimeFormat
                    ?: "EEE HH:mm", Locale.getDefault()).format(message.time)
            if (!isOwnMsg)
                channel.users[message.sender]?.let {
                    label += ", ${it.name}"
                }
            time?.text = label
            designWithConfig()
            setDirection(channel.users[message.sender], isOwnMsg, storage)
            setType(message, channel.users[message.sender])
        }

        private fun setType(message: Message, user: ChatUser?) {
            //TODO: Add onclick handlers for copying and displaying maps and such.
            listOf(txtBlock, locBlock).forEach { it?.visibility = View.GONE }
            when (message.type) {
                txt -> {
                    txtBlock?.visibility = View.VISIBLE
                    textBody?.text = message.body
                }
                loc -> {
                    locBlock?.visibility = View.VISIBLE
                    locTitle?.text =
                            if (ChatUser.current!!.uid == message.sender) "You sent your location"
                            else "${user?.name} sent their location"
                    message.body.toAddressAsync(locAddress!!.context) {
                        locAddress.text = it
                    }
                }
                img -> TODO("Not yet supported")
                vid -> TODO("Not yet supported")
                mic -> TODO("Not yet supported")
            }
        }

        fun designWithConfig() {
            ChatViewConfig.avatarSize?.let {
                avatar?.layoutParams = ViewGroup.LayoutParams(it, it)
            }
            with(time!!) {
                setTextColor(ChatViewConfig.labelColour ?: context.resources.getColor(R.color.chat_item_label))
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ChatViewConfig.labelSize
                        ?: context.resources.getDimension(R.dimen.item_message_label_text_size))
                if (!(ChatViewConfig.labelIsShown
                        ?: true)) visibility = View.GONE
            }
            with(textBody!!) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ChatViewConfig.textSize
                        ?: context.resources.getDimension(R.dimen.item_message_text_size))
                setTextColor(ChatViewConfig.textColour ?: context.resources.getColor(R.color.chat_item_text))
            }
            with(locTitle!!) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ChatViewConfig.textSize
                        ?: context.resources.getDimension(R.dimen.item_message_text_size))
                setTextColor(ChatViewConfig.textColour ?: context.resources.getColor(R.color.chat_item_text))
            }
            with(locAddress!!) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ChatViewConfig.textSize
                        ?: context.resources.getDimension(R.dimen.item_message_label_text_size))
                setTextColor(ChatViewConfig.textColourSecondary ?: context.resources.getColor(R.color.chat_item_text_secondary))
            }
            with(bubble!!) {
                background = ChatViewConfig.bubbleShape ?: ContextCompat.getDrawable(context, (R.drawable.chat_bubble))
            }
        }

        private fun setDirection(user: ChatUser?, isOwnMsg: Boolean, storage: StorageReference) {
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
            label?.setTextSize(TypedValue.COMPLEX_UNIT_PX, ChatViewConfig.headerSize
                    ?: label.context.resources.getDimension(R.dimen.header_text_size))
            label?.setTextColor(ChatViewConfig.headerColour
                    ?: label.context.resources.getColor(R.color.chat_day_header_color))
        }
    }
}