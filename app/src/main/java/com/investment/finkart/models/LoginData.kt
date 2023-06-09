package com.investment.finkart.models

data class LoginData(
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: LoginItems?
)

data class LoginItems(
    val reqId: String?,
    val isOnboarded: Boolean?,
    val token: String?,
    val name: String?,
    val profileImage: String?,
    val occupation: String?
)