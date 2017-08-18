package pl.applover.firebasechat.ui.chat

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_day_header.view.*


/**
 * Created by sp0rk on 18/08/17.
 */
class DayHeaderHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val label = itemView?.day_header_label
    fun bind(text: String) {
        label?.text = text
    }
}