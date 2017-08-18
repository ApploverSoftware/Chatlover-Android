package pl.applover.firebasechat.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by sp0rk on 10/08/17.
 */
class Channel(val id: String, val name: String, val users: HashMap<String, String>, val messages: HashMap<String, Message>, val picture: String? = null) : Parcelable {
    constructor() : this("", "", HashMap<String, String>(), HashMap<String, Message>()) //needed for Firebase
    //Parcelable implementation
    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            name = parcel.readString(),
            users = HashMap<String, String>(),
            messages = HashMap<String, Message>(),
            picture = parcel.readString()) {
        parcel.readMap(users, String::class.java.classLoader)
        parcel.readMap(messages, Message::class.java.classLoader)
    }

    var userList = emptyList<String>()
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

    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(p: Parcel) = Channel(p)
        override fun newArray(s: Int): Array<Channel?> = arrayOfNulls(s)
    }

}