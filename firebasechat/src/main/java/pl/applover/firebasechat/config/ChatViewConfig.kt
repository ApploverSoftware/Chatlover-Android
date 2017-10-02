package pl.applover.firebasechat.config

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.ChatUser
import pl.applover.firebasechat.model.Message

/**
 * Created by sp0rk on 28/08/17.
 */
object ChatViewConfig {
    var bubbleShape: Drawable? = null
    var bubbleColourOwn: Int? = null
    var bubbleColourOther: Int? = null

    var textSize: Float? = null
    var textColour: Int? = null
    var textColourSecondary: Int? = null

    var labelColour: Int? = null
    var labelSize: Float? = null
    var labelTimeFormat: String? = null
    var labelIsShown: Boolean? = null

    var headerColour: Int? = null
    var headerSize: Float? = null
    var headerIsShown: Boolean? = null
    var headerTimeFormat: String? = null

    var avatarIsShown: Boolean? = null
    var avatarSize: Int? = null
    var avatarPlaceholder: Drawable? = null
    var avatarOnClick: ((ChatUser) -> Unit)? = null

    var inputBackground: Drawable? = null
    var inputHint: String? = null
    var inputTextSize: Float? = null
    var inputTextColour: Int? = null
    var inputMaxLines: Int? = null

    var iconSend: Drawable? = null
    var iconLocation: Drawable? = null
//    var iconImage: Drawable? = null
//    var iconVideo: Drawable? = null
//    var iconVoice: Drawable? = null
//    var iconMessageType: Drawable? = null
//    var messageTypes: List<Message.Type>? = null

    var onTxtClick: ((message: Message, context: Context) -> Unit)? = null
    var onTxtLongClick: ((message: Message, context: Context) -> Unit)? = null
    var onLocClick: ((message: Message, context: Context) -> Unit)? = null
    var onLocLongClick: ((message: Message, context: Context) -> Unit)? = null

    var onFragmentViewCreated: ((View?, Channel) -> Unit)? = null
    var onBackPressed: (() -> Unit)? = null
}