package pl.applover.firebasechat.config

import android.graphics.drawable.Drawable
import com.google.firebase.storage.StorageReference
import pl.applover.firebasechat.model.Channel

/**
 * Created by sp0rk on 28/08/17.
 */
object ChannelListConfig {
    var picturePlaceholder: Drawable? = null
    var pictureDecider: ((channel: Channel,
                          userRef: StorageReference,
                          channelRef: StorageReference) -> StorageReference)? = null
    var pictureSize: Int? = null

    var nameSize: Float? = null
    var nameColour: Int? = null

    var lastMsgSize: Float? = null
    var lastMsgColour: Int? = null

    var timeSize: Float? = null
    var timeColour: Int? = null

    var itemBackground: Int? = null
    var dividerColour: Int? = null
}