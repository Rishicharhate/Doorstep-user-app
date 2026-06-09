package com.example.blinkitclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.blinkitclone.adapters.ProductAdapter
import com.example.blinkitclone.databinding.SeeAllBottomSheetBinding
import com.example.blinkitclone.viewmodels.CartViewModel
import com.example.blinkitclone.viewmodels.CategoryState
import com.example.blinkitclone.viewmodels.CategoryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class SeeAllBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: SeeAllBottomSheetBinding
    private val viewModel: CategoryViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private var categoryName: String? = null
    private var adapter: ProductAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SeeAllBottomSheetBinding.inflate(layoutInflater)

        categoryName = arguments?.getString("category")

        setupRecyclerView()
        observeProducts()
        observeCart()

        categoryName?.let {
            viewModel.fetchProductsByCategory(it)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            productList = emptyList(),
            onAddClick = { product -> cartViewModel.addItem(product) },
            onIncrementClick = { product -> cartViewModel.addItem(product) },
            onDecrementClick = { product -> cartViewModel.removeItem(product) },
            cartItems = cartViewModel.cartItems.value
        )
        binding.rvProduct.adapter = adapter
    }

    private fun observeCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartItems.collect { cartItems ->
                adapter?.updateCart(cartItems)
            }
        }
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryState.collect { state ->
                when (state) {
                    is CategoryState.Loading -> {
                        binding.shimmerViewContainer.visibility = View.VISIBLE
                        binding.shimmerViewContainer.startShimmer()
                        binding.rvProduct.visibility = View.GONE
                    }
                    is CategoryState.Success -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.rvProduct.visibility = View.VISIBLE
                        
                        adapter = ProductAdapter(
                            state.products,
                            onAddClick = { product -> cartViewModel.addItem(product) },
                            onIncrementClick = { product -> cartViewModel.addItem(product) },
                            onDecrementClick = { product -> cartViewModel.removeItem(product) },
                            cartItems = cartViewModel.cartItems.value
                        )
                        binding.rvProduct.adapter = adapter
                    }
                    is CategoryState.Error -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(category: String): SeeAllBottomSheet {
            val fragment = SeeAllBottomSheet()
            val args = Bundle()
            args.putString("category", category)
            fragment.arguments = args
            return fragment
        }
    }
}
