package pl.applover.firebasechat.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Channel
import kotlinx.android.synthetic.main.item_channel_list.view.*

/**
 * Created by sp0rk on 10/08/17.
 */
class ChannelListAdapter(val channels: MutableList<Channel>) : RecyclerView.Adapter<ChannelListAdapter.ChannelViewHolder>() {
    override fun getItemCount() = channels.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ChannelViewHolder(
            LayoutInflater
                    .from(parent?.context)
                    .inflate(R.layout.item_channel_list, parent, false))

    override fun onBindViewHolder(holder: ChannelViewHolder?, position: Int) {
        holder?.bind(channels[position])
    }

    class ChannelViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val name : TextView? = itemView?.item_channel_name

        fun bind(channel: Channel) {
            name?.text = channel.name
        }
    }
}