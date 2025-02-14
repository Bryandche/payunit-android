package com.github.payunit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.payunit_android.databinding.PaymentMethodBottomSheetBinding
import com.github.payunit.adapter.PaymentMethodsAdapter
import com.github.payunit.models.Provider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PaymentMethodsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: PaymentMethodBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var providers: List<Provider> = emptyList()
    private var onProviderSelected: ((Provider) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PaymentMethodBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        updateProviders()
    }

    private fun setupRecyclerView() {
        binding.rvPaymentMethods.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = PaymentMethodsAdapter { provider ->
                onProviderSelected?.invoke(provider)
                dismiss()
            }
        }
    }

    private fun updateProviders() {
        (binding.rvPaymentMethods.adapter as? PaymentMethodsAdapter)?.submitList(providers)
    }

    fun setProviders(providers: List<Provider>) {
        this.providers = providers
        if (_binding != null) {
            updateProviders()
        }
    }

    fun setOnProviderSelectedListener(listener: (Provider) -> Unit) {
        onProviderSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PaymentMethodsBottomSheet"
    }
}