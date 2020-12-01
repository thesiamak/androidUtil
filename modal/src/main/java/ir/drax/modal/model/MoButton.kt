package ir.drax.modal.model

import android.view.View

typealias MoClickListener = (button: View) -> Boolean
open class MoButton(val displayText:CharSequence="", val iconResourceId:Int=0, val clickListener:MoClickListener={ true })
class JvmMoButton(val text:CharSequence="", val icon:Int=0,callBack:OnClickListener?=null):MoButton(text,icon,{ callBack?.onClick(null)?:true })

interface OnClickListener{
    fun onClick(view: View?):Boolean
}