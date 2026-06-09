package com.example.blinkitclone.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.activity.UserMainActivity
import com.example.blinkitclone.R
import com.example.blinkitclone.viewmodels.AuthState
import com.example.blinkitclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class Fragment_splash : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe Authentication State
        observeAuthState()

        // Trigger session check
        viewModel.checkSession()
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is AuthState.Authenticated -> {
                            navigateToHome()
                        }
                        is AuthState.Unauthenticated -> {
                            // If you want to show login screen, use this:
                            // findNavController().navigate(R.id.action_fragment_splash_to_fragment_signin)
                            
                            // Currently bypassing login as requested
                            navigateToHome()
                        }
                        is AuthState.Error -> {
                            // Fallback to home even on error for now
                            navigateToHome()
                        }
                        else -> {} // Handle Idle/Loading if needed
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(requireContext(), UserMainActivity::class.java))
        requireActivity().finish()
    }
}
