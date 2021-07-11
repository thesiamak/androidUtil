package ir.drax.modal

import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import ir.drax.modal.model.MoButton
import ir.drax.modal.model.ModalObj

class Builder(private var root: ViewGroup, private val options:ModalObj) {

    var listener:Listener?
        get() = options.listener
        set(value){
            options.listener = value
        }

    var contentView:View?
        get() = options.contentView
        set(value){
            options.contentView = value
        }

    var onShow:()->Unit
        get() = options.onShow
        set(value){
            options.onShow = value
        }
    var onDismiss:()->Unit
        get() = options.onDismiss
        set(value){
            options.onDismiss = value
        }

    var blurEnabled:Boolean
        get() = options.blurEnabled
        set(value){
            options.blurEnabled = value
        }

    var direction:Modal.Direction
        get() = options.direction
        set(value){
            options.direction = value
        }

    var lockVisibility:Boolean
        get() = options.lockVisibility
        set(value){
            options.lockVisibility = value
        }

    var type:Modal.Type
        get() = options.type
        set(value){
            options.type = value
        }

    var title:CharSequence
        get() = options.title
        set(value){
            options.title = value
        }

    var messageBtn:MoButton
        get() = options.message
        set(value){
            options.message = value
        }

    var message:CharSequence
        get() = options.message.displayText
        set(value){
            options.message = MoButton(value,options.message.iconResourceId,options.message.clickListener)
        }

    var icon
        get() = options.icon
        set(value){
            options.icon = value
        }

    var callback: MoButton?
        get() = options.callback
        set(value){
            options.callback = value
        }

    var list:List<MoButton>
        get() = options.list
        set(value){
            options.list = value
        }

    var progress:Int
        get() = options.progress
        set(value){
            options.progress = value
        }

    fun setDirection(direction:Modal.Direction):Builder{
        options.direction=direction;return this
    }

    fun setBlurEnabled (blurEnabled:Boolean):Builder{
        options.blurEnabled=blurEnabled
        return this
    }

    fun  setLockVisibility(lockVisibility:Boolean):Builder{
        options.lockVisibility=lockVisibility
        return this
    }
    fun  setListener(listener:Listener):Builder{
        options.listener=listener
        return this
    }
    fun  setType(type:Modal.Type):Builder{
        options.type=type
        return this
    }
    fun  setCallback(callback:MoButton):Builder{
        options.callback=callback
        return this
    }
    fun  setTitle(title:CharSequence):Builder{
        options.title=title
        return this
    }
    fun  setIcon(icon:Int):Builder{
        options.icon=icon
        return this
    }
    fun  setContentView(view:Int):Builder{
        options.contentView = View.inflate(root.context,view,null)
        return this
    }
    fun  setContentView(view: View):Builder{
        options.contentView=view
        return this
    }

    fun  setProgress(progress:Int):Builder{
        options.progress=progress
        return this
    }

    fun  setMessage(message:CharSequence):Builder{
        options.message= MoButton(message,0)
        return this
    }

    fun  setMessage(message:MoButton):Builder{
        options.message=message
        return this
    }

    fun  setList(list:List<MoButton>):Builder{
        options.list = list
        return this
    }

    fun build():ModalBuilder{
            val view = when(options.type){
                Modal.Type.Alert -> R.layout.modal_alert_layout
                Modal.Type.Progress -> R.layout.modal_progress_layout
                Modal.Type.List -> R.layout.modal_list_layout
                Modal.Type.Custom -> 0
                else -> R.layout.modal_alert_layout
            }


            val inflated = if (view != 0)
                View.inflate(root.context,view,null)
            else
                options.contentView


            options.modal = when(options.type){
                Modal.Type.Custom -> {
                    val modal = RelativeLayout(ContextThemeWrapper(root.context,R.style.modal_root))
                    modal.addView(inflated, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT))
                    modal
                }
                else -> inflated
            }

            return ModalBuilder(options,root)
    }

}