package com.project.irhs.splash

import android.annotation.SuppressLint
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
                if (sharedPreference.isUserLoggedIn()) {
                    // If user is already logged in, go directly to the home screen
                    startActivity(Intent(this, BottomActivity::class.java))
                } else {
                    // If user is not logged in, proceed to the onboarding screen
                    startActivity(Intent(this, OnBoardActivity::class.java))
                }
                overridePendingTransition(0, 0)
                finish()
            }, splashTimeout
        )

    }
}
