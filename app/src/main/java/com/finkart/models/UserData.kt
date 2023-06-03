package com.finkart.models

data class UserData(
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: UserItems?
)

data class UserItems(
    val userId: String?,
    val customerId: String?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val city: String?,
    val occupation: String?,
    val profileImage: String?,
    val isVerified: Boolean?
)