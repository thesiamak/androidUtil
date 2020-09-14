package ir.drax.expandable

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.res.ResourcesCompat
import java.util.*

class Expandable @JvmOverloads constructor(context: Context, attrs:AttributeSet?=null, defStyleAttr:Int=0)  : ConstraintLayout(context,attrs,defStyleAttr), Observer {
    val stateObservable = StateObservable(false)
    val COLLAPSE_DURATION=250
    val EXPAND_DURATION=350
    val MARGIN_START=16
    private var expandedHeight=0
    private var collapsedHeight=0
    private var childs = listOf<View>()

    init {
        setup()
    }

    private fun setup(){
        LayoutInflater.from(context).inflate(R.layout.expandable_layout,this,true)

        stateObservable.addObserver(this)
        findViewById<View>(R.id.header).setOnClickListener {toggle()}

        addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
            val newHeight = bottom - top
            when {
                collapsedHeight == 0 -> {
                    expandedHeight = newHeight
                    collapsedHeight = newHeight
                    childs.forEach { it.visibility = View.VISIBLE }
                }

                newHeight > expandedHeight -> {
                    if (stateObservable.state)
                        enable(expandedHeight,newHeight)
                    else
                        disable()
                    expandedHeight = newHeight
                }
            }
        }

    }

    override fun update(observable: Observable?, obj: Any?) {
        if (obj as Boolean) enable() else disable()
    }

    private fun enable(){enable(collapsedHeight,expandedHeight)}
    private fun enable(from:Int,to:Int){
        findViewById<View>(R.id.header_icon).background=ResourcesCompat.getDrawable(resources,R.drawable.expandable_header_enabled,null)

        val resizeAnimation=ResizeAnimation(this,to,from)
        resizeAnimation.duration = EXPAND_DURATION.toLong()
        resizeAnimation.startOffset = 200
        resizeAnimation.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                layoutParams.height = LayoutParams.WRAP_CONTENT
            }

            override fun onAnimationRepeat(p0: Animation?) {}

        })
        startAnimation(resizeAnimation)
    }

    private fun disable(){
        findViewById<View>(R.id.header_icon).background=ResourcesCompat.getDrawable(resources,R.drawable.expandable_header_disabled,null)

        if (collapsedHeight > 0){
            val resizeAnimation=ResizeAnimation(this,collapsedHeight,expandedHeight)
            resizeAnimation.duration=COLLAPSE_DURATION.toLong()
            startAnimation(resizeAnimation)
        }
    }

    fun addChild(vararg views:View ):Expandable{
        childs = childs + views
        drawChilds(views.toList())
        return this
    }

    private fun drawChilds(views:List<View>){
        views.forEach {
            val params = Constraints.LayoutParams(0,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            params.marginStart = MARGIN_START
            params.startToStart = Constraints.LayoutParams.PARENT_ID
            params.endToEnd = Constraints.LayoutParams.PARENT_ID
            params.endToEnd = Constraints.LayoutParams.PARENT_ID
            params.topToBottom = getChildAt(childCount-1).id

            it.visibility = if(collapsedHeight==0) View.GONE else View.VISIBLE
            if (childCount > 1) params.topMargin = MARGIN_START
            if (it.id == -1) it.id= System.currentTimeMillis().plus(childCount).toInt()

            addView(it,params)
        }
    }

    var title:String=""
        set (newTitle){
            field=newTitle
            findViewById<TextView>(R.id.title).text =  newTitle }

    var icon:Int= 0
        set (newIcon){
            field=newIcon
            findViewById<ImageView>(R.id.header_icon).setImageResource(newIcon)
        }


    fun collapse() { stateObservable.state = false }
    fun expand() { stateObservable.state = true }
    fun toggle() { stateObservable.state = stateObservable.state.not() }


}