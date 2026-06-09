package com.example.blinkitclone.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkitclone.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.OtpType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object OtpSent : AuthState()
    object Success : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun checkSession() {
        viewModelScope.launch {
            delay(1500) // Splash delay
            val session = SupabaseClient.client.auth.currentSessionOrNull()
            if (session != null) {
                _authState.value = AuthState.Authenticated
            } else {
                // Change this to AuthState.Authenticated if you still want to skip login for now
                // or AuthState.Unauthenticated to show the login screen
                _authState.value = AuthState.Authenticated 
            }
        }
    }

    fun sendOtp(email: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signInWith(OTP) {
                    this.email = email
                }
                _authState.value = AuthState.OtpSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to send OTP")
            }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.verifyEmailOtp(
                    type = OtpType.Email.EMAIL,
                    email = email,
                    token = otp
                )
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Invalid OTP")
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return SupabaseClient.client.auth.currentSessionOrNull() != null
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
