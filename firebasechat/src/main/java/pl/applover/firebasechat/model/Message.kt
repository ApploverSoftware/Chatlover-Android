package pl.applover.firebasechat.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import java.util.*

/**
 * Created by sp0rk on 10/08/17.
 */
data class Message(val id: String, val sender: String, val time: Long, val body: String, val type: Type) : Parcelable {
    constructor() : this("", "", 0L, "", Type.txt) //needed for Firebase
    //Parcelable implementation
    constructor(parcel: Parcel) : this(
            id = parcel.readString(),
            sender = parcel.readString(),
            time = parcel.readLong(),
            body = parcel.readString(),
            type = Type.valueOf(parcel.readString()))

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(sender)
        dest?.writeLong(time)
        dest?.writeString(body)
        dest?.writeString(type.name)
    }

    override fun describeContents() = 0

    companion object CREATOR : Creator<Message> {
        override fun createFromParcel(p: Parcel) = Message(p)
        override fun newArray(s: Int): Array<Message?> = arrayOfNulls(s)
    }

    val calendar: Calendar by lazy {
        Calendar.getInstance().also { it.timeInMillis = time }
    }

    enum class Type {txt, img, vid, loc, mic}
}