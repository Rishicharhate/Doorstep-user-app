package com.example.blinkitclone.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.adapters.ProductAdapter
import com.example.blinkitclone.databinding.FragmentSearchBinding
import com.example.blinkitclone.viewmodels.CartViewModel
import com.example.blinkitclone.viewmodels.SearchState
import com.example.blinkitclone.viewmodels.SearchViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private var adapter: ProductAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        setupClickListeners()
        setupSearchListener()
        observeSearchState()
        observeCart()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSearchListener() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                viewModel.searchProducts(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeSearchState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchState.collect { state ->
                when (state) {
                    is SearchState.Loading -> {
                        binding.shimmerViewContainer.visibility = View.VISIBLE
                        binding.shimmerViewContainer.startShimmer()
                        binding.rvProduct.visibility = View.GONE
                        binding.tvText.visibility = View.GONE
                    }
                    is SearchState.Success -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        
                        if (state.products.isEmpty()) {
                            binding.rvProduct.visibility = View.GONE
                            binding.tvText.visibility = View.VISIBLE
                            binding.tvText.text = "No products found!"
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
                    is SearchState.Error -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartItems.collect { cartItems ->
                adapter?.updateCart(cartItems)
            }
        }
    }
}
