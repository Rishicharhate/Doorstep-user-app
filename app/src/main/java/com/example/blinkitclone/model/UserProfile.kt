package com.example.blinkitclone.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val address: String? = null
)
