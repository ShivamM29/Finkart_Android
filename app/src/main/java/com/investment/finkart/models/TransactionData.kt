package com.investment.finkart.models

import java.util.Date

data class TransactionData(
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: List<TransactionItems>?
)

data class TransactionItems(
    val type: String?,
    val amount: Float?,
    val createdAt: Date?
)