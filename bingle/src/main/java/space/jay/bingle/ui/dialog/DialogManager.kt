package space.jay.bingle.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import space.jay.bingle.R

class DialogManager {

    fun show(activityContext: Context,
             icon: Drawable?,
             title: CharSequence?,
             content: CharSequence?,
             button1: CharSequence,
             button1Listener: () -> Unit,
             dismissListener: DialogInterface.OnDismissListener?,
             log: String){
        show(activityContext, icon, title, content, button1, button1Listener, null, {}, null, {}, dismissListener, 1, log)
    }

    fun show(activityContext: Context,
             icon: Drawable?,
             title: CharSequence?,
             content: CharSequence?,
             button1: CharSequence,
             button1Listener: () -> Unit,
             button2: CharSequence,
             button2Listener: () -> Unit,
             dismissListener: DialogInterface.OnDismissListener?,
             log: String){
        show(activityContext, icon, title, content, button1, button1Listener, button2, button2Listener, null, {}, dismissListener, 2, log)
    }

    fun show(activityContext: Context,
             icon: Drawable?,
             title: CharSequence?,
             content: CharSequence?,
             button1: CharSequence,
             button1Listener: () -> Unit,
             button2: CharSequence,
             button2Listener: () -> Unit,
             button3: CharSequence,
             button3Listener: () -> Unit,
             dismissListener: DialogInterface.OnDismissListener?,
             log: String){
        show(activityContext, icon, title, content, button1, button1Listener, button2, button2Listener, button3, button3Listener, dismissListener, 3, log)
    }

    @SuppressLint("InflateParams")
    private fun show(activityContext: Context,
                     icon: Drawable?,
                     title: CharSequence?,
                     content: CharSequence?,
                     button1: CharSequence?,
                     button1Listener: () -> Unit,
                     button2: CharSequence?,
                     button2Listener: () -> Unit,
                     button3: CharSequence?,
                     button3Listener: () -> Unit,
                     dismissListener: DialogInterface.OnDismissListener?,
                     type: Int,
                     log: String){

        Log.d(javaClass.simpleName, "$type / $log / $title / $content")

        val view = LayoutInflater.from(activityContext).inflate(R.layout.dialog_basic, null)

        val dialog = AlertDialog.Builder(activityContext).let {
            it.setView(view)
            it.setOnDismissListener(dismissListener)
            it.setCancelable(false)
            it.create()
        }

//        icon?.let {drawable ->
//            val iconView = view.findViewById<ImageView>(R.id.imageView_basic_icon)
//            Glide.with(activityContext).load(drawable).into(iconView)
//            iconView.visibility = View.VISIBLE
//        }

        setString(view, R.id.textView_basic_title, title)
        setString(view, R.id.textView_basic_content, content)
        setButton(view, R.id.button_basic_1, button1, button1Listener, dialog)
        setButton(view, R.id.button_basic_2, button2, button2Listener, dialog)
        setButton(view, R.id.button_basic_3, button3, button3Listener, dialog)

        dialog.show()
    }

    private fun setString(view: View, id: Int, string: CharSequence?){
        string?.let {
            val textView = view.findViewById<TextView>(id)
            textView.text = string
            textView.visibility = View.VISIBLE
        }
    }

    private fun setButton(view: View, id: Int, string: CharSequence?, listener: () -> Unit?, dialog: AlertDialog){
        if (string != null){
            val button = view.findViewById<Button>(id)
            button.text = string
            button.setOnClickListener{
                listener.invoke()
                dialog.dismiss()
            }
            button.visibility = View.VISIBLE
        }
    }

    inline fun Context.toast(message: () -> String) {
        Toast.makeText(this, message(), Toast.LENGTH_SHORT).show()
    }
}