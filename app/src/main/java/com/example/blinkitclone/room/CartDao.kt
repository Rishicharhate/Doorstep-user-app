package com.example.blinkitclone.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_table")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartEntity: CartEntity)

    @Update
    suspend fun updateCartItem(cartEntity: CartEntity)

    @Delete
    suspend fun deleteCartItem(cartEntity: CartEntity)

    @Query("DELETE FROM cart_table WHERE productId = :productId")
    suspend fun deleteCartItemById(productId: String)

    @Query("SELECT * FROM cart_table WHERE productId = :productId")
    suspend fun getCartItemById(productId: String): CartEntity?

    @Query("DELETE FROM cart_table")
    suspend fun clearCart()
}
