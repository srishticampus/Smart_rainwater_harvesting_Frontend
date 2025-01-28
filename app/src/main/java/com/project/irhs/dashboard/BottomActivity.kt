package com.project.irhs.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.project.irhs.R
import com.project.irhs.databinding.ActivityBottomBinding
import com.project.irhs.fragments.HomeContainerFragment
import com.project.irhs.fragments.MonitorFragment
import com.project.irhs.fragments.ProfileFragment
import com.project.irhs.fragments.SettingsFragment

class BottomActivity : AppCompatActivity() {
    lateinit var binding: ActivityBottomBinding

    private val fragmentManger = supportFragmentManager
    private val homeContainerFragment = HomeContainerFragment()
    private val monitorFragment = MonitorFragment()
    private val profileFragment = ProfileFragment()
  //  private val settingFragment = SettingsFragment()
    private var activeFragment: Fragment = homeContainerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle insets for bottom navigation
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigationView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBarsInsets.bottom)
            insets
        }

        fragmentManger.beginTransaction().apply {
            add(R.id.dashBoardFrame, homeContainerFragment, "FragmentHome")
            add(R.id.dashBoardFrame, monitorFragment, "MonitorFragment").hide(monitorFragment)
            add(R.id.dashBoardFrame, profileFragment, "FragmentProfile").hide(profileFragment)
          //  add(R.id.dashBoardFrame, settingFragment, "FragmentSettings").hide(settingFragment)
        }.commit()

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> switchFragment(homeContainerFragment)
                R.id.monitor -> switchFragment(monitorFragment)
                R.id.profile -> switchFragment(profileFragment)
               // R.id.settings -> switchFragment(settingFragment)
                else -> false
            }
        }
    }

    private fun switchFragment(fragment: Fragment): Boolean {
        fragmentManger.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
        return true
    }
}
