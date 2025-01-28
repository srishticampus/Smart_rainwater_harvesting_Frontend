package com.project.irhs.login.model

data class LoginResponse(
    val message: String,
    val status: Boolean,
    val userData: List<LoginData>
)