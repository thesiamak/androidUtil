package ir.drax.modal

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.drax.modal.model.ModalObj
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class ModalBuilder @JvmOverloads constructor(var options:ModalObj, val root: ViewGroup, context: Context=root.context, attrs: AttributeSet?=null, defStyleAttr: Int = 0)
    :RelativeLayout(context,attrs,defStyleAttr)
        ,Observer<ModalObj> {

    private var slideAnimation: Animation? = null
    private var bg:ViewGroup
    private var layoutListener: OnGlobalLayoutListener?=null

    private val blurBg :ImageView by lazy {
        ImageView(root.context)
    }
    private var onAnimationFinishedQueue:MutableList<() -> Unit> = mutableListOf()

    private fun drawBlurBg(){
        val screenToCapture = root.drawToBitmap()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                BlurBuilder(root.context)
                        .blur(screenToCapture).let {bitmap ->

                            withContext(Dispatchers.Main){
                                blurBg.setImageBitmap(bitmap)
                            }
                        }

            }catch (e:Exception){
                e.printStackTrace()
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
        bg.addView(this,ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT))


        // Display direction set
        with(FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)){
            gravity =
                    if (options.direction == Modal.Direction.Bottom)
                        Gravity.BOTTOM
                    else
                        Gravity.TOP

            layoutParams = this
        }
    }

    fun hide() = hide(bg,true)

    fun show():ModalBuilder{
        if (slideAnimation != null){
            onAnimationFinishedQueue.add {
                show()
            }

        }else {
            buildModal()
            options.update.observeForever(this)
        }
        return this
    }

    private fun buildModal(){
        if(bg.parent ==null) {
            root.addView(bg, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    if (options.type == Modal.Type.List)
                        findViewById<RecyclerView>(R.id.listScrollView).apply {
                            if (measuredHeight > (bg.height * .7)) {
                                layoutParams.height = (bg.height * .7).toInt()
                                requestLayout()
                            }
                        }


                    // On Animation start
                    if(options.blurEnabled.not())
                        (bg.background as TransitionDrawable).startTransition(options.animationDuration.toInt())
                    else
                        blurEffect(true)
                    // On Animation start


                    visibility = View.VISIBLE

                    animateIn(options.direction){

                        options.listener?.onShow()
                        if (options.type != Modal.Type.Custom) {
                            animateOkBtn()
                        }

                    }
                }
            }.also { layoutListener = it }
            root.viewTreeObserver?.addOnGlobalLayoutListener(layoutListener)
        }
    }

    private fun animateOkBtn() {
        findViewById<View>(R.id.ok)
                .animate()
                .translationY(
                        if (options.direction == Modal.Direction.Bottom)
                            -16f
                        else
                            0f
                )
                .setDuration(options.animationDuration)
                .start()
    }

    fun hide(header:View, forceClose:Boolean=false) : Boolean {
        return if (slideAnimation != null){
            onAnimationFinishedQueue.add {
                hide(header, forceClose)
            }
            false

        }else if (options.lockVisibility.not() || forceClose){

            root.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)

            if(options.blurEnabled.not())
                (header.background as TransitionDrawable).resetTransition()
            else
                blurEffect(false)

            animateOut(options.direction){

                release(header)
                options.listener.let {
                    it?.onDismiss()
                }
            }

            true

        }else
            false
    }

    private fun release(header: View) {
        options.update.removeObserver(this@ModalBuilder)
        root.removeView(header)
    }

    private inline fun animateOut(direction: Modal.Direction, crossinline onEnd:()->Unit = {}) {
        val animation =
                when(direction){
                    Modal.Direction.Bottom -> R.anim.slide_exit_bottom
                    Modal.Direction.Top -> R.anim.slide_exit_top
                }

        slideAnimation = AnimationUtils
                .loadAnimation(context, animation)

        startAnimation(
                slideAnimation?.apply {
                    duration = options.exitAnimationDuration

                    setAnimationListener(object :Animation.AnimationListener{
                        override fun onAnimationStart(p0: Animation?) {}

                        override fun onAnimationEnd(p0: Animation?) {
                            animationFinished(onEnd)
                        }

                        override fun onAnimationRepeat(p0: Animation?) {}

                    })
                }
        )
    }

    private inline fun animationFinished(crossinline onEnd: () -> Unit) {
        onEnd.invoke()
        slideAnimation = null
        onAnimationFinishedQueue
                .lastOrNull()
                ?.let {
                    onAnimationFinishedQueue.clear()
                    it.invoke()
                }
    }

    private inline fun animateIn(direction: Modal.Direction, crossinline onEnd:()->Unit = {}) {
        val animation =
                when(direction){
                    Modal.Direction.Top -> R.anim.slide_enter_top
                    Modal.Direction.Bottom -> R.anim.slide_enter_bottom
                }

        slideAnimation = AnimationUtils
                .loadAnimation(context, animation)

        startAnimation(
                slideAnimation?.apply {

                    duration = options.animationDuration
                    interpolator = FastOutSlowInInterpolator()

                    setAnimationListener(object :Animation.AnimationListener{
                        override fun onAnimationStart(p0: Animation?) {}

                        override fun onAnimationEnd(p0: Animation?) {
                            animationFinished(onEnd)
                        }

                        override fun onAnimationRepeat(p0: Animation?) {}

                    })
                }
        )
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
        background =
                when {
                    options.backgroundDrawable != null -> options.backgroundDrawable
                    options.direction == Modal.Direction.Bottom -> ResourcesCompat.getDrawable(resources,R.drawable.top_curved_header,null)
                    else -> ResourcesCompat.getDrawable(resources,R.drawable.bottom_curved_header,null)
                }
    }


    private fun blurEffect(set:Boolean){
        if (options.blurEnabled){
            //Do some blur magic..
            if(set){
                drawBlurBg()
                animateInBlurView()
            }

            else
                animateOutBlurView()
        }
    }

    private fun animateInBlurView() {
        blurBg.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.alpha_enter)
                        .apply {
                            duration = options.animationDuration
                        }
        )
    }

    private fun animateOutBlurView() {
        blurBg.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.alpha_exit)
                        .apply {
                            duration = options.animationDuration
                        }
        )
    }

    fun isShowing():Boolean = bg.parent != null

    override fun onChanged(obj: ModalObj) {
        val patches: MutableList<() -> Unit> = mutableListOf()

        if(
                obj.title!=options.title ||
                obj.message!=options.message ||
                obj.icon!=options.icon
        ){
            patches += {
                setHeader(bg)
            }
        }

        if(
                obj.callback!=options.callback ||
                obj.onDismiss!=options.onDismiss ||
                obj.onShow!=options.onShow
        ){
            patches += {
                setCallback()
            }
        }

        if(
                obj.list!=options.list
        ){
            patches += {
                setList()
            }
        }

        if(
                obj.progress!=options.progress
        ){
            patches += {
                setProgress()
            }
        }

        options = obj
        patches.forEach {
            it.invoke()
        }
    }

    fun update(patch: ModalObj.()->Unit) = with(options.copy()){
        patch()
        options.update.postValue(this)
    }
}