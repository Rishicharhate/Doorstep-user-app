package com.example.blinkitclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.adapters.ProductAdapter
import com.example.blinkitclone.databinding.FragmentCategoryBinding
import com.example.blinkitclone.viewmodels.CartViewModel
import com.example.blinkitclone.viewmodels.CategoryState
import com.example.blinkitclone.viewmodels.CategoryViewModel
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private val viewModel: CategoryViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private var categoryName: String? = null
    private var adapter: ProductAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        categoryName = arguments?.getString("category")
        binding.tbSearchFragment.title = categoryName

        setupToolbar()
        observeProducts()
        observeCart()

        categoryName?.let {
            viewModel.fetchProductsByCategory(it)
        }

        binding.tbSearchFragment.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.searchMenu -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    private fun setupToolbar() {
        binding.tbSearchFragment.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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
                        binding.tvText.visibility = View.GONE
                    }
                    is CategoryState.Success -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE

                        if (state.products.isEmpty()) {
                            binding.rvProduct.visibility = View.GONE
                            binding.tvText.visibility = View.VISIBLE
                        } else {
                            binding.rvProduct.visibility = View.VISIBLE
                            binding.tvText.visibility = View.GONE

                            adapter = ProductAdapter(
                                state.products,
                                onAddClick = { product -> cartViewModel.addItem(product) },
                                onIncrementClick = { product -> cartViewModel.addItem(product) },
                                onDecrementClick = { product -> cartViewModel.removeItem(product) },
                                cartItems = cartViewModel.cartItems.value
                            )
                            binding.rvProduct.adapter = adapter
                        }
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
}
