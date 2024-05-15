package com.task.contacts.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.task.contacts.databinding.CustomLoaderLayoutBinding
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class Loader @Inject constructor(@ActivityContext var context: Context) {
    val dialog = Dialog(context)
    var binding = CustomLoaderLayoutBinding.inflate(LayoutInflater.from(context), null, false)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun show() {
        binding.message.visibility = View.GONE
        dialog.show()
    }

    fun showWithMessage(message: String) {
        binding.message.visibility = View.VISIBLE
        binding.message.text = message
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}