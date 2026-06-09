package com.example.blinkitclone.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
data class CartEntity(
    @PrimaryKey
    val productId: String,
    val productTitle: String?,
    val productQuantity: Int?,
    val productUnit: String?,
    val productPrice: Int?,
    val productStock: Int?,
    val productCategory: String?,
    val productType: String?,
    val itemQuantity: Int, // Quantity in cart
    val productImagesUris: String? // Store as comma separated or just first one
)
