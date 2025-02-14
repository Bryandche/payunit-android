package com.github.payunit.ui

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.payunit_android.databinding.MobileMoneyBottomSheetBinding
import com.github.payunit.PayUnitButton
import com.github.payunit.models.Provider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MobileMoneyBottomSheet(
    private val provider: Provider,
    private val amount: Int,
    private val currency: String,
    private val onPaymentSubmit: (String, (PayUnitButton.PaymentResult) -> Unit) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: MobileMoneyBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var loadingDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MobileMoneyBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.apply {
            tvAmount.text = "${formatAmount(amount.toString())} ${currency}"
        }
    }

    private fun formatAmount(amount: String): String {
        return try {
            val number = amount.toDouble()
            String.format("%,.0f", number)
        } catch (e: Exception) {
            amount
        }
    }

    private fun setupListeners() {
        binding.btnPay.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString()
            if (validatePhoneNumber(phoneNumber)) {
                showLoading()
                // Call onPaymentSubmit with the phone number and a callback
                onPaymentSubmit(phoneNumber) { result ->
                    hideLoading()
                    // Check payment status if the payment was successful
                    if (result is PayUnitButton.PaymentResult.Success) {
                        dismiss()
                    }
                }
            }
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        return if (phoneNumber.length < 9) {
            binding.tilPhoneNumber.error = "Enter a valid phone number"
            false
        } else {
            binding.tilPhoneNumber.error = null
            true
        }
    }

    private fun showLoading() {
        binding.btnPay.isEnabled = false
        binding.etPhoneNumber.isEnabled = false
        binding.etPhoneNumber.isEnabled = false
        binding.btnPay.setTextColor(Color.parseColor("#FF9800")) // Hide text
        binding.myProgress.visibility = View.VISIBLE // Show the progress bar

//        loadingDialog = MaterialAlertDialogBuilder(requireContext())
//            .setView(R.layout.dialog_loading)
//            .setCancelable(false)
//            .show()
    }

    private fun hideLoading() {
        binding.btnPay.isEnabled = true
        binding.etPhoneNumber.isEnabled = true
        binding.btnPay.setTextColor(Color.parseColor("#FFFFFF")) // Show text
        binding.myProgress.visibility = View.GONE // Hide the progress bar
//      loadingDialog?.dismiss()
    }

    fun showDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun handlePaymentResult(result: PayUnitButton.PaymentResult) {
        when (result) {

            is PayUnitButton.PaymentResult.Success -> {
//                showSuccessDialog(result.message)
            }
            is PayUnitButton.PaymentResult.Error -> {
//                showErrorDialog(result.message)
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Payment Initiated")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                dismiss()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Payment Failed")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    fun showDialog(context: Context, message: String) {
//        AlertDialog.Builder(context)
//            .setMessage(message)
//            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//            .create()
//            .show()
//    }

    companion object {
        const val TAG = "MobileMoneyBottomSheet"
    }
}