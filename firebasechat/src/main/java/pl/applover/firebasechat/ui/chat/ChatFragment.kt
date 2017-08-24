package pl.applover.firebasechat.ui.chat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chat.*
import pl.applover.firebasechat.R
import pl.applover.firebasechat.model.Message

/**
 * Created by sp0rk on 11/08/17.
 */
class ChatFragment : Fragment() {
    var listener: ChatListener? = null
    val recyclerView: RecyclerView by lazy { chat_recycler_view }
    val input: EditText by lazy { chat_input_field }
    val send: ImageView by lazy { chat_send_btn }
    val sendLocation: ImageView by lazy { chat_location_btn }
    val channelId by lazy { arguments.getString("channelId") }
    val currentUserId by lazy { arguments.getString("currentUserId") }
    lateinit var manager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.fragment_chat, container, false)

        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val llm = LinearLayoutManager(context).apply { stackFromEnd = true }
        recyclerView.layoutManager = llm
        recyclerView.adapter = ChatAdapter(channelId, currentUserId, llm, recyclerView).withAutoscroll()

        send.setOnClickListener { onSend() }
        sendLocation.setOnClickListener { requestLocation { onLocationFound(it) } }
    }

    fun onSend() {
        if (input.text.isNotEmpty()) {
            val db = FirebaseDatabase.getInstance().reference
            with(db.child("channels").child(channelId).child("messages").push()) {
                ref.setValue(Message(
                        key,
                        currentUserId,
                        System.currentTimeMillis(),
                        input.text.toString(),
                        Message.Type.txt)).addOnCompleteListener {
                    input.setText("")
                    recyclerView.layoutManager.scrollToPosition(recyclerView.adapter.itemCount - 1)
                }
            }
        }
    }

    fun requestLocation(then: (l: Location?) -> Unit) {
        sendLocation.setImageResource(R.drawable.ic_hourglass)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, object : LocationListener {
                override fun onLocationChanged(l: Location?) = then(l)
                override fun onStatusChanged(s: String?, p1: Int, p2: Bundle?) {}
                override fun onProviderEnabled(s: String?) {}
                override fun onProviderDisabled(s: String?) {}
            }, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    fun onLocationFound(l: Location?) {
        Toast.makeText(context, "Click to send your location", Toast.LENGTH_SHORT).show()
        sendLocation.setOnClickListener { sendLocation(l) }
        sendLocation.setImageResource(R.drawable.ic_send)
        Handler().postDelayed({
            sendLocation.setOnClickListener { requestLocation { onLocationFound(it) } }
            sendLocation.setImageResource(R.drawable.ic_location)
        }, 3000)
    }

    fun sendLocation(l: Location?) {
        l?.let {
            val db = FirebaseDatabase.getInstance().reference
            with(db.child("channels").child(channelId).child("messages").push()) {
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
        sendLocation.setImageResource(R.drawable.ic_location)
    }

    fun withListener(listener: ChatListener) = this.also { this.listener = listener }

    companion object {
        fun newInstance(channelId: String, currentUserId: String): ChatFragment {
            val fragment = ChatFragment()
            with(Bundle()) {
                putString("channelId", channelId)
                putString("currentUserId", currentUserId)
                fragment.arguments = this
            }
            return fragment
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (recyclerView.adapter as? ChatAdapter)?.destroy()
    }

    interface ChatListener
}