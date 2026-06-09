package com.example.blinkitclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkitclone.databinding.ItemViewOrdersBinding
import com.example.blinkitclone.model.Order

class OrdersAdapter(
    private val orderList: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    class OrdersViewHolder(val binding: ItemViewOrdersBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            ItemViewOrdersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = orderList.size

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = orderList[position]
        holder.binding.apply {
            tvOrderDate.text = order.date ?: order.created_at?.substringBefore("T") ?: "N/A"
            tvOrderAmount.text = "₹${order.total_amount}"
            tvOrderStatus.text = order.status
            
            when (order.status.lowercase()) {
                "delivered" -> tvOrderStatus.setBackgroundResource(com.example.blinkitclone.R.drawable.bg_order_green)
                else -> tvOrderStatus.setBackgroundResource(com.example.blinkitclone.R.drawable.bg_order)
            }

            root.setOnClickListener {
                onOrderClick(order)
            }
        }
    }
}
