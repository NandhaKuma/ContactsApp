package com.task.contacts.utils


import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.task.contacts.R
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

class CommonClass {


    companion object {


        fun closeKeyboard(view: View, context: Context) {
            val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun clearAppDataAndCache(context: Context) {
            val packageName = context.packageName

            try {
                val packageManager = context.packageManager
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)

                // Clearing data
                val clearDataIntent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                clearDataIntent.data = android.net.Uri.parse("package:$packageName")
                context.startActivity(clearDataIntent)

                // Clearing cache
                val clearCacheIntent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                clearCacheIntent.data = android.net.Uri.parse("package:$packageName")
                context.startActivity(clearCacheIntent)

                // Note: The above code opens the App info screen, and the user needs to manually clear data and cache.
                // Clearing data and cache programmatically requires the app to have appropriate permissions, which are usually not granted to third-party apps.

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
