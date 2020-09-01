package ir.drax.modal

import android.app.Activity
import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import ir.drax.modal.model.MoButton
import ir.drax.modal.model.ModalObj
import ir.drax.modal.model.UnsatisfiedParametersException

class Builder(val activity: Activity) {
    val modalObj=ModalObj()


    fun setDirection(direction:Modal.Direction):Builder{
        modalObj.direction=direction;return this
    }

    fun setBlurEnabled (blurEnabled:Boolean):Builder{
        modalObj.blurEnabled=blurEnabled
        return this
    }

    fun  setLockVisibility(lockVisibility:Boolean):Builder{
        modalObj.lockVisibility=lockVisibility
        return this
    }
    fun  setListener(listener:Listener):Builder{
        modalObj.listener=listener
        return this
    }
    fun  setType(type:Modal.Type):Builder{
        modalObj.type=type
        return this
    }
    fun  setCallback(callback:MoButton):Builder{
        modalObj.reAction=callback
        return this
    }
    fun  setTitle(title:CharSequence):Builder{
        modalObj.title=title
        return this
    }
    fun  setIcon(icon:Int):Builder{
        modalObj.icon=icon
        return this
    }
    fun  setContentView(view:Int):Builder{
        modalObj.contentView=view
        return this
    }

    fun  setProgress(progress:Int):Builder{
        modalObj.progress=progress
        return this
    }

    fun  setMessage(message:CharSequence):Builder{
        modalObj.message= MoButton(message,0,null)
        return this
    }

    fun  setMessage(message:MoButton):Builder{
        modalObj.message=message
        return this
    }

    fun  setList(list:List<MoButton>):Builder{
        modalObj.list = list
        return this
    }

    fun build():ModalBuilder?{
        try {
            modalObj.root=activity.findViewById(android.R.id.content)

            val view = when(modalObj.type){
                Modal.Type.Alert -> R.layout.modal_alert_layout
                Modal.Type.Progress -> R.layout.modal_progress_layout
                Modal.Type.List -> R.layout.modal_list_layout
                Modal.Type.Custom -> modalObj.contentView!!
                else -> throw UnsatisfiedParametersException()
            }



            val inflated = (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(view,null,false)


            modalObj.modal = when(modalObj.type){
                Modal.Type.Custom -> {
                    val modal = RelativeLayout(ContextThemeWrapper(activity,R.style.modal_root))
                    modal.addView(inflated, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT))
                    modal
                }
                else -> inflated
            }

            return ModalBuilder(modalObj)


        }catch (e:UnsatisfiedParametersException){
            e.printStackTrace()
            return null
        }
    }

}