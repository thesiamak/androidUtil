package ir.drax.modal.model

import android.view.View

class MoButton @JvmOverloads constructor(val text:CharSequence="", val icon:Int=0, val clickListener:OnClickListener?=null)

interface OnClickListener{
    fun onClick(view: View):Boolean
}