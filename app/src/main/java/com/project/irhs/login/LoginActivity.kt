package com.project.irhs.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.irhs.R
import com.project.irhs.SharedPreferencesManager
import com.project.irhs.api.ApiUtilities
import com.project.irhs.dashboard.BottomActivity
import com.project.irhs.databinding.ActivityLoginBinding
import com.project.irhs.signup.SignupActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreference: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = SharedPreferencesManager(applicationContext)
        binding.hidePassword.setOnClickListener(this)
        val text = "New to Rain Sensing Sign Up"

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
                intent = Intent(applicationContext, SignupActivity::class.java)
                startActivity(intent)
            }
        }
        spannableString.setSpan(
            clickableSpan,
            19,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.forgotTv.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        binding.newtoRainSensingTv.text = spannableString

        binding.newtoRainSensingTv.movementMethod = LinkMovementMethod.getInstance()

        binding.loginSubmit.setOnClickListener {
            val params = HashMap<String?, String>()
            params["phone"] = binding.userName.text.toString()
            params["password"] = binding.loginPassword.text.toString()

            if (checkAllFields()) {
                lifecycleScope.launch(Dispatchers.IO) {

                    val response = ApiUtilities.getInstance().userLogin(params)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result?.status == true) {

                                result.userData[0].let {
                                    sharedPreference.saveUserId(it.userid)
                                    sharedPreference.savePhoneNumber(it.phone)
                                }
                                sharedPreference.saveLoginStatus(true)
                                intent = Intent(this@LoginActivity, BottomActivity::class.java)
                                startActivity(intent)
                                finish()

                                Toast.makeText(
                                    applicationContext,
                                    "User Logged in successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    result?.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                        } else {
                            Toast.makeText(
                                applicationContext,
                                response.message(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
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

        // Phone
        if (binding.userName.length() == 0) {
            binding.userName.error = "Number is required"
            isValid = false
        }
        // Password
        if (binding.loginPassword.length() == 0) {
            binding.loginPassword.error = "Password is required"
            isValid = false
        } else if (binding.loginPassword.length() < 4) {
            binding.loginPassword.error = "Password must be minimum 8 characters"
            isValid = false
        }
        return isValid
    }

    private fun showHidePassWord(v: View?) {
        if (v?.id == R.id.hidePassword) {
            if (binding.loginPassword.transformationMethod
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
                binding.hidePassword.setImageResource(R.drawable.ic_hide_eyes)
                binding.loginPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.hidePassword.setImageResource(R.drawable.ic_password_eye)
                binding.loginPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.hidePassword -> {
                showHidePassWord(v)
            }
        }
    }
}
