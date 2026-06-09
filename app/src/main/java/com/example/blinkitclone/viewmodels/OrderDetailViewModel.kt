package com.example.blinkitclone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkitclone.SupabaseClient
import com.example.blinkitclone.model.Order
import com.example.blinkitclone.model.OrderItem
import com.example.blinkitclone.model.Product
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OrderDetailState {
    object Loading : OrderDetailState()
    data class Success(val order: Order, val products: List<Pair<Product, Int>>) : OrderDetailState()
    data class Error(val message: String) : OrderDetailState()
}

class OrderDetailViewModel : ViewModel() {

    private val _orderDetailState = MutableStateFlow<OrderDetailState>(OrderDetailState.Loading)
    val orderDetailState: StateFlow<OrderDetailState> = _orderDetailState

    fun fetchOrderDetails(orderId: String) {
        _orderDetailState.value = OrderDetailState.Loading
        viewModelScope.launch {
            try {
                // 1. Fetch the Order itself to get status
                val order = SupabaseClient.client.postgrest["orders"]
                    .select {
                        filter {
                            eq("id", orderId)
                        }
                    }
                    .decodeSingle<Order>()

                // 2. Fetch OrderItems
                val orderItems = SupabaseClient.client.postgrest["order_items"]
                    .select {
                        filter {
                            eq("order_id", orderId)
                        }
                    }
                    .decodeList<OrderItem>()

                if (orderItems.isEmpty()) {
                    _orderDetailState.value = OrderDetailState.Success(order, emptyList())
                    return@launch
                }

                // 3. Fetch Products
                val productIds = orderItems.map { it.product_id }
                val products = SupabaseClient.client.postgrest["admin"]
                    .select {
                        filter {
                            isIn("id", productIds)
                        }
                    }
                    .decodeList<Product>()

                // 4. Map together
                val result = orderItems.mapNotNull { item ->
                    val product = products.find { it.id == item.product_id }
                    if (product != null) {
                        product to item.item_quantity
                    } else {
                        null
                    }
                }

                _orderDetailState.value = OrderDetailState.Success(order, result)

            } catch (e: Exception) {
                _orderDetailState.value = OrderDetailState.Error(e.message ?: "Failed to fetch order details")
            }
        }
    }
}
