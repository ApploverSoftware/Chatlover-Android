package pl.applover.firebasechat.ui.chat

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chat.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.config.ChatViewConfig
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.Message

/**
 * Created by sp0rk on 11/08/17.
 */
class ChatFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    val channel by lazy { arguments.getParcelable<Channel>("channel") }
    val currentUserId by lazy { arguments.getString("currentUserId") }
    lateinit var manager: LocationManager

    override fun onPause() {
        if (chat_input_field.hasFocus() || chat_input_field.hasWindowFocus()) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(chat_input_field.windowToken, 0)
        }
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (ChatViewConfig.onBackPressed != null).let {
            setHasOptionsMenu(it)
            getParentActivity(this).supportActionBar?.setDisplayHomeAsUpEnabled(it)
        }
    }

    override fun onDestroy() {
        (ChatViewConfig.onBackPressed != null).let {
            setHasOptionsMenu(false)
            getParentActivity(this).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        super.onDestroy()
    }

    private fun getParentActivity(fragment: Fragment): AppCompatActivity =
            fragment.activity as? AppCompatActivity ?: getParentActivity(parentFragment)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_chat, container, false)
        recyclerView = root!!.findViewById<RecyclerView>(R.id.chat_recycler_view)
        manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val llm = LinearLayoutManager(context).apply { stackFromEnd = true }
        recyclerView.layoutManager = llm
        recyclerView.adapter = ChatAdapter(channel, currentUserId, llm, recyclerView).withAutoscroll()

        designWithConfig()
        ChatViewConfig.onFragmentViewCreated?.invoke(view, channel)
    }

    override fun onResume() {
        super.onResume()
        chat_send_btn.setOnClickListener { onSend() }
        chat_location_btn.setup(activity, manager, 3000) { l: Location? ->
            l?.let {
                val db = FirebaseDatabase.getInstance().reference
                with(db.child("chatlover").child("channels").child(channel.id).child("messages").push()) {
                    ref.setValue(Message(
                            key,
                            currentUserId,
                            System.currentTimeMillis(),
                            "${it.latitude}/${it.longitude}",
                            Message.Type.loc)).addOnCompleteListener {
                        recyclerView.layoutManager.scrollToPosition(recyclerView.adapter.itemCount - 1)
                    }
                }
            } ?: Toast.makeText(context, "No location", Toast.LENGTH_SHORT).show()
        }
    }

    fun onSend() {
        if (chat_input_field.text.toString().trim().isNotEmpty()) {
            val db = FirebaseDatabase.getInstance().reference
            with(db.child("chatlover").child("channels").child(channel.id).child("messages").push()) {
                ref.setValue(Message(
                        key,
                        currentUserId,
                        System.currentTimeMillis(),
                        chat_input_field.text.toString().trim(),
                        Message.Type.txt)).addOnCompleteListener {
                    chat_input_field.setText("")
                    recyclerView.layoutManager.scrollToPosition(recyclerView.adapter.itemCount - 1)
                }
            }
        }
    }

    fun designWithConfig() {
        chat_send_btn.setImageDrawable(ChatViewConfig.iconSend ?: ContextCompat.getDrawable(context, R.drawable.ic_send))
        chat_location_btn.setImageDrawable(ChatViewConfig.iconLocation ?: ContextCompat.getDrawable(context, R.drawable.ic_location))

        with(chat_input_field) {
            background = ChatViewConfig.inputBackground ?: ContextCompat.getDrawable(context, R.drawable.chat_input_background)
            hint = ChatViewConfig.inputHint ?: context.getString(R.string.chat_input_hint)
            maxLines = ChatViewConfig.inputMaxLines ?: 4
            setTextSize(TypedValue.COMPLEX_UNIT_PX, ChatViewConfig.inputTextSize
                    ?: context.resources.getDimension(R.dimen.input_text_size))
            setTextColor(ChatViewConfig.inputTextColour ?: context.resources.getColor(R.color.chat_input_text))
        }
    }

    companion object {
        val TAG = "ChatloverChatFragment"

        fun newInstance(channel: Channel, currentUserId: String): ChatFragment {
            val fragment = ChatFragment()
            with(Bundle()) {
                putParcelable("channel", channel)
                putString("currentUserId", currentUserId)
                fragment.arguments = this
            }
            return fragment
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> ChatViewConfig.onBackPressed?.invoke()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (recyclerView.adapter as? ChatAdapter)?.destroy()
    }
}