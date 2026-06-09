package com.example.blinkitclone.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.blinkitclone.databinding.ItemViewBestsellerBinding
import com.example.blinkitclone.viewmodels.BestsellerData

class BestsellerAdapter(
    private val bestsellerList: List<BestsellerData>,
    private val onSeeAllClick: (BestsellerData) -> Unit
) : RecyclerView.Adapter<BestsellerAdapter.BestsellerViewHolder>() {

    class BestsellerViewHolder(val binding: ItemViewBestsellerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestsellerViewHolder {
        return BestsellerViewHolder(
            ItemViewBestsellerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = bestsellerList.size

    override fun onBindViewHolder(holder: BestsellerViewHolder, position: Int) {
        val data = bestsellerList[position]
        holder.binding.apply {
            tvProductType.text = data.category
            tvTotalProducts.text = "${data.products.size} products"

            val products = data.products
            val imageViews = listOf(ivProduct1, ivProduct2, ivProduct3)
            
            // Reset visibility
            imageViews.forEach { it.visibility = View.GONE }
            tvProductCount.visibility = View.GONE

            for (i in products.indices) {
                if (i < 3) {
                    val imageUrl = products[i].productImagesUris?.firstOrNull()
                    if (imageUrl != null) {
                        imageViews[i].visibility = View.VISIBLE
                        imageViews[i].load(imageUrl)
                    }
                } else {
                    tvProductCount.visibility = View.VISIBLE
                    tvProductCount.text = "+${products.size - 3}"
                    break
                }
            }

            tvSeeAll.setOnClickListener {
                onSeeAllClick(data)
            }
        }
    }
}
