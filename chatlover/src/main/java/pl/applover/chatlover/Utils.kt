package pl.applover.chatlover

import android.content.Context
import android.content.res.Resources
import android.location.Geocoder
import android.support.annotation.DrawableRes
import com.gturedi.views.CustomStateOptions
import com.gturedi.views.R
import com.gturedi.views.StatefulLayout
import java.util.*
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream


/**
 * Created by sp0rk on 28/08/17.
 */
/*
For geoData in strings saved as Decimal Degrees (sep. by "/"), e.g. 32.04512/-12.3
 */
fun String.toAddressAsync(context: Context, completion: (address: String) -> Unit) = with(split("/")) {
    val lat = get(0).toDouble()
    val lon = get(1).toDouble()
    val lines = Geocoder(context, Locale.getDefault()).getFromLocation(lat, lon, 1)[0]
    var address = ""
    for (i in 0..lines.maxAddressLineIndex) {
        address += lines.getAddressLine(i) + "\n"
    }
    completion(address.trim())
}

fun StatefulLayout.showEmpty(message: String, @DrawableRes image: Int?) {
    showCustom(CustomStateOptions()
            .message(message)
            .image(image ?: R.drawable.stf_ic_empty))
}

fun convertDpToPixel(dp: Float): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return Math.round(px)
}