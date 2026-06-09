package com.example.blinkitclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.blinkitclone.databinding.ItemViewCartProductBinding
import com.example.blinkitclone.model.Product

class CartAdapter(
    private var cartItems: List<Pair<Product, Int>>,
    private val onIncrementClick: (Product) -> Unit,
    private val onDecrementClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: ItemViewCartProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            ItemViewCartProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newCartItems: List<Pair<Product, Int>>) {
        this.cartItems = newCartItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (product, count) = cartItems[position]
        holder.binding.apply {
            tvProductTitle.text = product.productTitle
            tvProductPrice.text = "₹${(product.productPrice ?: 0) * count}"
            tvProductCount.text = count.toString()
            
            val imageUrl = product.productImagesUris?.firstOrNull()
            if (imageUrl != null) {
                ivProductImage.load(imageUrl)
            }

            tvIncrementCount.setOnClickListener {
                onIncrementClick(product)
            }

            tvDecrementCount.setOnClickListener {
                onDecrementClick(product)
            }
        }
    }
}
