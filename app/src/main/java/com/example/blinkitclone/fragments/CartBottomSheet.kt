package com.example.blinkitclone.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.blinkitclone.activity.OrderPlacedActivity
import com.example.blinkitclone.adapters.CartAdapter
import com.example.blinkitclone.databinding.BsCartProductBinding
import com.example.blinkitclone.viewmodels.CartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class CartBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BsCartProductBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BsCartProductBinding.inflate(layoutInflater)

        setupRecyclerView()
        observeCart()
        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.ivShowingProductsCart.setOnClickListener {
            dismiss()
        }

        binding.btnNextCart.setOnClickListener {
            if (cartViewModel.getTotalItemCount() > 0) {
                startActivity(Intent(requireContext(), OrderPlacedActivity::class.java))
                dismiss()
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems = emptyList(),
            onIncrementClick = { product -> cartViewModel.addItem(product) },
            onDecrementClick = { product -> cartViewModel.removeItem(product) }
        )
        binding.rvProductsItems.adapter = cartAdapter
    }

    private fun observeCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartItems.collect { items ->
                val totalCount = cartViewModel.getTotalItemCount()
                binding.tvNumaberOfProductCount.text = totalCount.toString()
                
                if (totalCount == 0) {
                    dismiss()
                }

                val cartList = items.map { it.key to it.value }
                cartAdapter.updateData(cartList)
            }
        }
    }
}
