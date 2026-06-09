package com.example.blinkitclone.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkitclone.R
import com.example.blinkitclone.databinding.ActivityHomeBinding
import com.example.blinkitclone.fragments.CartBottomSheet
import com.example.blinkitclone.viewmodels.CartViewModel
import kotlinx.coroutines.launch

class UserMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.parseColor("#35035C"))
        )
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeCart()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.ivShowingProductsCart.setOnClickListener {
            val bottomSheet = CartBottomSheet()
            bottomSheet.show(supportFragmentManager, "CartBottomSheet")
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OrderPlacedActivity::class.java))
        }
    }

    private fun observeCart() {
        lifecycleScope.launch {
            cartViewModel.cartItems.collect { items ->
                val totalCount = cartViewModel.getTotalItemCount()
                if (totalCount > 0) {
                    binding.llCart.visibility = View.VISIBLE
                    binding.tvNumberOfProductCount.text = totalCount.toString()
                } else {
                    binding.llCart.visibility = View.GONE
                }
            }
        }
    }
}
