package com.project.irhs.signup.model

data class Registration(
    val message: String,
    val status: Boolean,
    val userData: List<RegistrationData>
)