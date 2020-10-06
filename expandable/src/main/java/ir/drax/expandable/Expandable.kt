package ir.drax.expandable

import android.animation.TimeInterpolator
import android.content.Context
import android.os.Build
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.res.ResourcesCompat
import java.util.*

class Expandable @JvmOverloads constructor(context: Context, attrs:AttributeSet?=null, defStyleAttr:Int=0)  : ConstraintLayout(context,attrs,defStyleAttr), Observer {
    val stateObservable = StateObservable(true)
    val COLLAPSE_DURATION=250
    val EXPAND_DURATION=350
    val MARGIN_START=16
    private var childs = listOf<View>()

    init {
        setup()
    }

    private fun setup(){
        LayoutInflater.from(context).inflate(R.layout.expandable_layout,this,true)

        stateObservable.addObserver(this)
        findViewById<View>(R.id.header).setOnClickListener {toggle()}

    }

    override fun update(observable: Observable?, obj: Any?) {
        if (obj as Boolean) enable() else disable()
    }

    private fun enable(){
        findViewById<View>(R.id.header_icon).background=ResourcesCompat.getDrawable(resources,R.drawable.expandable_header_enabled,null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val transition=ChangeBounds()
            transition.apply {
                interpolator= TimeInterpolator {time->
                    childs.onEach {
                        if (time==0f) it.visibility= VISIBLE
                        else it.alpha=time
                    }
                    time
                }
                duration= EXPAND_DURATION.toLong()
            }
            TransitionManager.beginDelayedTransition(this,transition)
        }else{
            childs.onEach {
                it.visibility= VISIBLE
            }
        }
        val before = ConstraintSet();
        before.clone(this);

        for ((index, child) in childs.withIndex()) {
            before.connect(child.id, ConstraintSet.TOP, if(index == 0) R.id.header else childs[index-1].id, ConstraintSet.BOTTOM);
            before.setVisibility(child.id,ConstraintSet.VISIBLE);
            before.setMargin(child.id, ConstraintSet.TOP, MARGIN_START);
        }

        before.applyTo(this);


    }

    private fun disable(){
        findViewById<View>(R.id.header_icon).background=ResourcesCompat.getDrawable(resources,R.drawable.expandable_header_disabled,null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val transition=ChangeBounds()
            transition.apply {
                interpolator= TimeInterpolator {time->
                    childs.onEach {
                        if (time==1f) it.visibility= GONE
                        else it.alpha=1-time
                    }
                    time
                }
                duration=COLLAPSE_DURATION.toLong()
            }


            TransitionManager.beginDelayedTransition(this,transition)
        }else{
            childs.onEach {
                it.visibility= GONE
            }
        }
        val before = ConstraintSet();
        before.clone(this);


        childs.forEach {child->
            before.connect(child.id, ConstraintSet.TOP, R.id.header, ConstraintSet.TOP);
            before.setMargin(child.id, ConstraintSet.TOP, 0);
        }

        before.applyTo(this);

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

            it.visibility = if (stateObservable.state)View.VISIBLE else View.GONE
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