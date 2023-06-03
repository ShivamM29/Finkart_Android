package com.finkart.models

data class InvestmentData(
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: List<InvestmentItems>?
)

data class InvestmentItems(
    val amount: Float?,
    val interest: Float?,
    val locking: Int?,
    val planName: String?,
    val date: String?,
    val expireDate: String?,
    val isActive: Boolean?,
    val profit: Float?
)