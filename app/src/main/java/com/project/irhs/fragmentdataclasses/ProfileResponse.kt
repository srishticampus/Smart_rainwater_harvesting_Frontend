package com.project.irhs.fragmentdataclasses

data class ProfileResponse(
    val message: String,
    val status: Boolean,
    val userData: List<ProfileData>
)