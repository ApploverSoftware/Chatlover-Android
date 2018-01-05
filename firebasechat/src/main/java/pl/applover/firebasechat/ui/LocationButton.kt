package pl.applover.firebasechat.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.Toast
import pl.applover.firebasechat.R
import pl.applover.firebasechat.config.ChatViewConfig

/**
 * Created by sp0rk on 24/08/17.
 */
class LocationButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    var anim: ObjectAnimator? = null

    lateinit var activity: Activity
    lateinit var manager: LocationManager
    lateinit var onSend: (l: Location?) -> Unit
    var timeout: Long = 5000

    fun setup(activity: Activity, manager: LocationManager, timeout: Long, onSend: (l: Location?) -> Unit) {
        this.activity = activity
        this.manager = manager
        this.onSend = onSend
        this.timeout = timeout
        setOnClickListener { requestLocation() }
    }

    private fun startAnim() {
        isClickable = false
        anim = ObjectAnimator.ofPropertyValuesHolder(this,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f))
                .setDuration(150).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }

    fun requestLocation() {
        startAnim()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, object : LocationListener {
                override fun onLocationChanged(l: Location?) = onLocationFound(l)
                override fun onStatusChanged(s: String?, p1: Int, p2: Bundle?) {}
                override fun onProviderEnabled(s: String?) {}
                override fun onProviderDisabled(s: String?) {}
            }, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    fun onLocationFound(l: Location?) {
        Toast.makeText(context, ChatViewConfig.locationFoundText, Toast.LENGTH_SHORT).show()
        setOnClickListener { sendLocation(l) }
        anim?.cancel()
        anim = null
        setImageResource(R.drawable.ic_send)
        Handler().postDelayed({
            setOnClickListener { requestLocation() }
            setImageResource(R.drawable.ic_location)
            isClickable = true
        }, timeout)
    }

    fun sendLocation(l: Location?) {
        onSend(l)
        isClickable = true
        setImageResource(R.drawable.ic_location)
    }
}