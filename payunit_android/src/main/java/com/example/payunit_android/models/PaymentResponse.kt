data class ProcessPaymentResponse(
    val status: String,
    val statusCode: Int,
    val message: String,
    val data: PaymentData
)

data class PaymentData(
    val transaction_id: String,
    val payment_status: String,
    val amount: Int
)