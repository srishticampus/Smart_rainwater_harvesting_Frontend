package com.project.irhs.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.irhs.api.ApiUtilities
import com.project.irhs.databinding.ActivitySignupBinding
import com.project.irhs.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = "Already Registered Login"
        val spannableString = SpannableString(text)

        spannableString.setSpan(
            ForegroundColorSpan(0xFF525252.toInt()),
            0,
            18,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#1196CB")),
            19,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //Login text click
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                intent = Intent(this@SignupActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        spannableString.setSpan(
            clickableSpan,
            18,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.alredyRegistered.text = spannableString

        binding.alredyRegistered.movementMethod = LinkMovementMethod.getInstance()

        //API call
        binding.signupButton.setOnClickListener {

            val signupParams = HashMap<String?, String>()
            signupParams["first_name"] = binding.firstName.text.toString()
            signupParams["last_name"] = binding.lastName.text.toString()
            signupParams["phone"] = binding.phoneNumber.text.toString()
            signupParams["email"] = binding.email.text.toString()
            signupParams["password"] = binding.password.text.toString()
//    signupParams["image"]

            if (checkAllFields()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val response = ApiUtilities.getInstance().userSignup(signupParams)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result?.status == true) {

                                //result.userData.get(0)

                                intent = Intent(this@SignupActivity, LoginActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(
                                    applicationContext,
                                    "User Registration successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    result?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Enter the details", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun checkAllFields(): Boolean {

        var isValid = true

        // Reset errors
        binding.firstName.error = null
        binding.lastName.error = null
        binding.phoneNumber.error = null
        binding.email.error = null
        binding.password.error = null

        // Pattern for alphabetic characters only
        val namePattern = "^[A-Za-z]+$"
        val nameWithSpacePattern = "^[A-Za-z ]+$"

        // First Name
        val firstName = binding.firstName.text.toString()
        if (firstName.isEmpty()) {
            binding.firstName.error = "Name is required"
            isValid = false
        } else if (firstName.length < 2 || firstName.length > 20) {
            binding.firstName.error = "First name must be between 2 and 20 characters"
            isValid = false
        } else if (!firstName.matches(namePattern.toRegex())) {
            binding.firstName.error = "Invalid name. Only alphabetic characters allowed"
            isValid = false
        }

        // Last Name
        val lastName = binding.lastName.text.toString()
        if (lastName.isEmpty()) {
            binding.lastName.error = "Name is required"
            isValid = false
        } else if (lastName.isEmpty() || lastName.length > 20) {
            binding.lastName.error = "Last name must be between 2 and 20 characters"
            isValid = false
        } else if (!lastName.matches(nameWithSpacePattern.toRegex())) {
            binding.lastName.error = "Invalid name. Only alphabetic characters and spaces allowed"
            isValid = false
        }

        // Phone
        val phoneNumber = binding.phoneNumber.text.toString()
        val phonePattern = "^[+]?[0-9]{10}$"
        val isValidPhone = phoneNumber.matches(phonePattern.toRegex())
        if (phoneNumber.isEmpty()) {
            binding.phoneNumber.error = "Phone number is required"
            isValid = false
        } else if (!isValidPhone) {
            binding.phoneNumber.error = "Enter a valid phone number"
            isValid = false
        }

        // Email
        if (binding.email.length() == 0) {
            binding.email.error = "Email is required"
            isValid = false
        } else if (!isEmailValid(binding.email.text.toString())) {
            binding.email.error = "Invalid email address"
            isValid = false
        } else if (!binding.email.text.toString().matches(Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]+$"))) {
            binding.email.error = "Only lowercase alphanumeric characters allowed"
            isValid = false
        }

        // Password validation
        val password = binding.password.text.toString()
        val passwordPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!])[A-Za-z\\d@#\$%^&+=!]{6,8}$"

        if (password.isEmpty()) {
            binding.password.error = "Password is required"
            isValid = false
        } else if (password.length < 6 || password.length > 8) {
            binding.password.error = "Password must be between 6 and 8 characters"
            isValid = false
        } else if (!password.matches(passwordPattern.toRegex())) {
            binding.password.error =
                "Password must contain at least one uppercase letter, one special character, and one number"
            isValid = false
        }

        return isValid
    }

    fun isEmailValid(email: CharSequence?): Boolean {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}