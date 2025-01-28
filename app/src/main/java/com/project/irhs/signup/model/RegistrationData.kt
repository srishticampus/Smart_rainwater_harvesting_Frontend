package com.project.irhs.signup.model

data class RegistrationData(
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val phone: String,
    val photo: String,
    val userid: String
)