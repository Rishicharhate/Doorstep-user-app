package com.example.blinkitclone.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkitclone.SupabaseClient
import com.example.blinkitclone.adapters.CheckoutAdapter
import com.example.blinkitclone.databinding.ActivityOrderPlacedBinding
import com.example.blinkitclone.model.Order
import com.example.blinkitclone.model.OrderItem
import com.example.blinkitclone.model.Product
import com.example.blinkitclone.viewmodels.CartViewModel
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class OrderPlacedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderPlacedBinding
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var checkoutAdapter: CheckoutAdapter
    private var grandTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.parseColor("#35035C"))
        )
        binding = ActivityOrderPlacedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        observeCart()
        setupToolbar()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        checkoutAdapter = CheckoutAdapter(emptyList())
        binding.rvProductsItems.adapter = checkoutAdapter
    }

    private fun setupClickListeners() {
        binding.btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun observeCart() {
        lifecycleScope.launch {
            cartViewModel.cartItems.collect { items ->
                val cartList = items.map { it.key to it.value }
                checkoutAdapter.updateData(cartList)

                calculateBill(items)
            }
        }
    }

    private fun calculateBill(items: Map<Product, Int>) {
        var subTotal = 0
        for ((product, count) in items) {
            subTotal += (product.productPrice ?: 0) * count
        }

        val deliveryCharge = when {
            subTotal > 2000 -> 0
            subTotal >= 300 -> 20
            else -> 50
        }

        grandTotal = subTotal + deliveryCharge

        binding.tvSubTotal.text = "₹$subTotal"
        binding.tvDeliveryCharge.text = if (deliveryCharge == 0) "Free" else "₹$deliveryCharge"
        binding.tvGrandTotal.text = "₹$grandTotal"
    }

    private fun placeOrder() {
        // For testing purposes, we use a fixed user ID if not logged in
        val user = SupabaseClient.client.auth.currentUserOrNull()
        val userId = user?.id ?: "fcd6b342-bf46-4d04-9e0d-8080cc1130cb"

        val cartItems = cartViewModel.cartItems.value
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnPlaceOrder.isEnabled = false
        
        lifecycleScope.launch {
            try {
                // 1. Create the order
                val order = Order(
                    user_id = userId,
                    total_amount = grandTotal
                )
                
                val insertedOrder = SupabaseClient.client.postgrest["orders"]
                    .insert(order) {
                        select()
                    }.decodeSingle<Order>()

                val orderId = insertedOrder.id ?: throw Exception("Failed to get order ID")

                // 2. Create order items
                val orderItemsList = cartItems.map { (product, quantity) ->
                    OrderItem(
                        order_id = orderId,
                        product_id = product.id!!,
                        item_quantity = quantity,
                        price = product.productPrice ?: 0
                    )
                }

                SupabaseClient.client.postgrest["order_items"].insert(orderItemsList)

                // 3. Clear cart
                cartViewModel.clearCart()

                Toast.makeText(this@OrderPlacedActivity, "Order Placed Successfully", Toast.LENGTH_LONG).show()
                
                val intent = Intent(this@OrderPlacedActivity, UserMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@OrderPlacedActivity, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnPlaceOrder.isEnabled = true
            }
        }
    }
}
