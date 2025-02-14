package com.github.payunit

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.example.payunit_android.databinding.PayUnitButtonBinding
import com.github.payunit.models.InitializeRequest
import com.github.payunit.models.PaymentData
import com.github.payunit.models.PaymentStatusResponse
import com.github.payunit.models.ProcessPaymentRequest
import com.github.payunit.models.Provider
import com.github.payunit.models.ProviderData
import com.github.payunit.models.TransactionData
import com.github.payunit.network.PayUnitApiClient
import com.github.payunit.ui.MobileMoneyBottomSheet
import com.github.payunit.ui.PaymentMethodsBottomSheet
import com.github.payunit.ui.ProcessingPaymentDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PayUnitButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
//    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: PayUnitButtonBinding = PayUnitButtonBinding.inflate(LayoutInflater.from(context), this, true)
//    private val transactionId = generateTransactionId()
    private var state = true
    private val processingDialog = ProcessingPaymentDialog(context)

    private val lifecycleOwner: LifecycleOwner? by lazy {
        when (context) {
            is LifecycleOwner -> context
            is ContextWrapper -> (context as? ContextWrapper)?.baseContext as? LifecycleOwner
            else -> null
        }
    }

    // Required properties with @JvmField for better Java interop
    @JvmField
    var apiUsername: String = ""

    @JvmField
    var apiPassword: String = ""

    @JvmField
    var apiKey: String = ""

    @JvmField
    var mode: String = ""  // "live" or "sandbox"

    @JvmField
    var paymentCountry: String = ""

    @JvmField
    var totalAmount: Int = 0

    @JvmField
    var currency: String = ""

    @JvmField
    var returnUrl: String = ""

    @JvmField
    var notifyUrl: String = ""

    // Optional properties
    private var buttonText: String = "Pay Now"
        private set

    private var buttonColor: Int = ContextCompat.getColor(context, android.R.color.holo_orange_light)
        private set

    private var buttonTextColor: Int = ContextCompat.getColor(context, android.R.color.white)
        private set

    private var paymentCallback: PaymentCallback? = null

    init {
        // Set up the button click listener
        binding.payButton.setOnClickListener {
            if (validateRequiredFields()) {
                startPaymentProcess()
            } else {
//                paymentCallback?.onError("Missing required fields")
                showDialog(context, "Missing required fields")
            }
        }
    }

    private fun showDialog(context: Context, message: String) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // Convert dp to pixels
    private fun dpToPx(dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    // Java-friendly methods
    @JvmName("setButtonText")
    fun setButtonText(text: String) {
        this.buttonText = text
//        this.text = text
    }

    @JvmName("setButtonColor")
    fun setButtonColor(color: Int) {
        this.buttonColor = color
        backgroundTintList = ColorStateList.valueOf(color)
    }

    @JvmName("setButtonTextColor")
    fun setButtonTextColor(color: Int) {
        this.buttonTextColor = color
//        setTextColor(color)
    }

    @JvmName("setPaymentCallback")
    fun setPaymentCallback(callback: PaymentCallback) {
        this.paymentCallback = callback
        validateAndSetupClickListener()
    }

    private fun validateAndSetupClickListener() {
        setOnClickListener {
            if (validateRequiredFields()) {
                startPaymentProcess()
            } else {
//                paymentCallback?.onError("Missing required fields")
                showDialog(context, "Missing required fields")
            }
        }
    }

    private fun validateRequiredFields(): Boolean {
        return apiUsername.isNotEmpty() &&
                apiPassword.isNotEmpty() &&
                apiKey.isNotEmpty() &&
                mode.isNotEmpty() &&
                paymentCountry.isNotEmpty() &&
                totalAmount > 0 &&
                currency.isNotEmpty() &&
                returnUrl.isNotEmpty() &&
                notifyUrl.isNotEmpty() &&
                paymentCallback != null
    }

     private fun startPaymentProcess() {

        binding.payButton.setTextColor(Color.parseColor("#FF9800")) // Hide text
        binding.payButton.isEnabled = false // Disable button
        binding.myProgress.visibility = View.VISIBLE // Show the progress bar

        // Initialize payment
        MainScope().launch {
            try {
//                payUnitApiClient = PayUnitApiClient("config")
                val initializeResponse = PayUnitApiClient(apiUsername,apiPassword, apiKey,mode).api.initializePayment(
                    InitializeRequest(
                        total_amount = totalAmount,
                        currency = currency,
                        transaction_id = generateTransactionId(),
                        return_url = returnUrl,
                        notify_url = notifyUrl,
                        payment_country = paymentCountry
                    )
                )

                if (initializeResponse.isSuccessful) {
                    initializeResponse.body()?.let { response ->
                        showPaymentMethods(response.data)
                    }
                } else {
//                    paymentCallback?.onError("Failed to initialize payment")
                    showDialog(context, "Failed to initialize payment")
                }
            } catch (e: Exception) {
//                paymentCallback?.onError(e.message ?: "Unknown error occurred")
                showDialog(context, "Unknown error occurred")
            } finally {
                isEnabled = true
//                text = buttonText
                binding.payButton.setTextColor(Color.parseColor("#FFFFFF"))
                binding.payButton.isEnabled = true // Enable button
                binding.myProgress.visibility = View.GONE // Hide the progress bar
            }
        }
    }

    private fun getProviders(tUrl: String, tId: String, tSum: String, callback: (PaymentResult) -> Unit) {

        MainScope().launch {
            try {
                val providersResponse = PayUnitApiClient(apiUsername,apiPassword, apiKey,mode).api.getProviders(tUrl, tId, tSum)

                if (providersResponse.isSuccessful) {
                    providersResponse.body()?.let { response ->
                        if (response.statusCode == 200) {
                            // Process the providers as needed
                            val providerList = response.data.map { item ->
                                ProviderData(
                                    shortcode = item.shortcode,
                                    name = item.name,
                                    logo = item.logo,
                                    provider_type = item.logo,
                                    country = item.country
                                )
                            }
//                            callback(PaymentResult.Success(providerList))
                        } else {
                            callback(PaymentResult.Error("Failed to get providers: ${response.message}"))
                        }
                    }
                } else {
                    callback(PaymentResult.Error("Error: ${providersResponse.message()}"))
                }
            } catch (e: Exception) {
                callback(PaymentResult.Error("Error: ${e.message}"))
            }
        }
    }

    private fun showPaymentMethods(transactionData: TransactionData) {
        // Filter providers based on the payment country
        val filteredProviders = transactionData.providers.filter { provider ->
            provider.country.country_code.equals(paymentCountry, ignoreCase = true)
        }

        // Show bottom sheet with filtered providers
        val bottomSheet = PaymentMethodsBottomSheet().apply {
            setProviders(filteredProviders)
            setOnProviderSelectedListener { provider ->
                handleProviderSelection(provider, transactionData)
            }
        }

        val isEmpty = filteredProviders.isEmpty()

        if(isEmpty){
            showDialog(context, "No providers available")
        }else{
            bottomSheet.show(
                (context as FragmentActivity).supportFragmentManager,
                PaymentMethodsBottomSheet.TAG
            )
        }


    }

    // Helper extension to find FragmentManager
    private fun Context.findFragmentManager(): FragmentManager? {
        return when (this) {
            is FragmentActivity -> supportFragmentManager
            is ContextWrapper -> baseContext.findFragmentManager()
            else -> null
        }
    }

    sealed class PaymentResult {
        data class Success(val message: String) : PaymentResult()
        data class Error(val message: String) : PaymentResult()
    }

    private fun handleProviderSelection(provider: Provider, transactionData: TransactionData) {
        val fragmentManager = context.findFragmentManager()
        if (fragmentManager == null) {
//            paymentCallback?.onError("Cannot show payment form: Activity not found")
            showDialog(context, "Cannot show payment form: Activity not found")
            return
        }

        when (provider.provider_type) {
            "MOBILE_AND_WALLET" -> {
                val mobileMoneySheet = MobileMoneyBottomSheet(
                    provider = provider,
                    amount = totalAmount,
                    currency = currency,
                ) { phoneNumber, callback ->
                    processMobileMoneyPayment(
                        provider = provider,
                        transactionData = transactionData,
                        phoneNumber = phoneNumber,
                        callback = callback
                    )
                }
                mobileMoneySheet.show(fragmentManager, MobileMoneyBottomSheet.TAG)
            }
            "WORLD" -> {
                // Card payment implementation
            }
        }
    }


    private fun processMobileMoneyPayment(
        provider: Provider,
        transactionData: TransactionData,
        phoneNumber: String,
        callback: (PaymentResult) -> Unit
    ) {
        MainScope().launch {
            try {
                val request = ProcessPaymentRequest(
                    gateway = provider.shortcode,
                    amount = totalAmount,
                    transaction_id = transactionData.transaction_id,
                    return_url = returnUrl,
                    phone_number = phoneNumber,
                    currency = currency,
                    paymentType = "button",
                    notify_url = notifyUrl
                  )

                val response = withContext(Dispatchers.IO) {
                    PayUnitApiClient(apiUsername,apiPassword, apiKey,mode).api.processMobilePayment(request)
                }


                if (response.isSuccessful) {
                    response.body()?.let { paymentResponse ->
                        if (paymentResponse.statusCode == 200) {
                            callback(PaymentResult.Success("Payment Completed"))
                            checkPaymentStatus(transactionData.transaction_id)
                        } else {
                            callback(PaymentResult.Error(paymentResponse.message))
//                            paymentCallback?.onError(paymentResponse.message)
                            showDialog(context, paymentResponse.message)
                        }
                    } ?: run {
                        callback(PaymentResult.Error("Empty response from server"))
//                        paymentCallback?.onError("Empty response from server")
                        showDialog(context, "Empty response from server")
                    }
                } else {
                    callback(PaymentResult.Error("Payment failed: ${response.message()}"))
//                    paymentCallback?.onError("Payment failed: ${response.message()}")
                    showDialog(context, "Payment failed: ${response.message()}")
                }
            } catch (e: Exception) {
                callback(PaymentResult.Error(e.message ?: "Unknown error occurred"))
//                paymentCallback?.onError(e.message ?: "Unknown error occurred")
                showDialog(context, "Unknown error occurred")
            }
        }
    }

    private fun checkPaymentStatus(transactionId: String) {

      MainScope().launch {
          processingDialog.show()

        while (state) {

            val call = PayUnitApiClient(apiUsername,apiPassword, apiKey,mode).api.getPaymentStatus(transactionId)

            call.enqueue(object : Callback<PaymentStatusResponse> {
                override fun onResponse(call: Call<PaymentStatusResponse>, response: Response<PaymentStatusResponse>) {
                    if (response.isSuccessful) {
                        val paymentStatus = response.body()
                        if (paymentStatus != null && paymentStatus.statusCode == 200) {
                            // Break the loop if the status is SUCCESS or FAILED
                            if (paymentStatus.data.transaction_status == "SUCCESS" || paymentStatus.data.transaction_status == "FAILED") {
                                handlePaymentStatus(paymentStatus.data)
                                processingDialog.hide()
                                state = false // Exit the loop
                            }
                        } else {
                            // Handle error response
                            showDialog(context, "Error: ${paymentStatus?.message ?: "Unknown error"}")
                        }
                    } else {
                        showDialog(context, "Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<PaymentStatusResponse>, t: Throwable) {
                    showDialog(context, "Failure: ${t.message}")
                }
            })

            // Wait for 2 seconds before the next request
                delay(2000)
        }
      }
    }

    private fun handlePaymentStatus(data: PaymentData) {
        when (data.transaction_status) {
            "SUCCESS" -> {
                // Handle success case
                showDialog(context, "Payment Successful: ${data.message}")
                paymentCallback?.onComplete(
                    data.transaction_id,
                    data.transaction_status
                )
            }
            "FAILED" -> {
                // Handle failure case
                showDialog(context, "Payment Failed: ${data.message}")
                paymentCallback?.onComplete(
                    data.transaction_id,
                    data.transaction_status
                )
            }
            else -> {
                showDialog(context, "Unknown transaction status.")
            }
        }
    }

    private fun showLoadingDialog(message: String) {
        // We'll implement these dialog methods next
    }

    private fun showSuccessDialog(message: String) {
        // We'll implement these dialog methods next
    }

    private fun showErrorDialog(message: String) {
        // We'll implement these dialog methods next
    }

    private fun generateTransactionId(): String {
        return "pu-${System.currentTimeMillis()}"
    }

    // Java-friendly interface for callback
    interface PaymentCallback {
        fun onComplete(transactionId: String, transactionStatus: String)
//        fun onError(error: String)
//        fun onCancel()
    }


    // Java-friendly builder
    class Builder(private val context: Context) {
        private val button = PayUnitButton(context)

        fun apiUsername(username: String): Builder {
            button.apiUsername = username
            return this
        }

        fun apiPassword(password: String): Builder {
            button.apiPassword = password
            return this
        }

        fun apiKey(key: String): Builder {
            button.apiKey = key
            return this
        }

        fun mode(mode: String): Builder {
            button.mode = mode
            return this
        }

        fun paymentCountry(country: String): Builder {
            button.paymentCountry = country
            return this
        }

        fun totalAmount(amount: Int): Builder {
            button.totalAmount = amount
            return this
        }

        fun currency(currency: String): Builder {
            button.currency = currency
            return this
        }

        fun returnUrl(url: String): Builder {
            button.returnUrl = url
            return this
        }

        fun notifyUrl(url: String): Builder {
            button.notifyUrl = url
            return this
        }

//        fun buttonText(text: String): Builder {
//            button.setButtonText(text)
//            return this
//        }
//
//        fun buttonColor(color: Int): Builder {
//            button.setButtonColor(color)
//            return this
//        }
//
//        fun buttonTextColor(color: Int): Builder {
//            button.setButtonTextColor(color)
//            return this
//        }

        fun paymentCallback(callback: PaymentCallback): Builder {
            button.setPaymentCallback(callback)
            return this
        }

        fun build(): PayUnitButton {
            return button
        }
    }
}
