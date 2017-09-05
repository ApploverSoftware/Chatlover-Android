package pl.applover.firebasechat

import android.content.Context
import android.location.Geocoder
import java.util.*

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