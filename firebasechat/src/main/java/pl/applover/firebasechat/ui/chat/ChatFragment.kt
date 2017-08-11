package pl.applover.firebasechat.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_chat.*
import pl.applover.firebasechat.R

/**
 * Created by sp0rk on 11/08/17.
 */
class ChatFragment : Fragment() {
    var listener: ChatListener? = null
    val recyclerView: RecyclerView by lazy { chat_recycler_view }
    val channelId by lazy { arguments.getString("channelId") }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_chat, container, false)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MessageHolder.getAdapter(channelId)
    }

    fun withListener(listener: ChatListener) = this.also { this.listener = listener }

    companion object {
        fun newInstance(channelId: String): ChatFragment {
            val fragment = ChatFragment()
            with(Bundle()) {
                putString("channelId", channelId)
                fragment.arguments = this
            }
            return fragment
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (recyclerView.adapter as? FirebaseRecyclerAdapter<*,*>)?.cleanup()
    }

    interface ChatListener
}