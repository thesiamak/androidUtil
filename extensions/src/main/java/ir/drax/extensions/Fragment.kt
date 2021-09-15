package ir.drax.extensions

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ir.drax.modal.Modal
import ir.drax.modal.ModalBuilder
import ir.drax.modal.model.MoButton
import java.io.File


/**
 * Export and share file
 */
fun Fragment.shareFile(file: File, subject: String? = null, text: String? = null) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(requireContext(), requireContext().packageName+".fileprovider", file))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, subject))

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Export and share text
 */
fun Fragment.shareText(subject: String? = null, text: String? = null) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, subject))

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Fragment.copyText(text: String? = null) {
    try {
        val clipboard: ClipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)

        message(R.string.copy_done)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


/**
 * Display message as SnackBar from the top inside fragments
 * */
fun Fragment.message(message: Int, actionText: String = "", callback: View.OnClickListener? = null) {
    message(getString(message), actionText, callback)
}

fun Fragment.message(message: String, actionText: String = "", callback: View.OnClickListener? = null): ModalBuilder {
    val modal = Modal.builder(requireActivity()).apply {
        setMessage(message)
        icon = R.drawable.ic_outline_info_24
        direction = Modal.Direction.Top
        if (actionText.isNotEmpty())
            setCallback(MoButton(actionText, 0) {
                callback?.onClick(it)
                true
            })

    }.build()
    modal.show()
    return modal
}


/**
 * Keep fragment layout in a fullscreen window
 */
fun Fragment.toggleFullScreen(goFullScreen: Boolean, isPortrait: Boolean = true) {
    requireActivity().requestedOrientation =
        if (isPortrait)
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    if (goFullScreen) {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    } else {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
    view?.requestLayout()
}


fun Fragment.openLink(url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(i)
}


/**
 *  Permission dialog
 * */

var REQUIRED_PERMISSIONS = arrayOf<String>()

fun Fragment.requirePermissions(): Boolean {
    // Main part to grant permission.
    return if (REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }) true
    else {
        //ActivityCompat.requestPermissions(requireActivity(),
        requestPermissions(REQUIRED_PERMISSIONS, 100)

        false
    }
}


/***      Show-Hide keyboard functions  ***/
fun hideKeyboard(activity: Activity?) {
    try {
        activity?.let {
            if (activity.currentFocus != null && activity.currentFocus!!.windowToken != null) {
                (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        activity.currentFocus!!.windowToken,
                        0
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun showKeyboard(context: Context?) {
    (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}