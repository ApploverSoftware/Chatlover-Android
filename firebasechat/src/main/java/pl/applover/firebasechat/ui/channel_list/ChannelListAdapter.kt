package pl.applover.firebasechat.ui.channel_list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Channel
import kotlinx.android.synthetic.main.item_channel_list.view.*

/**
 * Created by sp0rk on 10/08/17.
 */
class ChannelListAdapter(val channels: MutableList<Channel>, val listener: OnChannelClickListener) : RecyclerView.Adapter<ChannelListAdapter.ChannelViewHolder>() {
    val storage by lazy { FirebaseStorage.getInstance().reference.child("FirebaseChat").child("channel_pictures") }

    override fun getItemCount() = channels.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ChannelViewHolder(
            LayoutInflater
                    .from(parent?.context)
                    .inflate(R.layout.item_channel_list, parent, false))

    override fun onBindViewHolder(holder: ChannelViewHolder?, position: Int) {
        holder?.bind(channels[position], storage, listener)
    }

    class ChannelViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val name : TextView? = itemView?.item_channel_name
        val lastMsg : TextView? = itemView?.item_channel_last_message
        val time : TextView? = itemView?.item_channel_time
        val cell : RelativeLayout? = itemView?.item_channel_cell
        val icon : ImageView? = itemView?.item_channel_icon

        fun bind(channel: Channel, storage: StorageReference, listener: OnChannelClickListener) {
            name?.text = channel.name
            lastMsg?.text = channel.messages[0].body
            time?.text = channel.messages[0].time
            channel.picture?.let {
                Glide.with(icon?.context)
                        .using(FirebaseImageLoader())
                        .load(storage.child(channel.picture))
                        .into(icon)
            }
            cell?.setOnClickListener{listener.onClick(channel.id)}
        }
    }

    interface OnChannelClickListener {
        fun onClick(channelId: String)
    }
}