package pl.applover.firebasechat.config

import android.graphics.drawable.Drawable
import android.view.View
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
    var nameDecider: ((channel: Channel) -> String)? = null

    var lastMsgSize: Float? = null
    var lastMsgColour: Int? = null

    var timeSize: Float? = null
    var timeColour: Int? = null

    var itemBackground: Int? = null
    var dividerColour: Int? = null

    var onFragmentViewCreated: ((View?)->Unit)? = null

    var swipeActions: List<SwipeAction<Channel>> = ArrayList()
        set(value) {
            if (value.size <= 4) field = value
            else throw IllegalStateException("Maximum number of swipe actions is 4")
        }

    var swipeActionWidth: Float = 48f

}