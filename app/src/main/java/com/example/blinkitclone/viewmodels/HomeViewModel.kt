package com.example.blinkitclone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkitclone.SupabaseClient
import com.example.blinkitclone.model.Product
import com.example.blinkitclone.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val bestsellerList: List<BestsellerData>) : HomeState()
    data class Error(val message: String) : HomeState()
}

data class BestsellerData(
    val category: String,
    val products: List<Product>
)

class HomeViewModel : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState

    private val _addressState = MutableStateFlow<String?>(null)
    val addressState: StateFlow<String?> = _addressState

    init {
        fetchBestsellers()
        fetchUserAddress()
    }

    fun fetchUserAddress() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        val userId = user?.id ?: "guest_user_123"

        viewModelScope.launch {
            try {
                val profile = SupabaseClient.client.postgrest["profiles"]
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<UserProfile>()
                _addressState.value = profile?.address
            } catch (e: Exception) {
                // Silently fail or handle error for address
            }
        }
    }

    fun fetchBestsellers() {
        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            try {
                val products = SupabaseClient.client.postgrest["admin"]
                    .select()
                    .decodeList<Product>()

                val groupedProducts = products.groupBy { it.productCategory?.trim() ?: "Unknown" }
                    .map { (category, products) ->
                        BestsellerData(category, products)
                    }

                _homeState.value = HomeState.Success(groupedProducts)
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "Failed to fetch products")
            }
        }
    }
}
