package com.project.irhs.onboard

import OnboardAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.project.irhs.R
import com.project.irhs.SharedPreferencesManager
import com.project.irhs.dashboard.BottomActivity
import com.project.irhs.databinding.ActivityOnBoardBinding
import com.project.irhs.signup.SignupActivity

class OnBoardActivity : AppCompatActivity() {
    lateinit var binding: ActivityOnBoardBinding
    lateinit var viewPager: ViewPager2
    lateinit var onboardAdapter: OnboardAdapter
    private lateinit var sharedPreference: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sharedPreference = SharedPreferencesManager(this)

        if (sharedPreference.isUserLoggedIn()) {
            startActivity(Intent(this, BottomActivity::class.java))
            finish()
            return
        }
        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupOnboardingScreens()

    }

    private fun setupOnboardingScreens() {
        val items = listOf(
            PagerData(
                R.drawable.rain_light,
                "Rain Status",
                "Rain status refers to the real-time monitoring of precipitation levels in a given area.",
                12f
            ),
            PagerData(
                R.drawable.tank_lid,
                "Tank lid Status",
                " Tank Lid Status refers to the current position or condition of the lid on a water storage tank.",
                12f
            ),
            PagerData(
                R.drawable.water_level,
                " Water Level",
                "Water Level refers to the amount of water present in a tank or reservoir, typically measured in percentage or specific units like meters or liters.",
                12f
            ),
            PagerData(
                R.drawable.mdi_rain_chance,
                "Rain Status",
                "Rainfall Status refers to the real-time monitoring of rain conditions in a specific area.",
                12f
            ),
            PagerData(
                R.drawable.ph,
                "PH Level",
                "pH Level measures the acidity or alkalinity of water or soil on a scale from 0 to 14, with 7 being neutral.",
                12f
            ),
            PagerData(
                R.drawable.plant,
                "Plant watering Status",
                "Plant Watering Status refers to the current state of irrigation or watering activities for plants.",
                12f
            )
        )
        viewPager = binding.viewPager
        onboardAdapter = OnboardAdapter((items))
        viewPager.adapter = onboardAdapter
        binding.indicator.setViewPager(viewPager)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Check if it is the last page
                if (position == items.size - 1) {
                    binding.nextTv.text = "Finish"  // Change "Next" to "Finish"
                } else {
                    binding.nextTv.text = "Next"  // Reset to "Next" on other pages
                }
            }
        })
        // Next button click listener
        binding.nextTv.setOnClickListener {
            if (viewPager.currentItem < items.size - 1) {
                // Move to the next page
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                // Handle finish action (e.g., move to another activity)
                finishSetup()
            }
        }
        //skip btn click
        binding.skipTv.setOnClickListener {
            finishSetup()
        }
    }

    // Handle the finish action when the last page is reached
    private fun finishSetup() {
        intent = Intent(applicationContext, SignupActivity::class.java)
        startActivity(intent)
        finish()
    }
}