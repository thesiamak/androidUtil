package ir.drax.modal

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import ir.drax.modal.model.ModalObj

class Modal {
    enum class Type{ Alert, List, Custom, Progress}
    enum class Direction{ TopToBottom, BottomToTop}

    companion object{
        @JvmStatic
        fun builder(activity: Activity):Builder{
            return Builder(activity.findViewById(android.R.id.content))
        }

        @JvmStatic
        fun builder(anyVisibleView: View):Builder{
            return Builder(anyVisibleView.rootView.findViewById(android.R.id.content))
        }

        @JvmStatic
        fun hide(activity: Activity):Boolean{
            return hide(activity.findViewById<ViewGroup>(android.R.id.content))
        }

        @JvmStatic
        fun hide(anyVisibleView: View):Boolean{
            return hide(anyVisibleView.rootView.findViewById<ViewGroup>(android.R.id.content))
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
            val tmp = root.findViewWithTag<View>(ModalObj.VIEW_TAG_ID)
            return if (tmp != null){
                tmp.tag="tmp"
                val tmp2=getLastView(root)
                tmp.tag=ModalObj.VIEW_TAG_ID
                tmp2 ?: tmp

            } else
                null
        }

    }
}

abstract class Listener{
    open fun onDismiss(){}
    open fun onShow(){}
}