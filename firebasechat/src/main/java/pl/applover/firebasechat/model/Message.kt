package pl.applover.firebasechat.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Created by sp0rk on 10/08/17.
 */
data class Message(val id: String, val sender: String, val time: String, val body: String?) : Parcelable {

    //Parcelable implementation
    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            sender = parcel.readString(),
            time = parcel.readString(),
            body = parcel.readString())
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(sender)
        dest?.writeString(time)
        dest?.writeString(body)
    }
    override fun describeContents() = 0
    companion object CREATOR : Creator<Message> {
        override fun createFromParcel(p: Parcel) = Message(p)
        override fun newArray(s: Int): Array<Message?> = arrayOfNulls(s)
    }


}