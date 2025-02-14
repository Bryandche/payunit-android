package com.github.payunit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.payunit.models.Provider
import com.example.payunit_android.databinding.ItemPaymentMethodBinding
import com.github.payunit.adapter.PaymentMethodsAdapter.ProviderViewHolder

class PaymentMethodsAdapter(
    private val onProviderClick: (Provider) -> Unit
) : ListAdapter<Provider, ProviderViewHolder>(ProviderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val binding = ItemPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProviderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProviderViewHolder(
        private val binding: ItemPaymentMethodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(provider: Provider) {
            binding.apply {
                tvProviderName.text = provider.name
                // Load logo using Glide or similar
                Glide.with(ivProviderLogo)
                    .load(provider.logo)
                    .into(ivProviderLogo)

                root.setOnClickListener { onProviderClick(provider) }
            }
        }
    }

    class ProviderDiffCallback : DiffUtil.ItemCallback<Provider>() {
        override fun areItemsTheSame(oldItem: Provider, newItem: Provider) =
            oldItem.shortcode == newItem.shortcode

        override fun areContentsTheSame(oldItem: Provider, newItem: Provider) =
            oldItem == newItem
    }
}