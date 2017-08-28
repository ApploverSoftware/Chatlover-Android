package pl.applover.firebasechat.ui.channel_list

import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_channel_list.view.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.ChatUser
import pl.applover.firebasechat.ui.HeaderedFirebaseAdapter
import pl.applover.firebasechat.ui.channel_list.ChannelAdapter.ChannelHolder
import pl.applover.firebasechat.ui.channel_list.ChannelAdapter.ChannelHolder.OnChannelClickListener

/**
 * Created by sp0rk on 18/08/17.
 */
class ChannelAdapter(val listener: OnChannelClickListener)
    : HeaderedFirebaseAdapter<ChannelHolder, ChannelHolder, Channel>(
        FirebaseDatabase.getInstance().reference.child("channel_by_user").child(ChatUser.current!!.uid),
        Channel::class.java, ChannelHolder::class.java, ChannelHolder::class.java,
        R.layout.item_channel_list, R.layout.item_channel_list, createDayHeaderDecider()) {

    override fun populateItem(holder: ChannelHolder, previous: Channel?, model: Channel, next: Channel?, position: Int) {
        holder.bind(model,
                position,
                FirebaseStorage.getInstance().reference
                        .child("FirebaseChat").child("channel_pictures"),
                listener)
    }

    override fun populateHeader(holder: ChannelHolder, previous: Channel?, next: Channel?, position: Int) {}

    companion object {
        fun createDayHeaderDecider() = object : HeaderDecider<Channel> {
            override fun getHeader(previous: Channel?, next: Channel?)= null
        }
    }

    class ChannelHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val name: TextView? = itemView?.item_channel_name
        val lastMsg: TextView? = itemView?.item_channel_last_message
        val time: TextView? = itemView?.item_channel_time
        val cell: RelativeLayout? = itemView?.item_channel_cell
        val icon: ImageView? = itemView?.item_channel_icon

        fun bind(channel: Channel, position: Int, storage: StorageReference, listener: OnChannelClickListener) {
            name?.text = channel.name
            if (channel.messageList.isNotEmpty()) {
                lastMsg?.text = channel.messageList.last().body
                time?.text = DateUtils.getRelativeTimeSpanString(channel.messageList.last().time)
            } else {
                lastMsg?.text = ""
                time?.text = ""
            }
            channel.picture?.let {
                Glide.with(icon?.context)
                        .using(FirebaseImageLoader())
                        .load(storage.child(channel.picture))
                        .into(icon)
            }
            cell?.setOnClickListener { listener.onClick(channel) }
        }

        interface OnChannelClickListener {
            fun onClick(channel: Channel)
        }
    }
}