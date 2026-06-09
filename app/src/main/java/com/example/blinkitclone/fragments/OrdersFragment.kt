package com.example.blinkitclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkitclone.R
import com.example.blinkitclone.adapters.OrdersAdapter
import com.example.blinkitclone.databinding.FragmentOrderBinding
import com.example.blinkitclone.viewmodels.OrdersState
import com.example.blinkitclone.viewmodels.OrdersViewModel
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private val viewModel: OrdersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(layoutInflater)

        setupToolbar()
        observeOrders()
        viewModel.fetchOrders()

        return binding.root
    }

    private fun setupToolbar() {
        binding.tbProfileFragment.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ordersState.collect { state ->
                when (state) {
                    is OrdersState.Loading -> {
                        binding.shimmerViewContainer.visibility = View.VISIBLE
                        binding.shimmerViewContainer.startShimmer()
                        binding.rvOrders.visibility = View.GONE
                    }
                    is OrdersState.Success -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.rvOrders.visibility = View.VISIBLE
                        binding.rvOrders.adapter = OrdersAdapter(state.orders) { order ->
                            val bundle = Bundle().apply {
                                putString("orderId", order.id)
                            }
                            findNavController().navigate(R.id.action_ordersFragment_to_orderDetailFragment, bundle)
                        }
                    }
                    is OrdersState.Error -> {
                        binding.shimmerViewContainer.stopShimmer()
                        binding.shimmerViewContainer.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
