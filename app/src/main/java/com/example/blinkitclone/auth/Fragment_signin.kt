package com.example.blinkitclone.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.viewmodels.AuthState
import com.example.blinkitclone.viewmodels.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class Fragment_signin : Fragment() {

    private lateinit var etUserEmail: TextInputEditText
    private lateinit var continueButton: TextView
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_signin, container, false)

        etUserEmail = view.findViewById(R.id.etUserEmail)
        continueButton = view.findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            val email = etUserEmail.text.toString().trim()

            if (email.isEmpty()) {
                etUserEmail.error = "Enter email"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etUserEmail.error = "Enter valid email"
                return@setOnClickListener
            }

            viewModel.sendOtp(email)
        }

        observeState()

        return view
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        continueButton.isEnabled = false
                    }
                    is AuthState.OtpSent -> {
                        continueButton.isEnabled = true
                        val email = etUserEmail.text.toString().trim()
                        val bundle = Bundle().apply {
                            putString("email", email)
                        }
                        findNavController().navigate(R.id.action_fragment_signin_to_fragment_OTP, bundle)
                        viewModel.resetState()
                    }
                    is AuthState.Error -> {
                        continueButton.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                    else -> {
                        continueButton.isEnabled = true
                    }
                }
            }
        }
    }
}
