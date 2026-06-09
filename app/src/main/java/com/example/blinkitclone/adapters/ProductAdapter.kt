package com.example.blinkitclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blinkitclone.databinding.ItemViewProductBinding
import com.example.blinkitclone.model.Product

class ProductAdapter(
    private val productList: List<Product>,
    private val onAddClick: (Product) -> Unit,
    private val onIncrementClick: (Product) -> Unit,
    private val onDecrementClick: (Product) -> Unit,
    private var cartItems: Map<Product, Int>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemViewProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemViewProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = productList.size

    fun updateCart(newCartItems: Map<Product, Int>) {
        this.cartItems = newCartItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.apply {
            tvProductTitle.text = product.productTitle
            productQuantity.text = "${product.productQuantity} ${product.productUnit}"
            tvProductPrice.text = "₹${product.productPrice}"

            val imageList = ArrayList<SlideModel>()
            product.productImagesUris?.forEach {
                it?.let { url -> imageList.add(SlideModel(url)) }
            }
            ivImageSlider.setImageList(imageList)

            // Look up count by product ID to be safe
            val count = cartItems.entries.find { it.key.id == product.id }?.value ?: 0
            
            if (count > 0) {
                tvAdd.visibility = View.GONE
                llProductCount.visibility = View.VISIBLE
                tvProductCount.text = count.toString()
            } else {
                tvAdd.visibility = View.VISIBLE
                llProductCount.visibility = View.GONE
            }

            tvAdd.setOnClickListener {
                onAddClick(product)
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
