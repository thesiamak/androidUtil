package ir.drax.util

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ir.drax.modal.Listener
import ir.drax.modal.Modal
import ir.drax.modal.ModalBuilder
import ir.drax.modal.model.JvmMoButton
import ir.drax.modal.model.MoButton
import ir.drax.modal.model.OnClickListener
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var customModal: ModalBuilder

    private val updateProgressModal by lazy {
        Modal.builder(this).apply {
            type = Modal.Type.Progress
            title = "Updating .."
            onDismiss = {

                Toast.makeText(this@MainActivity,"Dismissed!",Toast.LENGTH_SHORT).show()
            }
            setMessage("Update message ..")
        }.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Modal.init {
            blurEnabled = true
        }

        initExpandable()
        initListExpandable()
    }

    fun simpleModal(view: View?){
        Modal
                .builder(this).apply {
                    title = "Hiii!"
                    message = "Message is here!\n second line of message as well! :) "
                    blurEnabled = false

                }
                .build()
                .show()
    }

    @SuppressLint("ResourceType")
    fun openFragmentModal(view: View?) {
        val layout = FrameLayout(this)
        layout.id = 1
        val fm = supportFragmentManager.beginTransaction()
        Modal.builder(this).apply {
            onDismiss = {
                Toast.makeText(this@MainActivity,"Dismissed!",Toast.LENGTH_SHORT).show()
            }

            type = Modal.Type.Custom
            contentView = layout
        }.build().show()

        fm.replace(layout.id, MyFragment())
        fm.commit()
    }

    fun openListModal(view: View?) {
        val buttonList: MutableList<JvmMoButton> = ArrayList()
        buttonList.add(JvmMoButton(Html.fromHtml("Repair service:  <i>$2506 Dollars</i>"), R.drawable.ic_build_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize servifce :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_content_cut_black_24dp, null))
        buttonList.add(JvmMoButton(Html.fromHtml("Resize service :  <i>$1500 Dollars</i>"), R.drawable.ic_brush_black_24dp, object : OnClickListener {
            override fun onClick(view: View?): Boolean {
                Toast.makeText(this@MainActivity, "sssss", Toast.LENGTH_SHORT).show()
                return true
            }
        }))
        buttonList.add(JvmMoButton(Html.fromHtml("Discount :  <u>$500 Dollars</u>"), R.drawable.ic_mood_black_24dp, null))
        val listModal = Modal.builder(this)
                .setType(Modal.Type.List)
                .setTitle("Sample list modal")
                .setIcon(R.drawable.ic_gesture_black_24dp)
                .setList(buttonList)
                .setMessage(JvmMoButton("2706 Total", R.drawable.ic_attach_money_black_24dp, null))
                .setCallback(JvmMoButton("Share", R.drawable.ic_share_black_24dp, object : OnClickListener {
                    override fun onClick(view: View?): Boolean {
                        Toast.makeText(this@MainActivity, "Throw an intent here ...", Toast.LENGTH_SHORT).show()
                        return true //Hide modal..
                    }
                }))
                .build().show()

        listModal.update {
            title = "Sample title patched!"
            list = listModal.options.list + MoButton("Last Item(patched as well)")
        }

    }

    override fun onBackPressed() {
        if (!Modal.hide(this)) super.onBackPressed()
    }

    fun openModalWithCustomBackground(view: View?) {
        customModal = Modal.builder(view!!) //                    .setDirection(Modal.Direction.Top)
                .setTitle("A Custom Background!")
                .setMessage("We have passed a drawable file and it's the new background.\nLooks good!")
                .setBlurEnabled(false)
                .setDirection(Modal.Direction.Top)
                .setBackground(ResourcesCompat.getDrawable(resources,R.drawable.custom_bg,null))
                .build()

        customModal.show()
        customModal.hide()
        customModal.hide()
        customModal.hide()
        customModal.hide()
        customModal.show()
    }

    fun hideModal(view: View?) {
        onBackPressed()
    }

    private var progressBuilder: ModalBuilder? = null
    fun openProgressModal(view: View?) {
        if (progressBuilder == null) {
            progressBuilder = Modal.builder(view!!)
                    .setLockVisibility(true)
                    .setListener(object : Listener() {
                        override fun onShow() {
                            Thread {
                                while (progressBuilder!!.options.progress < 99) {
                                    try {
                                        Thread.sleep(100)
                                        runOnUiThread { progressBuilder!!.options.progress = progressBuilder!!.options.progress + 1 }
                                    } catch (e: InterruptedException) {
                                        e.printStackTrace()
                                    }
                                }
                                runOnUiThread { progressBuilder!!.hide() }
                            }.start()
                        }
                    })
                    .setType(Modal.Type.Progress)
                    .setTitle("Uploading")
                    .setMessage("Uploading file: readme.txt")
                    .setIcon(R.drawable.ic_baseline_cloud_queue_24)
                    .setProgress(0)
                    .build()
        }
        progressBuilder!!.show()
    }

    private fun initExpandable() {
        /*val expandable = findViewById<WaterfallExpandable>(R.id.waterfallExpandableList)
        val textView = TextView(this)
        textView.text = "Heyyy, I'm a Textview with a message.\nClick me to add more views..."
        textView.setTextColor(Color.WHITE)
        textView.setOnClickListener { v: View? ->
            val textView2 = TextView(this)
            textView2.text = "I'm a NEW Textview with a message.\nA second line is provided as well."
            textView2.setTextColor(Color.WHITE)
            expandable.addChild(textView2)
        }
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.ic_close_black_12dp)
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(resources.getColor(R.color.colorAccent)))
        imageView.setOnClickListener { v: View? -> expandable.collapse() }
        expandable.title = "My expandable list"
        expandable.icon = R.drawable.ic_build_black_24dp
        expandable.addChild(textView, imageView)*/
    }

    private fun initListExpandable() {
        /*val expandable = findViewById<Expandable>(R.id.myExpandableList)
        val recyclerView = ListView(this)
        val itemsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        recyclerView.adapter = itemsAdapter
        recyclerView.setBackgroundColor(Color.WHITE)
        val textView = TextView(this)
        textView.text = "Click me to add more list items ..."
        textView.setTextColor(Color.WHITE)
        textView.setOnClickListener { v: View? -> itemsAdapter.add("New item!") }
        expandable.title = "My recyclerView expandable list"
        expandable.icon = R.drawable.ic_build_black_24dp
        expandable.addChild(textView, recyclerView)
        expandable.stateObservable.addObserver { observable: Observable?, o: Any -> if (o as Boolean) Toast.makeText(this@MainActivity, "Event observer catched expand status ", Toast.LENGTH_LONG).show() }*/
    }

    //    @WithPermission(permission = {Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE})
    fun testFunc() {
        Toast.makeText(this, "testttt", Toast.LENGTH_SHORT).show()
    }

    private fun bindPerm(activity: MainActivity, vararg permissions: String) {
        while (ContextCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED) if (ContextCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, permissions[1]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, permissions[2]) == PackageManager.PERMISSION_GRANTED) activity.testFunc() else Toast.makeText(activity, "nok", Toast.LENGTH_LONG).show()
    }

    companion object {
        private var mGetContent: ActivityResultLauncher<Array<String>>? = null
        private fun runPermission(mainActivity: MainActivity) {
            mGetContent = mainActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(),
                    ActivityResultCallback<Map<String?, Boolean?>?> { })
        }
    }
}