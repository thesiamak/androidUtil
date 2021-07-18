package ir.drax.modal.model

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ir.drax.modal.Listener
import ir.drax.modal.Modal
import java.util.*
import kotlin.properties.ObservableProperty

data class ModalObj(
        var type:Modal.Type=Modal.Type.Alert
) {

    /*fun copy() = ModalObj().let {
        it.listener = listener
        it.modal = modal
        it.contentView = contentView
        it.onShow = onShow
        it.onDismiss = onDismiss
        it.blurEnabled = blurEnabled
        it.direction = direction
        it.lockVisibility = lockVisibility
        it.type = type
        it.title = title
        it.message = message
        it.icon = icon
        it.callback = callback
        it.list = list
        it.progress = progress
        it
    }*/
    var listener:Listener?=null
    var modal:View?=null
    var contentView:View?=null
    var onShow:()->Unit={}
    var onDismiss:()->Unit={}
    var blurEnabled:Boolean=false
    var direction=Modal.Direction.BottomToTop
    var lockVisibility:Boolean=false
    var title:CharSequence=""
    var message = MoButton("")
    var icon=0
    var callback: MoButton? =null
    var list= listOf<MoButton>()
    var progress=0

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


    var update:MutableLiveData<ModalObj> = MutableLiveData<ModalObj>()

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
}