package pl.applover.firebasechat.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by sp0rk on 10/08/17.
 */
class Channel(val id: String, val name: String, val users: List<String>, val messages: List<Message>, val picture: String? = null) : Parcelable {

    //Parcelable implementation
    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            name = parcel.readString(),
            users = ArrayList<String>(),
            messages = ArrayList<Message>(),
            picture = parcel.readString()) {
        parcel.readStringList(users)
        parcel.readTypedList(messages, Message.CREATOR)
    }
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(name)
        dest?.writeStringList(users)
        dest?.writeTypedList(messages)
        dest?.writeString(picture)
    }
    override fun describeContents() = 0
    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(p: Parcel) = Channel(p)
        override fun newArray(s: Int): Array<Channel?> = arrayOfNulls(s)
    }
}