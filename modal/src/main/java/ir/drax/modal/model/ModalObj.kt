package ir.drax.modal.model

import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.MutableLiveData
import ir.drax.modal.Listener
import ir.drax.modal.Modal

data class ModalObj(
        private val source:ModalObj? = null,
        var type:Modal.Type = source?.type ?: Modal.Type.Alert
) {

    var listener:Listener?= source?.listener
    var modal:View?= source?.modal
    var contentView:View? = source?.contentView
    var onShow:()->Unit= source?.onShow ?: {}
    var onDismiss:()->Unit= source?.onDismiss ?:{}
    var blurEnabled:Boolean= source?.blurEnabled ?: false
    var direction:Modal.Direction = source?.direction ?: Modal.Direction.Bottom
    var lockVisibility:Boolean= source?.lockVisibility ?: false
    var title:CharSequence= source?.title ?: ""
    var message:MoButton = source?.message ?: MoButton("")
    var icon:Int = source?.icon?: 0
    var callback: MoButton? = source?.callback
    var list:List<MoButton> = source?.list ?: listOf()
    var progress:Int = source?.progress ?: 0
    var backgroundDrawable:Drawable? = source?.backgroundDrawable

    var animationDuration:Long = source?.animationDuration ?: 250L
    var animationStartDelay:Long = source?.animationStartDelay ?: 0L

    init {
        if (listener == null) {
            listener = object:Listener(){
                override fun onDismiss() {
                    super.onDismiss()
                    onDismiss.invoke()
                }

                override fun onShow() {
                    super.onShow()
                    onShow.invoke()
                }
            }
        }


    }


    var update = MutableLiveData<ModalObj>()

    class Builder{
        private val modalObj = ModalObj()

        fun blurEnabled(isBlurEffectEnabled: Boolean) = apply { this.modalObj.blurEnabled = isBlurEffectEnabled }
        fun listener(listener: Listener) = apply { this.modalObj.listener = listener }
        fun onShow(onShow: ()->Unit) = apply { this.modalObj.onShow = onShow }
        fun onDismiss(onDismiss: ()->Unit) = apply { this.modalObj.onDismiss = onDismiss }
        fun direction(direction: Modal.Direction) = apply { this.modalObj.direction = direction }
        fun lockVisibility(lockVisibility: Boolean) = apply { this.modalObj.lockVisibility = lockVisibility }
        fun title(title: CharSequence) = apply { this.modalObj.title = title }
        fun message(message: String) = apply { this.modalObj.message = MoButton(message) }
        fun message(message: MoButton) = apply { this.modalObj.message = message }
        fun icon(icon: Int) = apply { this.modalObj.icon = icon }
        fun callback(callback: MoButton) = apply { this.modalObj.callback = callback }

        fun build() = modalObj
    }

    companion object{
        @JvmField
        var VIEW_TAG_ID: String? = this.javaClass.canonicalName
    }


    fun copy() = ModalObj(source = this)

}