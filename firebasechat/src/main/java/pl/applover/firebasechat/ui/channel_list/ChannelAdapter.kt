package pl.applover.firebasechat.ui.channel_list

import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.daimajia.swipe.SwipeLayout
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_channel_list.view.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.config.ChannelListConfig
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.ChatUser
import pl.applover.firebasechat.ui.CircleTransformation
import pl.applover.firebasechat.ui.HeaderedFirebaseAdapter
import pl.applover.firebasechat.ui.channel_list.ChannelAdapter.ChannelHolder
import pl.applover.firebasechat.ui.channel_list.ChannelAdapter.ChannelHolder.OnChannelClickListener

/**
 * Created by sp0rk on 18/08/17.
 */
class ChannelAdapter(val listener: OnChannelClickListener)
    : HeaderedFirebaseAdapter<ChannelHolder, ChannelHolder, Channel>(
        FirebaseDatabase.getInstance().reference.child("chatlover").child("channel_by_user").child(ChatUser.current!!.uid),
        Channel::class.java, ChannelHolder::class.java, ChannelHolder::class.java,
        R.layout.item_channel_list, R.layout.item_channel_list, createDayHeaderDecider()) {

    override fun populateItem(holder: ChannelHolder, previous: Channel?, model: Channel, next: Channel?, position: Int) {
        holder.bind(model,
                position,
                FirebaseStorage.getInstance().reference,
                listener)
    }

    override fun populateHeader(holder: ChannelHolder, previous: Channel?, next: Channel?, position: Int) {}

    companion object {
        fun createDayHeaderDecider() = object : HeaderDecider<Channel> {
            override fun getHeader(previous: Channel?, next: Channel?) = null
        }
    }

    class ChannelHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView?.item_channel_name
        val lastMsg: TextView? = itemView?.item_channel_last_message
        val time: TextView? = itemView?.item_channel_time
        val cell: RelativeLayout? = itemView?.item_channel_cell
        val divider: FrameLayout? = itemView?.item_channel_divider
        val icon: ImageView? = itemView?.item_channel_icon
        val swipe: SwipeLayout? = itemView?.chat_swipe_layout
        var latestSwipe: SwipeLayout? = null
        val swipes = listOf(itemView?.swipe_0, itemView?.swipe_1, itemView?.swipe_2, itemView?.swipe_3)
        val swipeIcon: ImageView? = itemView?.swipe_icon

        val onToggle = object : SwipeLayout.SwipeListener {
            override fun onOpen(layout: SwipeLayout?) {}
            override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {}
            override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}
            override fun onStartClose(layout: SwipeLayout?) {}
            override fun onClose(layout: SwipeLayout?) {}
            override fun onStartOpen(layout: SwipeLayout?) {
                if (latestSwipe != layout) {
                    latestSwipe?.close()
                    latestSwipe = layout
                }
            }
        }

        fun bind(channel: Channel, position: Int, storage: StorageReference, listener: OnChannelClickListener) {
            name?.text = ChannelListConfig.nameDecider?.invoke(channel) ?: channel.name

            if (ChannelListConfig.swipeActions.isEmpty())
                swipe?.isSwipeEnabled = false
            else
                bindSwipeActions(channel)

            swipeIcon?.setOnClickListener { swipe?.toggle() }

            swipe?.addSwipeListener(onToggle)
            if (channel.messageList.isNotEmpty()) {
                with(channel.messageList.last()) {
                    lastMsg?.text = body
                    if (sender == "init") {
                        lastMsg?.alpha = 0.5f
                    }
                }
                time?.text =
                        if (channel.messageList.last().sender != "init")
                            DateUtils.getRelativeTimeSpanString(channel.messageList.last().time)
                        else
                            ""
            } else {
                lastMsg?.text = ""
                time?.text = ""
            }
            channel.picture?.let {
                Glide.with(icon?.context)
                        .using(FirebaseImageLoader())
                        .load(ChannelListConfig.pictureDecider?.invoke(
                                channel,
                                storage,
                                FirebaseStorage.getInstance().reference)
                                ?: FirebaseStorage.getInstance().reference
                                .child("chatlover").child("chat_user"))
                        .placeholder(ChannelListConfig.picturePlaceholder ?: ContextCompat.getDrawable(icon!!.context, R.drawable.channel_placeholder))
                        .bitmapTransform(CircleTransformation(icon!!.context))
                        .into(icon)
            } ?: Glide.with(icon?.context)
                    .using(FirebaseImageLoader())
                    .load(ChannelListConfig.pictureDecider?.invoke(channel, storage, storage))
                    .placeholder(ChannelListConfig.picturePlaceholder ?: ContextCompat.getDrawable(icon!!.context, R.drawable.channel_placeholder))
                    .bitmapTransform(CircleTransformation(icon!!.context))
                    .into(icon) ?:
                    icon?.setImageDrawable(ChannelListConfig.picturePlaceholder ?: ContextCompat.getDrawable(icon.context, R.drawable.channel_placeholder))
            cell?.setOnClickListener { listener.onClick(channel) }
            designWithConfig()
        }

        private fun bindSwipeActions(channel: Channel) {
            swipeIcon?.visibility = View.VISIBLE
            ChannelListConfig.swipeActions.forEachIndexed { i, action ->
                with(swipes[i]!!) {
                    visibility = View.VISIBLE
                    background = ColorDrawable(action.colour)
                    with(getChildAt(0) as LinearLayout) {
                        val icon = getChildAt(0) as ImageView
                        val text = getChildAt(1) as TextView
                        icon.setImageDrawable(action.icon)
                        text.text = action.name
                    }
                    setOnClickListener { action.action(channel) }
                }
            }
        }

        private fun designWithConfig() {
            with(name!!) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ChannelListConfig.nameSize
                        ?: context.resources.getDimension(R.dimen.item_channel_name_size))
                setTextColor(ChannelListConfig.nameColour ?: context.resources.getColor(R.color.item_channel_name))
            }
            with(time!!) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ChannelListConfig.timeSize
                        ?: context.resources.getDimension(R.dimen.item_channel_time_size))
                setTextColor(ChannelListConfig.timeColour ?: context.resources.getColor(R.color.item_channel_time))
            }
            with(lastMsg!!) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, ChannelListConfig.lastMsgSize
                        ?: context.resources.getDimension(R.dimen.item_channel_last_msg_size))
                setTextColor(ChannelListConfig.lastMsgColour ?: context.resources.getColor(R.color.item_channel_last_msg))
            }
            ChannelListConfig.pictureSize?.let {
                icon?.layoutParams = ViewGroup.LayoutParams(it, it)
            }
            cell!!.background = ColorDrawable((ChannelListConfig.itemBackground ?: cell.context.resources.getColor(R.color.channel_list_item_background)))
            divider!!.background = ColorDrawable((ChannelListConfig.dividerColour ?: cell.context.resources.getColor(R.color.channel_list_divider)))
        }

        interface OnChannelClickListener {
            fun onClick(channel: Channel)
        }
    }
}