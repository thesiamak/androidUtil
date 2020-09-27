package ir.drax.expandable

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.res.ResourcesCompat
import org.w3c.dom.Text
import java.util.*

class Expandable @JvmOverloads constructor(context: Context, attrs:AttributeSet?=null, defStyleAttr:Int=0)  : ConstraintLayout(context,attrs,defStyleAttr), Observer {
    private val observableState = StateObservable(false)
    private val COLLAPSE_DURATION=250
    private val EXPAND_DURATION=350
    private val MARGIN_START=16
    private var expandedHeight=0
    private var collapsedHeight=0
    private var childs = listOf<View>()

    init {
        setup()
    }

    private fun setup(){
        LayoutInflater.from(context).inflate(R.layout.expandable_layout,this,true)

        observableState.addObserver(this)
        findViewById<View>(R.id.header).setOnClickListener {toggle()}

        addOnLayoutChangeListener (object:OnLayoutChangeListener {
            override fun onLayoutChange(view: View, i: Int, top: Int, i2: Int, bottom: Int, i4: Int, i5: Int, i6: Int, oldBottom: Int) {

                val newHeight = bottom - top
                when{
                    collapsedHeight == 0 -> {
                        collapsedHeight = newHeight
                        expandedHeight = newHeight
                        childs.forEach { it.visibility= View.VISIBLE }
                    }

                    newHeight > expandedHeight -> {
                        expandedHeight = newHeight
                        removeOnLayoutChangeListener(this)
                        disable()
                    }
                }
            }
        })

    }

    override fun update(observable: Observable?, obj: Any?) {
        if (obj as Boolean) enable() else disable()
    }

    private fun enable(){
        findViewById<View>(R.id.header_icon).background=ResourcesCompat.getDrawable(resources,R.drawable.expandable_header_enabled,null)

        val resizeAnimation=ResizeAnimation(this,expandedHeight,collapsedHeight)
        resizeAnimation.duration = EXPAND_DURATION.toLong()
        resizeAnimation.startOffset = 200
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
            params.topToBottom = getChildAt(childCount-1).id

            it.visibility = View.GONE
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


    fun collapse() { observableState.state = false }
    fun expand() { observableState.state = true }
    fun toggle() { observableState.state = observableState.state.not() }


}