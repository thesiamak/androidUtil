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

class Builder(private val activity: Activity):ModalObj() {

    fun setDirection(direction:Modal.Direction):Builder{
        this.direction=direction;return this
    }

    fun setBlurEnabled (blurEnabled:Boolean):Builder{
        this.blurEnabled=blurEnabled
        return this
    }

    fun  setLockVisibility(lockVisibility:Boolean):Builder{
        this.lockVisibility=lockVisibility
        return this
    }
    fun  setListener(listener:Listener):Builder{
        this.listener=listener
        return this
    }
    fun  setType(type:Modal.Type):Builder{
        this.type=type
        return this
    }
    fun  setCallback(callback:MoButton):Builder{
        this.callback=callback
        return this
    }
    fun  setTitle(title:CharSequence):Builder{
        this.title=title
        return this
    }
    fun  setIcon(icon:Int):Builder{
        this.icon=icon
        return this
    }
    fun  setContentView(view:Int):Builder{
        contentView = (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(view,null,false)
        return this
    }
    fun  setContentView(view: View):Builder{
        this.contentView=view
        return this
    }

    fun  setProgress(progress:Int):Builder{
        this.progress=progress
        return this
    }

    fun  setMessage(message:CharSequence):Builder{
        this.message= MoButton(message,0,null)
        return this
    }

    fun  setMessage(message:MoButton):Builder{
        this.message=message
        return this
    }

    fun  setList(list:List<MoButton>):Builder{
        this.list = list
        return this
    }

    fun build():ModalBuilder?{
        try {
            this.root=activity.findViewById(android.R.id.content)

            val view = when(this.type){
                Modal.Type.Alert -> R.layout.modal_alert_layout
                Modal.Type.Progress -> R.layout.modal_progress_layout
                Modal.Type.List -> R.layout.modal_list_layout
                Modal.Type.Custom -> 0
                else -> throw UnsatisfiedParametersException()
            }


            val inflated = if (view != 0)
                (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(view,null,false)
            else
                contentView


            this.modal = when(this.type){
                Modal.Type.Custom -> {
                    val modal = RelativeLayout(ContextThemeWrapper(activity,R.style.modal_root))
                    modal.addView(inflated, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT))
                    modal
                }
                else -> inflated
            }

            return ModalBuilder(this)


        }catch (e:UnsatisfiedParametersException){
            e.printStackTrace()
            return null
        }
    }

}