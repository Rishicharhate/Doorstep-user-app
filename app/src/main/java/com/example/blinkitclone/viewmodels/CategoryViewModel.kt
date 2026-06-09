package com.example.blinkitclone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkitclone.SupabaseClient
import com.example.blinkitclone.model.Product
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CategoryState {
    object Loading : CategoryState()
    data class Success(val products: List<Product>) : CategoryState()
    data class Error(val message: String) : CategoryState()
}

class CategoryViewModel : ViewModel() {

    private val _categoryState = MutableStateFlow<CategoryState>(CategoryState.Loading)
    val categoryState: StateFlow<CategoryState> = _categoryState

    fun fetchProductsByCategory(category: String) {
        _categoryState.value = CategoryState.Loading
        // Trim the category string to handle trailing spaces from Constants.kt
        val trimmedCategory = category.trim()
        
        viewModelScope.launch {
            try {
                val products = SupabaseClient.client.postgrest["admin"]
                    .select {
                        filter {
                            eq("productCategory", trimmedCategory)
                        }
                    }
                    .decodeList<Product>()

                _categoryState.value = CategoryState.Success(products)
            } catch (e: Exception) {
                _categoryState.value = CategoryState.Error(e.message ?: "Failed to fetch products")
            }
        }
    }
}
