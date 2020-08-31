package ir.drax.modal

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import ir.drax.modal.model.ModalObj

class Modal {
    companion object {

        enum class Type{ Alert, List, Custom, Progress}
        enum class Direction{ TopToBottom, BottomToTop}

        @JvmStatic
        fun builder(activity: Activity):Builder{
            return Builder(activity)
        }

        @JvmStatic
        fun hide(activity: Activity):Boolean{
            return hide(activity.findViewById<ViewGroup>(android.R.id.content))
        }

        private fun hide(root:ViewGroup):Boolean{
            val bg = getLastView(root) as ViewGroup?
            return if (bg == null) false
            else{
                val modal=bg.getChildAt(bg.childCount-1) as ModalBuilder
                modal.closeModal(bg)
            }

        }

        private fun getLastView(root: ViewGroup):View?{
            val tmp = root.findViewWithTag<View>(ModalObj.ViewIdTag)
            return if (tmp != null){
                tmp.tag="tmp"
                val tmp2=getLastView(root)
                tmp.tag=ModalObj.ViewIdTag
                tmp2 ?: tmp

            } else
                null
        }

    }

}