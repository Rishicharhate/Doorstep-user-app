package com.example.blinkitclone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkitclone.SupabaseClient
import com.example.blinkitclone.model.Order
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OrdersState {
    object Loading : OrdersState()
    data class Success(val orders: List<Order>) : OrdersState()
    data class Error(val message: String) : OrdersState()
}

class OrdersViewModel : ViewModel() {

    private val _ordersState = MutableStateFlow<OrdersState>(OrdersState.Loading)
    val ordersState: StateFlow<OrdersState> = _ordersState

    fun fetchOrders() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        // Using fallback 'guest_user_123' as defined in OrderPlacedActivity for testing
        val userId = user?.id ?: "fcd6b342-bf46-4d04-9e0d-8080cc1130cb"

        _ordersState.value = OrdersState.Loading
        viewModelScope.launch {
            try {
                val orders = SupabaseClient.client.postgrest["orders"]
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeList<Order>()
                _ordersState.value = OrdersState.Success(orders)
            } catch (e: Exception) {
                _ordersState.value = OrdersState.Error(e.message ?: "Failed to fetch orders")
            }
        }
    }
}
