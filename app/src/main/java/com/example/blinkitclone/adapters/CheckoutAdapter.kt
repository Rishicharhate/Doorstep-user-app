package com.example.blinkitclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.blinkitclone.databinding.ItemViewCheckoutOrderBinding
import com.example.blinkitclone.model.Product

class CheckoutAdapter(private var cartItems: List<Pair<Product, Int>>) :
    RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    class CheckoutViewHolder(val binding: ItemViewCheckoutOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        return CheckoutViewHolder(
            ItemViewCheckoutOrderBinding.inflate(
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

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        val (product, count) = cartItems[position]
        holder.binding.apply {
            tvProductTitle.text = product.productTitle
            tvProductQuantity.text = "${product.productQuantity} ${product.productUnit}"
            tvProductPrice.text = "₹${(product.productPrice ?: 0) * count}"
            tvProductCount.text = count.toString()

            val imageUrl = product.productImagesUris?.firstOrNull()
            if (imageUrl != null) {
                ivProductImage.load(imageUrl)
            }
        }
    }
}
