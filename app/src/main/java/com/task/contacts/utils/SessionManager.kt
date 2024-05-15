package com.task.contacts.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private var sharedPreferencesMode = 0
    private val sharedPreferencesName = "Contacts"
    private var sharedPreferences = context.getSharedPreferences(sharedPreferencesName, sharedPreferencesMode)
    private var editor = sharedPreferences.edit()

    private var emaill = "email"

    val dark_theme = "dark_theme"


    fun setDarkTheme(darkThemeStatus: Boolean) {
        editor.putBoolean(dark_theme, darkThemeStatus)
        editor.commit()
    }

    fun getDarkTheme():Boolean{
        return sharedPreferences.getBoolean(dark_theme,false)
    }









    fun setEmail(email: String) {
        editor.putString(emaill, email)
        editor.commit()
    }

    fun getEmail(): String {
        return sharedPreferences.getString(emaill, "") ?: ""
    }


    fun clearSession() {
        editor.clear()
        editor.apply()
    }

}