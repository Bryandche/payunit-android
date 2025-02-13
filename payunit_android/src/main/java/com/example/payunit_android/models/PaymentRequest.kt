data class ProcessPaymentRequest(
    val gateway: String,
    val amount: Int,
    val transaction_id: String,
    val return_url: String,
    val phone_number: String,
    val currency: String,
    val paymentType: String,
    val notify_url:String
)

