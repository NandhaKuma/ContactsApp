package com.task.contacts.dialog


import android.content.Context

import android.widget.Toast
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


class Alert @Inject constructor(@ActivityContext var context: Context) {

    fun makeToastDefault(message: String){
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}

