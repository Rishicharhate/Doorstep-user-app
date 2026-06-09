package com.example.blinkitclone.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.adapters.CheckoutAdapter
import com.example.blinkitclone.databinding.FragmentOrderDetailBinding
import com.example.blinkitclone.viewmodels.OrderDetailState
import com.example.blinkitclone.viewmodels.OrderDetailViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private val viewModel: OrderDetailViewModel by viewModels()
    private var orderId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)

        orderId = arguments?.getString("orderId")

        setupToolbar()
        observeOrderDetails()

        orderId?.let {
            viewModel.fetchOrderDetails(it)
        }

        return binding.root
    }

    private fun setupToolbar() {
        binding.tbOrderDetailFragment.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeOrderDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orderDetailState.collect { state ->
                when (state) {
                    is OrderDetailState.Loading -> {
                        // Optional: Show a progress bar
                    }
                    is OrderDetailState.Success -> {
                        updateStatusUI(state.order.status)
                        
                        if (state.products.isNotEmpty()) {
                            val adapter = CheckoutAdapter(state.products)
                            binding.rvProductsItems.adapter = adapter
                        }
                    }
                    is OrderDetailState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateStatusUI(status: String) {
        val greenColor = ContextCompat.getColor(requireContext(), R.color.green)
        val grayColor = Color.parseColor("#6B6464")
        val lightGrayColor = Color.parseColor("#DDDDDD")

        // Reset all to default first (though xml usually has them)
        
        when (status.lowercase()) {
            "placed" -> {
                binding.iv1.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv1, ColorStateList.valueOf(Color.WHITE))
            }
            "packed" -> {
                binding.iv1.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv1, ColorStateList.valueOf(Color.WHITE))
                binding.view1.setBackgroundColor(greenColor)
                binding.iv2.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv2, ColorStateList.valueOf(Color.WHITE))
            }
            "shipped" -> {
                binding.iv1.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv1, ColorStateList.valueOf(Color.WHITE))
                binding.view1.setBackgroundColor(greenColor)
                binding.iv2.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv2, ColorStateList.valueOf(Color.WHITE))
                binding.view2.setBackgroundColor(greenColor)
                binding.iv3.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv3, ColorStateList.valueOf(Color.WHITE))
            }
            "delivered" -> {
                binding.iv1.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv1, ColorStateList.valueOf(Color.WHITE))
                binding.view1.setBackgroundColor(greenColor)
                binding.iv2.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv2, ColorStateList.valueOf(Color.WHITE))
                binding.view2.setBackgroundColor(greenColor)
                binding.iv3.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv3, ColorStateList.valueOf(Color.WHITE))
                binding.view3.setBackgroundColor(greenColor)
                binding.iv4.backgroundTintList = ColorStateList.valueOf(greenColor)
                ImageViewCompat.setImageTintList(binding.iv4, ColorStateList.valueOf(Color.WHITE))
            }
        }
    }
}
