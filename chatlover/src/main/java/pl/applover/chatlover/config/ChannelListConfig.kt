package pl.applover.chatlover.config

import android.graphics.drawable.Drawable
import android.view.View
import com.google.firebase.storage.StorageReference
import pl.applover.chatlover.model.Channel

/**
 * Created by sp0rk on 28/08/17.
 */
object ChannelListConfig {
    var picturePlaceholder: Drawable? = null
    var pictureDecider: ((channel: Channel,
                          userRef: StorageReference,
                          channelRef: StorageReference) -> StorageReference)? = null
    var pictureSize: Float? = null

    var nameSize: Float? = null
    var nameColour: Int? = null
    var nameDecider: ((channel: Channel) -> String)? = null

    var lastMsgSize: Float? = null
    var lastMsgColour: Int? = null

    var timeSize: Float? = null
    var timeColour: Int? = null

    var itemBackground: Int? = null
    var dividerColour: Int? = null

    var onFragmentViewCreated: ((View?) -> Unit)? = null

    var swipeActions: List<SwipeAction<Channel>>? = null
        set(value) {
            field = if (value == null) null else {
                if (value.size <= 4) value
                else throw IllegalStateException("Maximum number of swipe actions is 4")
            }
        }

    var swipeActionWidth: Float? = null

}