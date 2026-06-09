package com.example.blinkitclone.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.SupabaseClient
import com.example.blinkitclone.activity.AuthMainActivity
import com.example.blinkitclone.databinding.AddressEditLayoutBinding
import com.example.blinkitclone.databinding.AddressLayoutBinding
import com.example.blinkitclone.databinding.FragmentProfileBinding
import com.example.blinkitclone.model.UserProfile
import com.example.blinkitclone.viewmodels.CartViewModel
import com.example.blinkitclone.viewmodels.ProfileState
import com.example.blinkitclone.viewmodels.ProfileViewModel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private var currentUserProfile: UserProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupClickListeners()
        observeProfile()
        viewModel.fetchProfile()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.llOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }

        binding.llAddress.setOnClickListener {
            if (currentUserProfile?.address.isNullOrEmpty()) {
                showAddAddressDialog()
            } else {
                showEditAddressDialog(currentUserProfile?.address!!)
            }
        }

        binding.llLogout.setOnClickListener {
            showLogoutDialog()
        }
        
        binding.tbProfileFragment.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performLogout() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Clear Supabase Session
                SupabaseClient.client.auth.signOut()
                
                // Clear Local Cart
                cartViewModel.clearCart()
                
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

                // Navigate back to Auth Activity
                val intent = Intent(requireContext(), AuthMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                when (state) {
                    is ProfileState.Loading -> {
                        // Optional: Show loading indicator
                    }
                    is ProfileState.Success -> {
                        currentUserProfile = state.profile
                    }
                    is ProfileState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showAddAddressDialog() {
        val dialogBinding = AddressLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnAdd.setOnClickListener {
            val address = dialogBinding.etDescriptiveAddress.text.toString().trim()
            if (address.isNotEmpty()) {
                viewModel.saveAddress(address)
                dialog.dismiss()
            } else {
                dialogBinding.etDescriptiveAddress.error = "Please enter address"
            }
        }

        dialog.show()
    }

    private fun showEditAddressDialog(currentAddress: String) {
        val dialogBinding = AddressEditLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.etAddress.setText(currentAddress)

        dialogBinding.btnEdit.setOnClickListener {
            // Enable editing
            dialogBinding.tilAddress.isEnabled = true
            dialogBinding.etAddress.requestFocus()
        }

        dialogBinding.btnSave.setOnClickListener {
            val newAddress = dialogBinding.etAddress.text.toString().trim()
            if (newAddress.isNotEmpty()) {
                viewModel.saveAddress(newAddress)
                dialog.dismiss()
            } else {
                dialogBinding.etAddress.error = "Address cannot be empty"
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
