package com.investment.finkart.models

data class BankData (
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: List<BankItems>?
)

data class BankItems(
    val customId: String?,
    val bankId: String?,
    val bankName: String?,
    val accountNumber: String?,
    val ifsc: String?,
    val accountHolderName: String?,
    val upiId: String?
)
