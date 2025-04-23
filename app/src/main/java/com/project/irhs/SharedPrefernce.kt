package com.project.irhs

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "userId"
        private const val KEY_PHONE_NUMBER = "phone"
        private const val LOGIN_STATUS = "loginStatus"
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun savePhoneNumber(phone: String) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, phone).apply()
    }

    fun getUserId(): String = sharedPreferences.getString(KEY_USER_ID, "") ?: ""
<<<<<<< HEAD
=======
    fun getPhoneNumber(): String = sharedPreferences.getString(KEY_PHONE_NUMBER, "") ?: ""

>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
    fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(LOGIN_STATUS, isLoggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return try {
            sharedPreferences.getBoolean(LOGIN_STATUS, false)
        } catch (e: ClassCastException) {
            // Migrate old data
            val legacyValue = sharedPreferences.getString(LOGIN_STATUS, "false") == "true"
            saveLoginStatus(legacyValue) // Save as Boolean for future use
            legacyValue
        }
    }
<<<<<<< HEAD

=======
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
    // Save updated first and last name
    fun saveFirstName(firstName: String) {
        sharedPreferences.edit().putString("first_name", firstName).apply()
    }

    fun saveLastName(lastName: String) {
        sharedPreferences.edit().putString("last_name", lastName).apply()
    }
<<<<<<< HEAD
=======

>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
}


