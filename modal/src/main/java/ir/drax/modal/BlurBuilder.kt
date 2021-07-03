package ir.drax.modal

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import kotlin.math.roundToInt

class BlurBuilder(private val context: Context) {
    private val BITMAP_SCALE = 0.4f
    private val BLUR_RADIUS = 4.5f

    fun blur(originalImage: Bitmap): Bitmap {
        val width = (originalImage.width * BITMAP_SCALE).roundToInt()
        val height = (originalImage.height * BITMAP_SCALE).roundToInt()
        val inputBitmap: Bitmap = Bitmap.createScaledBitmap(originalImage, width, height, false)
        val outputBitmap: Bitmap = Bitmap.createBitmap(inputBitmap)
        val rs: RenderScript = RenderScript.create(context)
        val theIntrinsic: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn: Allocation = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut: Allocation = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(BLUR_RADIUS)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }
}