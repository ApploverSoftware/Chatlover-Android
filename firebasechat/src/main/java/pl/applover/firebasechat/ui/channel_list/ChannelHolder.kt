package pl.applover.firebasechat.ui.channel_list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_channel_list.view.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Channel

/**
 * Created by sp0rk on 11/08/17.
 */
class ChannelHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val name: TextView? = itemView?.item_channel_name
    val lastMsg: TextView? = itemView?.item_channel_last_message
    val time: TextView? = itemView?.item_channel_time
    val cell: RelativeLayout? = itemView?.item_channel_cell
    val icon: ImageView? = itemView?.item_channel_icon

    fun bind(channel: Channel, position: Int, storage: StorageReference, listener: OnChannelClickListener) {
        name?.text = channel.name
        lastMsg?.text = channel.msgs.last().body
        time?.text = channel.msgs.last().time.toString()
        channel.picture?.let {
            Glide.with(icon?.context)
                    .using(FirebaseImageLoader())
                    .load(storage.child(channel.picture))
                    .into(icon)
        }
        cell?.setOnClickListener { listener.onClick(channel.id) }
    }

    companion object {
        fun getAdapter(listener: OnChannelClickListener) =
                object : FirebaseRecyclerAdapter<Channel, ChannelHolder>(
                        Channel::class.java,
                        R.layout.item_channel_list,
                        ChannelHolder::class.java,
                        FirebaseDatabase.getInstance().reference.child("channels")) {
                    override fun populateViewHolder(viewHolder: ChannelHolder?, model: Channel?, position: Int) {
                        val ref = FirebaseStorage.getInstance().reference.child("FirebaseChat").child("channel_pictures")
                        model?.let { viewHolder?.bind(model, position, ref, listener) }
                    }
                }
    }
    interface OnChannelClickListener {
        fun onClick(channelId: String)
    }
}