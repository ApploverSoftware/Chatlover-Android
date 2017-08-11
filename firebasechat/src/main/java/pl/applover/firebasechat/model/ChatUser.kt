package pl.applover.firebasechat.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Created by sp0rk on 10/08/17.
 */
data class ChatUser(val uid: String, val fcmToken:String? = null, val avatar:String? = null) : Parcelable {
    constructor():this("") //needed for firebase
    //Parcelable implementation
    constructor(parcel: Parcel) : this(
            uid = parcel.readString(),
            fcmToken = parcel.readString(),
            avatar = parcel.readString()
    )
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(uid)
        dest?.writeString(fcmToken)
        dest?.writeString(avatar)
    }
    override fun describeContents()=0
    companion object CREATOR : Creator<ChatUser> {
        override fun createFromParcel(p: Parcel)=ChatUser(p)
        override fun newArray(s: Int): Array<ChatUser?> = arrayOfNulls(s)
    }

}