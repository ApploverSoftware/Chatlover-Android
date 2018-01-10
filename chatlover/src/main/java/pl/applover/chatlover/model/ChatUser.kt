package pl.applover.chatlover.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

/**
 * Created by sp0rk on 10/08/17.
 */
data class ChatUser(val uid: String, val name: String, var fcmToken: String? = null, val avatar: String? = null) : Parcelable {
    constructor() : this("", "") //needed for firebase

    fun save(completion: (() -> Unit)? = null) {
        FirebaseDatabase.getInstance().reference.child("chatlover").child("chat_users").child(uid).setValue(this).addOnCompleteListener {
            completion?.invoke()
        }
    }

    //Parcelable implementation
    constructor(parcel: Parcel) : this(
            uid = parcel.readString(),
            name = parcel.readString(),
            fcmToken = parcel.readString(),
            avatar = parcel.readString()
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(uid)
        dest?.writeString(name)
        dest?.writeString(fcmToken)
        dest?.writeString(avatar)
    }

    override fun describeContents() = 0

    companion object {
        fun loginWithUid(id: String, name: String, completion: (() -> Unit)? = null) {
            FirebaseDatabase.getInstance().reference.child("chatlover").child("chat_users").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(err: DatabaseError?) {
                    TODO("not implemented")
                }

                override fun onDataChange(snap: DataSnapshot?) {
                    if (snap?.exists() ?: false) {
                        current = snap?.getValue(ChatUser::class.java)
                        refreshCurrentToken(completion)
                    } else {
                        val user = ChatUser(id, name)
                        FirebaseDatabase.getInstance().reference.child("chatlover")
                                .child("chat_users").child(id).setValue(user).addOnCompleteListener {
                            current = user
                            refreshCurrentToken(completion)
                        }
                    }
                }
            })
        }

        fun refreshCurrentToken(completion: (() -> Unit)? = null) {
            FirebaseInstanceId.getInstance().token?.let {
                if (it.isNotEmpty()) {
                    current?.fcmToken = it
                    FirebaseDatabase.getInstance().reference.child("chatlover").child("chat_users").child(current!!.uid).child("fcmToken").setValue(it).addOnCompleteListener { completion?.invoke() }
                }
            }
        }

        var current: ChatUser? = null
        val CREATOR = object : Creator<ChatUser> {
            override fun createFromParcel(p: Parcel) = ChatUser(p)
            override fun newArray(s: Int): Array<ChatUser?> = arrayOfNulls(s)
        }
    }

}