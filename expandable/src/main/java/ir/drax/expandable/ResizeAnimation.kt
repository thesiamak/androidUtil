package ir.drax.expandable

import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(private val view:View, private val targetHeight:Int, private val startHeight:Int, private val interpolate:Interpolate?=null): Animation() {
    init {
        interpolator=AccelerateDecelerateInterpolator()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        if (interpolatedTime==1f)return // To stop any extra layout changes right after the animation ended-> To avoid threshold bugs
        Log.e("Transform","sss$interpolatedTime")
        val newHeight = startHeight + (targetHeight - startHeight) * interpolatedTime
        //to support decent animation, change new heigt as Nico S. recommended in comments
        //int newHeight = (int) (startHeight+(targetHeight - startHeight) * interpolatedTime);
        interpolate?.interpolate(interpolatedTime)
        view.layoutParams.height = newHeight.toInt()
        view.requestLayout()
    }
}