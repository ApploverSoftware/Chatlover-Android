package pl.applover.firebasechat.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by sp0rk on 10/08/17.
 */
class Channel(val id: String, val name: String, val users: HashMap<String, ChatUser>, val messages: HashMap<String, Message>, val picture: String? = null) : Parcelable {
    constructor() : this("", "", HashMap<String, ChatUser>(), HashMap<String, Message>()) //needed for Firebase
    //Parcelable implementation
    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            name = parcel.readString(),
            users = HashMap<String, ChatUser>(),
            messages = HashMap<String, Message>(),
            picture = parcel.readString()) {
        parcel.readMap(users, ChatUser::class.java.classLoader)
        parcel.readMap(messages, Message::class.java.classLoader)
    }

    var userList = emptyList<ChatUser>()
        get() = users.values.toList()

    var messageList = emptyList<Message>()
        get() = messages.entries.sortedBy { it.key }.map { it.value }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(name)
        dest?.writeMap(users)
        dest?.writeMap(messages)
        dest?.writeString(picture)
    }

    override fun describeContents() = 0

    companion object {
        val CREATOR = object : Parcelable.Creator<Channel> {
            override fun createFromParcel(p: Parcel) = Channel(p)
            override fun newArray(s: Int): Array<Channel?> = arrayOfNulls(s)
        }

        fun provideChannel(channelId: String, completion: (Channel?) -> Unit) {
            FirebaseDatabase.getInstance().reference.child("channels").child(channelId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError?) {
                    completion(null)
                }

                override fun onDataChange(snapshot: DataSnapshot?) {
                    snapshot?.let {
                        completion(snapshot.getValue(Channel::class.java))
                    } ?: run {
                        completion(null)
                    }
                }
            })
        }
    }
}