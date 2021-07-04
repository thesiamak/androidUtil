package ir.drax.modal

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.CycleInterpolator
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.drax.modal.model.ModalObj
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModalBuilder @JvmOverloads constructor(val options:ModalObj, val root: ViewGroup, context: Context=root.context, attrs: AttributeSet?=null, defStyleAttr: Int = 0):RelativeLayout(context,attrs,defStyleAttr)
        ,Observer<ModalObj> {

    private var bg:ViewGroup
    private val ANIMATION_DURATION=250L
    private var openingAnim:ViewPropertyAnimator?=null
    private var closingAnim:ViewPropertyAnimator?=null
    private var layoutListener: OnGlobalLayoutListener?=null

    private val blurBg = ImageView(context).apply {
        alpha = 0f
        GlobalScope.launch(Dispatchers.IO) {
            BlurBuilder(context)
                    .blur(
                            root.drawToBitmap()
                    ).let {bitmap->
                        withContext(Dispatchers.Main){
                            setImageBitmap(bitmap)
                        }
                    }
        }
    }

    init {
        addView(options.modal, LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT))
        tag=options.direction
        visibility= View.INVISIBLE
        bg =FrameLayout(root.context)
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
        bg.setOnClickListener{ hide(bg) }
        bg.addView(blurBg,ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
        bg.addView(this)

    }

    fun  hide(){
        hide(bg,true)
    }

    fun show():ModalBuilder{
        buildModal()
        options.observeForever(this)
        return this
    }

    private fun buildModal(){
        if(bg.parent ==null) {
            root.addView(bg, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            layoutListener =object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val fLayoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    if (options.direction == Modal.Direction.BottomToTop) {
                        fLayoutParams.gravity = Gravity.BOTTOM
                        fLayoutParams.bottomMargin = -height
                    } else {
                        fLayoutParams.gravity = Gravity.TOP
                        fLayoutParams.topMargin = -height
                    }

                    layoutParams = fLayoutParams

                    if (options.type == Modal.Type.List)
                        findViewById<RecyclerView>(R.id.listScrollView).apply {
                            if (measuredHeight > (bg.height * .7)) {
                                layoutParams.height = (bg.height * .7).toInt()
                                requestLayout()
                            }
                        }

                    openingAnim = animate()
                            .setStartDelay(ANIMATION_DURATION)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationStart(animation: Animator?) {
                                    visibility = View.VISIBLE
                                    (bg.background as TransitionDrawable).startTransition(ANIMATION_DURATION.toInt())

                                    super.onAnimationStart(animation)
                                }

                                override fun onAnimationEnd(animation: Animator?) {
                                    options.listener?.onShow()
                                    if (options.type != Modal.Type.Custom) {
                                        findViewById<View>(R.id.ok)
                                                .animate()
                                                .translationY(
                                                        if (options.direction == Modal.Direction.BottomToTop)
                                                            -16f
                                                        else
                                                            0f
                                                )
                                                .setDuration(400)
                                                .setInterpolator(CycleInterpolator(0.1f))
                                                .start()
                                    }
                                    super.onAnimationEnd(animation)
                                }
                            })
                            .translationY(
                                    if ((options.direction) == Modal.Direction.BottomToTop)
                                        -height.toFloat()
                                    else
                                        height.toFloat())
                            .setDuration(2*ANIMATION_DURATION)
                    blurEffect(true)
                    openingAnim?.start()
                }
            }
            root.viewTreeObserver?.addOnGlobalLayoutListener(layoutListener)

        }
    }

    fun hide(header:View, forceClose:Boolean=false) : Boolean{
        return if (options.lockVisibility.not() || forceClose){
            root.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
            closingAnim = animate().translationY(
                    if((this.tag as Modal.Direction)==Modal.Direction.BottomToTop)
                        height.toFloat()
                    else
                        -height.toFloat())
                    .setDuration(ANIMATION_DURATION)
                    .setListener(object :AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: Animator?) {
                            (header.background as TransitionDrawable).resetTransition()
                            options.removeObserver(this@ModalBuilder)
                            root.removeView(header)
                            blurEffect(false)
                            options.listener.let {
                                it?.onDismiss()
                            }
                            super.onAnimationEnd(animation)
                        }
                    })
            closingAnim?.start()

            true

        }else
            false
    }

    private fun setHeader(header:ViewGroup){
        if (options.type != Modal.Type.Custom){
            if (options.message.displayText.isEmpty().not()){
                val summary=findViewById<TextView>(R.id.text)
                summary.text=options.message.displayText
                summary.setCompoundDrawablesWithIntrinsicBounds(options.message.iconResourceId,0,0,0)
                summary.setOnClickListener {
                    if(options.message.clickListener(summary))
                        hide(header,true)
                }
            }


            findViewById<TextView>(R.id.title).text = options.title

            if (options.icon==0) findViewById<ImageView>(R.id.icon).visibility= View.GONE
            else findViewById<ImageView>(R.id.icon).setImageResource(options.icon)

            if (options.lockVisibility)
                findViewById<ImageView>(R.id.close).visibility= View.INVISIBLE

        }
    }

    private fun setCallback(){
        if (options.type!=Modal.Type.Custom){
            options.callback?.let { cb ->
                findViewById<TextView>(R.id.ok).apply {
                    text = cb.displayText
                    setCompoundDrawablesWithIntrinsicBounds(cb.iconResourceId,0,0,0)
                    setOnClickListener {
                        if (cb.clickListener(this))
                            hide(bg,true)

                    }
                }
            }

        }
    }

    private fun setList(){
        if (options.type==Modal.Type.List){
            val doneBtnView=findViewById<TextView>(R.id.ok)
            findViewById<RecyclerView>(R.id.listScrollView).apply {
                if (adapter==null){
                    layoutManager=LinearLayoutManager(context)
                    adapter=ListAdapter(doneBtnView.typeface,options.list){ position->
                        hide(bg,true)
                    }
                }else
                    (adapter as ListAdapter).setMedicines(options.list)
            }
        }
    }

    private fun setProgress(){
        if (options.type==Modal.Type.Progress){
            findViewById<TextView>(R.id.percentage).text=options.progress.toString()
            val progress=findViewById<ProgressBar>(R.id.progress)
            progress.isIndeterminate=options.progress==0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progress.setProgress(options.progress,true)

            }else
                progress.progress=options.progress
        }
    }

    private fun setViewDirection(){
        background = if (options.direction==Modal.Direction.BottomToTop)
            ResourcesCompat.getDrawable(resources,R.drawable.top_curved_header,null)
        else
            ResourcesCompat.getDrawable(resources,R.drawable.bottom_curved_header,null)
    }


    private fun blurEffect(set:Boolean){
        if (options.blurEnabled){
            //Do some blur magic..
            if(set)
                blurBg.animate().setDuration(ANIMATION_DURATION).alpha(1f).start()

            else
                blurBg.animate().setDuration(ANIMATION_DURATION).alpha(0f).start()
        }
    }

    fun isShowing():Boolean = bg.parent != null

    override fun onChanged(obj: ModalObj) {
        when (obj.changedIndex){
            in 0..2 -> setHeader(bg)
            3 -> setCallback()
            4 -> setList()
            5 -> setProgress()
        }
    }

    fun update(obj: ModalObj)= with(obj){
        when{
            title!=options.title ||
            message!=options.message ||
            icon!=options.icon -> setHeader(bg)

            callback != options.callback -> setCallback()
            list != options.list -> setList()
            progress != options.progress -> setProgress()

        }
    }
}