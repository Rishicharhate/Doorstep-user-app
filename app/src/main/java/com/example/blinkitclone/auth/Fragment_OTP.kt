package com.example.blinkitclone.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.blinkitclone.activity.UserMainActivity
import com.example.blinkitclone.R
import com.example.blinkitclone.viewmodels.AuthState
import com.example.blinkitclone.viewmodels.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class Fragment_OTP : Fragment() {

    private lateinit var emailTextView: TextView
    private lateinit var loginButton: AppCompatButton

    private lateinit var otp1: TextInputEditText
    private lateinit var otp2: TextInputEditText
    private lateinit var otp3: TextInputEditText
    private lateinit var otp4: TextInputEditText
    private lateinit var otp5: TextInputEditText
    private lateinit var otp6: TextInputEditText

    private var email: String = ""
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment__o_t_p,
            container,
            false
        )

        emailTextView = view.findViewById(R.id.phoneNumberTextView)

        otp1 = view.findViewById(R.id.otp_digit_1)
        otp2 = view.findViewById(R.id.otp_digit_2)
        otp3 = view.findViewById(R.id.otp_digit_3)
        otp4 = view.findViewById(R.id.otp_digit_4)
        otp5 = view.findViewById(R.id.otp_digit_5)
        otp6 = view.findViewById(R.id.otp_digit_6)

        loginButton = view.findViewById(R.id.loginButton)

        email = arguments?.getString("email") ?: ""

        emailTextView.text = email

        setupOtpInputs()

        loginButton.setOnClickListener {
            verifyOtp()
        }

        observeState()

        return view
    }

    private fun verifyOtp() {
        val otp = otp1.text.toString() +
                otp2.text.toString() +
                otp3.text.toString() +
                otp4.text.toString() +
                otp5.text.toString() +
                otp6.text.toString()

        if (otp.length != 6) {
            Toast.makeText(requireContext(), "Enter complete OTP", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.verifyOtp(email, otp)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        loginButton.isEnabled = false
                    }
                    is AuthState.Success -> {
                        loginButton.isEnabled = true
                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), UserMainActivity::class.java))
                        requireActivity().finish()
                        viewModel.resetState()
                    }
                    is AuthState.Error -> {
                        loginButton.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                    else -> {
                        loginButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupOtpInputs() {
        moveNext(otp1, otp2)
        moveNext(otp2, otp3)
        moveNext(otp3, otp4)
        moveNext(otp4, otp5)
        moveNext(otp5, otp6)
    }

    private fun moveNext(current: TextInputEditText, next: TextInputEditText) {
        current.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    next.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
