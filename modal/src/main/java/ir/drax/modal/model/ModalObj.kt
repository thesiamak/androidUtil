package ir.drax.modal.model

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import ir.drax.modal.Listener
import ir.drax.modal.Modal

open class ModalObj @JvmOverloads constructor(
        open var root:ViewGroup,
        var listener:Listener?=null,
        var modal:View?=null,
        var contentView:View?=null

): LiveData<ModalObj>() {
    companion object{
        @JvmField var VIEW_TAG_ID: String? = this.javaClass.canonicalName
    }

    var changedIndex:Int=-1

    private fun changedIndex(index:Int):ModalObj{
        changedIndex=index
        return this
    }
    var direction=Modal.Direction.BottomToTop
    var blurEnabled:Boolean=false
    var lockVisibility:Boolean=false
    var state:Modal.State= Modal.State.NONE
    var type=Modal.Type.Alert

    var title:CharSequence=""
        set(value) {
            field=value
            postValue(changedIndex(0))
        }

    var message = MoButton("")
        set(value) {
            field=value
            postValue(changedIndex(1))
        }

    var icon=0
        set(value) {
            field=value
            postValue(changedIndex(2))
        }

    var callback: MoButton? =null
        set(value) {
            field=value
            postValue(changedIndex(3))
        }

    var list= listOf<MoButton>()
        set(value) {
            field=value
            postValue(changedIndex(4))
        }

    var progress=0
        set(value) {
            field=value
            postValue(changedIndex(5))
        }


}