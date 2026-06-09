package com.example.blinkitclone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkitclone.SupabaseClient
import com.example.blinkitclone.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: UserProfile?) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    fun fetchProfile() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        val userId = user?.id ?: "fcd6b342-bf46-4d04-9e0d-8080cc1130cb"

        viewModelScope.launch {
            try {
                val profile = SupabaseClient.client.postgrest["profiles"]
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<UserProfile>()
                _profileState.value = ProfileState.Success(profile)
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to fetch profile")
            }
        }
    }

    fun saveAddress(address: String) {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        val userId = user?.id ?: "fcd6b342-bf46-4d04-9e0d-8080cc1130cb"

        viewModelScope.launch {
            try {
                val profile = UserProfile(id = userId, address = address)
                SupabaseClient.client.postgrest["profiles"].upsert(profile)
                fetchProfile() // Refresh after save
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to save address")
            }
        }
    }
}
