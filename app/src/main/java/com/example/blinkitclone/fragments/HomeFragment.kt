package com.example.blinkitclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.adapters.BestsellerAdapter
import com.example.blinkitclone.adapters.CategoryAdapter
import com.example.blinkitclone.databinding.AddressEditLayoutBinding
import com.example.blinkitclone.databinding.AddressLayoutBinding
import com.example.blinkitclone.databinding.FragmentHomeBinding
import com.example.blinkitclone.model.Categories
import com.example.blinkitclone.model.UserProfile
import com.example.blinkitclone.utils.Constants
import com.example.blinkitclone.viewmodels.HomeState
import com.example.blinkitclone.viewmodels.HomeViewModel
import com.example.blinkitclone.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val viewModel2: ProfileViewModel by viewModels()
    private var currentUserProfile: UserProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        setCategories()
        observeHomeData()
        observeAddress()
        setUpClickListener()
        viewModel2.fetchProfile()

        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.searchEt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        return binding.root
    }

    //Address add and edit
    private fun setUpClickListener(){
        binding.tvDropDown.setOnClickListener {
            if (currentUserProfile?.address.isNullOrEmpty()) {
                showAddAddressDialog()
            } else {
                showEditAddressDialog(currentUserProfile?.address!!)
            }
        }
    }

    //Add address dialog
    private fun showAddAddressDialog() {
        val dialogBinding = AddressLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnAdd.setOnClickListener {
            val address = dialogBinding.etDescriptiveAddress.text.toString().trim()
            if (address.isNotEmpty()) {
                viewModel2.saveAddress(address)
                dialog.dismiss()
            } else {
                dialogBinding.etDescriptiveAddress.error = "Please enter address"
            }
        }

        dialog.show()
    }

    //Edit address dialog
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
                viewModel2.saveAddress(newAddress)
                dialog.dismiss()
            } else {
                dialogBinding.etAddress.error = "Address cannot be empty"
            }
        }

        dialog.show()
    }

    private fun setCategories() {
        val categoryList = ArrayList<Categories>()
        for (i in Constants.allProductsCategory.indices) {
            categoryList.add(
                Categories(
                    Constants.allProductsCategory[i],
                    Constants.allProductsCategoryIcon[i]
                )
            )
        }
        binding.rvCategory.adapter = CategoryAdapter(categoryList) { category ->
            val bundle = Bundle().apply {
                putString("category", category.category)
            }
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
        }
    }

    private fun observeAddress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addressState.collect { address ->
                if (address != null) {
                    binding.tvAddress.text = address
                } else {
                    binding.tvAddress.text = "Click to set address in Profile"
                }
            }
        }
    }

    private fun observeHomeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeState.collect { state ->
                when (state) {
                    is HomeState.Loading -> {
                        binding.shimmerViewContainer.visibility = View.VISIBLE
                        binding.rvBestseller.visibility = View.GONE
                        binding.shimmerViewContainer.startShimmer()
                    }
                    is HomeState.Success -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.rvBestseller.visibility = View.VISIBLE
                        binding.rvBestseller.adapter = BestsellerAdapter(state.bestsellerList) { bestsellerData ->
                            val bottomSheet = SeeAllBottomSheet.newInstance(bestsellerData.category)
                            bottomSheet.show(childFragmentManager, "SeeAllBottomSheet")
                        }
                    }
                    is HomeState.Error -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserAddress()
        viewModel.fetchBestsellers() // Also refresh products to keep home fresh
    }
}
