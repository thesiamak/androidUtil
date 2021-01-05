package ir.drax.modal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.CycleInterpolator
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.drax.modal.model.ModalObj

class ModalBuilder @JvmOverloads constructor(val state:ModalObj, context: Context?=state.root.context):RelativeLayout(context),Observer<ModalObj> {

    private var bg:ViewGroup

    private val root=state.root

    init {
        addView(state.modal, LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT))
        tag=state.direction
        visibility= View.INVISIBLE
        bg =FrameLayout(state.root.context)
        bg.tag=ModalObj.VIEW_TAG_ID
        setViewDirection()
        setHeader(bg)
        setProgress()
        setCallback()
        setList()
        bg.background=ResourcesCompat.getDrawable(resources,R.drawable.modal_root_transition,null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bg.elevation=100f
        }
        bg.setOnClickListener{closeModal(bg)}
        bg.addView(this)

    }

    fun  hide(){
        closeModal(bg,true)
    }

    fun show(){
        when(state.state){
            Modal.State.CLOSING -> {
                Modal.State.OPENING
                animateUp()
            }
            in Modal.State.OPENED..Modal.State.OPENING -> { }
            else -> {
                state.state = Modal.State.OPENING
                buildModal()
            }
        }

        state.observeForever(this)

    }
    private fun buildModal(){
        if(bg.parent ==null) {
            root.addView(bg, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            root.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    if (state.state != Modal.State.OPENING) return

                    val fLayoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    if (state.direction == Modal.Direction.BottomToTop) {
                        fLayoutParams.gravity = Gravity.BOTTOM
                        fLayoutParams.bottomMargin = -height
                    } else {
                        fLayoutParams.gravity = Gravity.TOP
                        fLayoutParams.topMargin = -height
                    }

                    layoutParams = fLayoutParams

                    if (state.type == Modal.Type.List)
                        findViewById<RecyclerView>(R.id.listScrollView).apply {
                            if (measuredHeight > (bg.height * .7)) {
                                layoutParams.height = (bg.height * .7).toInt()
                                requestLayout()
                            }
                        }

                   animateUp()
                }
            })
        }else{

        }
    }
    val openingAnim = animate()
            .setStartDelay(250)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    visibility = View.VISIBLE
                    (bg.background as TransitionDrawable).startTransition(250)

                    super.onAnimationStart(animation)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    state.listener?.onShow()
                    if (state.type != Modal.Type.Custom) {
                        findViewById<View>(R.id.ok)
                                .animate()
                                .translationY(
                                        if (state.direction == Modal.Direction.BottomToTop)
                                            -16f
                                        else
                                            0f
                                )
                                .setDuration(400)
                                .setInterpolator(CycleInterpolator(0.1f))
                                .start()
                    }
                    state.state = Modal.State.OPENED
                    super.onAnimationEnd(animation)
                    blurEffect(true)
                }
            })
            .translationY(
                    if ((state.direction) == Modal.Direction.BottomToTop)
                        -height.toFloat()
                    else
                        height.toFloat())
            .setDuration(500)

    private fun animateUp() {
       closingAnim.cancel()
       openingAnim.cancel()
       openingAnim.start()
    }

    private fun setHeader(header:ViewGroup){
        if (state.type != Modal.Type.Custom){
            if (state.message.displayText.isEmpty().not()){
                val summary=findViewById<TextView>(R.id.text)
                summary.text=state.message.displayText
                summary.setCompoundDrawablesWithIntrinsicBounds(state.message.iconResourceId,0,0,0)
                summary.setOnClickListener {
                    if(state.message.clickListener(summary))
                        closeModal(header)
                }
            }


            findViewById<TextView>(R.id.title).text = state.title

            if (state.icon==0) findViewById<ImageView>(R.id.icon).visibility= View.GONE
            else findViewById<ImageView>(R.id.icon).setImageResource(state.icon)

            if (state.lockVisibility)
                findViewById<ImageView>(R.id.close).visibility= View.INVISIBLE

        }
    }
    private var headerView:View? = null
    val closingAnim = animate().translationY(
            if((this.tag as Modal.Direction)==Modal.Direction.BottomToTop)
                height.toFloat()
            else
                -height.toFloat())
            .setDuration(250)
            .setListener(object :AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    if (state.state != Modal.State.CLOSING)return

                    (headerView?.background as TransitionDrawable).resetTransition()
                    state.removeObserver(this@ModalBuilder)
                    root.removeView(headerView)
                    blurEffect(false)
                    state.listener.let {
                        it?.onDismiss()
                    }
                    state.state = Modal.State.CLOSED
                    super.onAnimationEnd(animation)
                }
            })

    fun closeModal(header:View, forceClose:Boolean=false) : Boolean{
        return if (state.lockVisibility.not() || forceClose){
            headerView =header
            state.state = Modal.State.CLOSING
            openingAnim.cancel()
            closingAnim.cancel()
            closingAnim.start()

            true

        }else
            false
    }

    private fun setCallback(){
        if (state.type!=Modal.Type.Custom){
            state.callback?.let { cb ->
                findViewById<TextView>(R.id.ok).apply {
                    text = cb.displayText
                    setCompoundDrawablesWithIntrinsicBounds(cb.iconResourceId,0,0,0)
                    setOnClickListener {
                        if (cb.clickListener(this))
                            closeModal(bg)

                    }
                }
            }

        }
    }

    private fun setList(){
        if (state.type==Modal.Type.List){
            val doneBtnView=findViewById<TextView>(R.id.ok)
            findViewById<RecyclerView>(R.id.listScrollView).apply {
                if (adapter==null){
                    layoutManager=LinearLayoutManager(context)
                    adapter=ListAdapter(doneBtnView.typeface,state.list){position->
                        closeModal(bg,true)
                    }
                }else
                    (adapter as ListAdapter).setMedicines(state.list)
            }
        }
    }

    private fun setProgress(){
        if (state.type==Modal.Type.Progress){
            findViewById<TextView>(R.id.percentage).text=state.progress.toString()
            val progress=findViewById<ProgressBar>(R.id.progress)
            progress.isIndeterminate=state.progress==0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progress.setProgress(state.progress,true)

            }else
                progress.progress=state.progress
        }
    }

    private fun setViewDirection(){
        background = if (state.direction==Modal.Direction.BottomToTop)
            ResourcesCompat.getDrawable(resources,R.drawable.top_curved_header,null)
        else
            ResourcesCompat.getDrawable(resources,R.drawable.bottom_curved_header,null)
    }


    private fun blurEffect(set:Boolean){
        if (state.blurEnabled){
            //Do some blur magic..
        }
    }

    fun isShowing():Boolean = bg.parent != null

    override fun onChanged(obj: ModalObj?) {
        when (obj!!.changedIndex){
            in 0..2 -> setHeader(bg)
            3 -> setCallback()
            4 -> setList()
            5 -> setProgress()
        }
    }
}