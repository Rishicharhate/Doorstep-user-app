package com.example.blinkitclone.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkitclone.model.Product
import com.example.blinkitclone.room.CartDatabase
import com.example.blinkitclone.room.CartEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartDao = CartDatabase.getDatabase(application).cartDao()

    val cartItems: StateFlow<Map<Product, Int>> = cartDao.getAllCartItems()
        .map { entities ->
            entities.associate { entity ->
                Product(
                    id = entity.productId,
                    productTitle = entity.productTitle,
                    productQuantity = entity.productQuantity,
                    productUnit = entity.productUnit,
                    productPrice = entity.productPrice,
                    productStock = entity.productStock,
                    productCategory = entity.productCategory,
                    productType = entity.productType,
                    productImagesUris = entity.productImagesUris?.split(",")?.toCollection(ArrayList())
                ) to entity.itemQuantity
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun addItem(product: Product) {
        viewModelScope.launch {
            val existingItem = cartDao.getCartItemById(product.id!!)
            if (existingItem != null) {
                cartDao.updateCartItem(existingItem.copy(itemQuantity = existingItem.itemQuantity + 1))
            } else {
                cartDao.insertCartItem(
                    CartEntity(
                        productId = product.id!!,
                        productTitle = product.productTitle,
                        productQuantity = product.productQuantity,
                        productUnit = product.productUnit,
                        productPrice = product.productPrice,
                        productStock = product.productStock,
                        productCategory = product.productCategory,
                        productType = product.productType,
                        itemQuantity = 1,
                        productImagesUris = product.productImagesUris?.joinToString(",")
                    )
                )
            }
        }
    }

    fun removeItem(product: Product) {
        viewModelScope.launch {
            val existingItem = cartDao.getCartItemById(product.id!!)
            if (existingItem != null) {
                if (existingItem.itemQuantity > 1) {
                    cartDao.updateCartItem(existingItem.copy(itemQuantity = existingItem.itemQuantity - 1))
                } else {
                    cartDao.deleteCartItem(existingItem)
                }
            }
        }
    }

    fun getTotalItemCount(): Int {
        return cartItems.value.values.sum()
    }

    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCart()
        }
    }
}
