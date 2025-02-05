package com.project.irhs.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.project.irhs.R
import com.project.irhs.SharedPreferencesManager
import com.project.irhs.api.ApiUtilities
import com.project.irhs.databinding.FragmentProfileBinding
import com.project.irhs.login.LoginActivity
import com.project.irhs.login.ResetActivity
import com.project.irhs.profile.EditProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var firstNameEt: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        sharedPreferencesManager = SharedPreferencesManager(requireContext())

        binding.editLyt.setOnClickListener(this)
        binding.resetLyt.setOnClickListener(this)
        val userId: String = sharedPreferencesManager.getUserId()
        viewProfile(userId)

        firstNameEt = binding.nameTV

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back press to quit the app
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Exit the app when back is pressed
                    requireActivity().finishAffinity()
                }
            })
        // Logout button logic
        binding.logoutBtn.setOnClickListener {
            // Show a confirmation dialog
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                // Clear shared preferences and logout
                sharedPreferencesManager.saveLoginStatus(false)

                // Navigate to LoginActivity
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.editLyt -> {
                editProfile()
            }

            R.id.resetLyt -> {
                resetPassword()
            }
        }
    }

    private fun editProfile() {
        activity?.let {
            startActivity(Intent(context, EditProfile::class.java))
        }
    }

    private fun resetPassword() {
        activity?.let {
            startActivity(Intent(context, ResetActivity::class.java))
        }
    }

    private fun viewProfile(userId: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val api = ApiUtilities.getInstance()
                val result = api.viewProfile(userId)
                withContext(Dispatchers.Main) {
                    result.body()?.let { root ->
                        if (root.status == true) {
                            val fullName =
                                "${root.userData[0].first_name} ${root.userData[0].last_name}"
                            firstNameEt.text = fullName

                            // Optionally, save updated data to SharedPreferences as well
                            sharedPreferencesManager.saveFirstName(root.userData[0].first_name)
                            sharedPreferencesManager.saveLastName(root.userData[0].last_name)
                        }
                    }
                }
            }
        } catch (_: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        val userId: String = sharedPreferencesManager.getUserId()
        viewProfile(userId)
    }
}
