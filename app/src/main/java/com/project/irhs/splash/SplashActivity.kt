package com.project.irhs.splash

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.project.irhs.onboard.OnBoardActivity
import com.project.irhs.R
import com.project.irhs.SharedPreferencesManager
import com.project.irhs.dashboard.BottomActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreference: SharedPreferencesManager
    private val splashTimeout = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        sharedPreference = SharedPreferencesManager(this)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                val nextActivity = if (sharedPreference.isUserLoggedIn()) {
                    BottomActivity::class.java
                } else {
                    OnBoardActivity::class.java
                }

                // Create animation options
                val options = ActivityOptions.makeCustomAnimation(
                    this,
                    android.R.anim.fade_in,   // Enter animation
                    android.R.anim.fade_out   // Exit animation
                )

                // Start activity with animation
                startActivity(Intent(this, nextActivity), options.toBundle())

                finish() // Close splash activity
            }, splashTimeout
        )
    }
}
