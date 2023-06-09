package com.investment.finkart.models

data class BalanceData(
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: BalanceItems?
)

data class BalanceItems(
    val _id: String?,
    val customId: String?,
    val investAmount: Float?,
    val profit: Float?,
)