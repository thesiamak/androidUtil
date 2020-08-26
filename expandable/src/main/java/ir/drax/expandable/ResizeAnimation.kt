package ir.drax.expandable

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation

class ResizeAnimation(val view:View, val targetHeight:Int, val startHeight:Int, val interpolate:Interpolate?=null): Animation() {
    init {
        interpolator=AccelerateDecelerateInterpolator()
    }

    override fun willChangeBounds(): Boolean {
        return true
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        val newHeight = startHeight + (targetHeight - startHeight) * interpolatedTime
        //to support decent animation, change new heigt as Nico S. recommended in comments
        //int newHeight = (int) (startHeight+(targetHeight - startHeight) * interpolatedTime);
        interpolate?.interpolate(interpolatedTime)
        view.layoutParams.height = newHeight.toInt()
        view.requestLayout()
    }
}