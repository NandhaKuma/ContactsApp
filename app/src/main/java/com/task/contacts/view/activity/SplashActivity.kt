package com.task.contacts.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.task.contacts.R
import com.task.contacts.databinding.ActivitySplashBinding
import com.task.contacts.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var activitySplashBinding: ActivitySplashBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            // Call your function here
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Finish the current activity if needed
            finish()
        }, 3000)

    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.getDarkTheme()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}