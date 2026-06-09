package com.example.blinkitclone.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String? = null,
    val user_id: String,
    val total_amount: Int,
    val status: String = "Placed",
    val date: String? = null,
    val created_at: String? = null
)

@Serializable
data class OrderItem(
    val id: String? = null,
    val order_id: String,
    val product_id: String,
    val item_quantity: Int,
    val price: Int
)
