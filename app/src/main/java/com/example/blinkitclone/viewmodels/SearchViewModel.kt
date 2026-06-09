package com.example.blinkitclone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkitclone.SupabaseClient
import com.example.blinkitclone.model.Product
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val products: List<Product>) : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchViewModel : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState

    private var allProducts: List<Product> = emptyList()

    init {
        fetchAllProducts()
    }

    private fun fetchAllProducts() {
        _searchState.value = SearchState.Loading
        viewModelScope.launch {
            try {
                allProducts = SupabaseClient.client.postgrest["admin"]
                    .select()
                    .decodeList<Product>()
                _searchState.value = SearchState.Success(allProducts)
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Failed to fetch products")
            }
        }
    }

    fun searchProducts(query: String) {
        if (query.isEmpty()) {
            _searchState.value = SearchState.Success(allProducts)
            return
        }

        val filteredList = allProducts.filter { product ->
            product.productTitle?.contains(query, ignoreCase = true) == true ||
            product.productCategory?.contains(query, ignoreCase = true) == true ||
            product.productPrice?.toString()?.contains(query) == true ||
            product.productQuantity?.toString()?.contains(query) == true ||
            product.productUnit?.contains(query, ignoreCase = true) == true
        }
        _searchState.value = SearchState.Success(filteredList)
    }
}
