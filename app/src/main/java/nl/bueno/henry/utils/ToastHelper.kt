package nl.bueno.henry.utils

import android.content.Context
import android.widget.Toast

object ToastHelper {

    private var context : Context? = null

    fun setup(context: Context){
        this.context = context
    }

    fun shortToast(message: String){
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    fun longToast(message: String){
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }
}