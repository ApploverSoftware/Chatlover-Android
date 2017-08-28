package pl.applover.firebasechat.config

import android.graphics.Color
import android.graphics.drawable.Drawable
import pl.applover.firebasechat.model.Message

/**
 * Created by sp0rk on 28/08/17.
 */
object ChatViewConfig {
    var bubbleRadius: Int? = null
    var bubbleShape: Drawable? = null
    var bubbleColourOwn: Color? = null
    var bubbleColourOther: Color? = null

    var textPadding: Int? = null
    var textSize: Int? = null
    var textColor: Color? = null

    var labelColor: Color? = null
    var labelSize: Int? = null
    var labelTimeFormat: String? = null

    var headerColour: Color? = null
    var headerIsShown: Boolean? = null
    var headerTimeFormat: String? = null

    var avatarIsShown: Boolean? = null
    var avatarSize: Int? = null
    var avatarPlaceholder: Drawable? = null

    var inputBackground: Drawable? = null
    var inputHint: String? = null
    var inputTextSize: Int? = null
    var inputTextColour: Color? = null
    var inputMaxLines: Int? = null

    var iconSend: Drawable? = null
    var iconLocation: Drawable? = null
    var iconImage: Drawable? = null
    var iconVideo: Drawable? = null
    var iconVoice: Drawable? = null
    var iconMessageType: Drawable? = null

    var messageTypes: List<Message.Type>? = null
}