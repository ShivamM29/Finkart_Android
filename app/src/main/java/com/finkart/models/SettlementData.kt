package com.finkart.models

import java.util.Date

data class SettlementData(
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: List<SettlementItems?>?
)

data class SettlementItems(
    val _id: String?,
    val customId: String?,
    val amount: Float?,
    val status: String?,
    val createdAt: Date?
)