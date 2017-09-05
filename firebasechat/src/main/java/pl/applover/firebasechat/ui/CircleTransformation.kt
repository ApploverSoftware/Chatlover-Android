package pl.applover.firebasechat.ui

import android.content.Context
import android.graphics.*

import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource

class CircleTransformation(private val pool: BitmapPool) : Transformation<Bitmap> {
    constructor(ctx: Context) : this(Glide.get(ctx).bitmapPool)

    override fun transform(res: Resource<Bitmap>, widthOut: Int, heightOut: Int): Resource<Bitmap> {
        val src = res.get()
        val size = Math.min(src.width, src.height)
        val width = (src.width - size) / 2
        val height = (src.height - size) / 2
        var bitmap: Bitmap? = pool.get(size, size, Bitmap.Config.ARGB_8888)
        bitmap.let {
            bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
        val paint = Paint()
        val canvas = Canvas(bitmap!!)
        val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        if (width != 0 || height != 0) {
            val matrix = Matrix()
            matrix.setTranslate((-width).toFloat(), (-height).toFloat())
            shader.setLocalMatrix(matrix)
        }
        paint.isAntiAlias = true
        paint.shader = shader
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        return BitmapResource.obtain(bitmap, pool)
    }

    override fun getId(): String {
        return "CircleTransformation()"
    }
}