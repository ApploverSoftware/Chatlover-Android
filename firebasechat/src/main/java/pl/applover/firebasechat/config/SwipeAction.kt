package pl.applover.firebasechat.config

import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt

/**
 * Created by sp0rk on 05.01.18.
 */
data class SwipeAction<in T>(
        val name: String,
        @ColorInt val colour: Int,
        val icon: Drawable,
        val action: (T)->Unit
)