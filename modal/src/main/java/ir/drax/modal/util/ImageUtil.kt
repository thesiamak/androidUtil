package ir.drax.modal.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import java.util.*

object ImageUtil {
    /**
     * Get bitmap of a view
     *
     * @param view source view
     * @return generated bitmap object
     */
    fun getBitmapFromView(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.width, view.height)
        Log.d("", "combineImages: width: " + view.width)
        Log.d("", "combineImages: height: " + view.height)
        view.draw(canvas)
        return bitmap
    }

    /**
     * Stitch two images one below another
     *
     * @param listOfBitmapsToStitch List of bitmaps to stitch
     * @return resulting stitched bitmap
     */
    fun combineImages(listOfBitmapsToStitch: ArrayList<Bitmap>): Bitmap? {
        var bitmapResult: Bitmap? = null
        var width = 0
        var height = 0
        for (bitmap in listOfBitmapsToStitch) {
            width = Math.max(width, bitmap.width)
            height += bitmap.height
        }
        bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val comboImageCanvas = Canvas(bitmapResult)
        var currentHeight = 0
        for (bitmap in listOfBitmapsToStitch) {
            comboImageCanvas.drawBitmap(bitmap, 0f, currentHeight.toFloat(), null)
            currentHeight += bitmap.height
        }
        return bitmapResult
    }
}