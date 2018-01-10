package pl.applover.chatlover.config

import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt

/**
 * Created by sp0rk on 05.01.18.
 */
data class SwipeAction<in T>(
        val name: String,
        @ColorInt val bgColour: Int,
        @ColorInt val textColour: Int,
        val icon: Drawable,
        val action: (T) -> Unit
)