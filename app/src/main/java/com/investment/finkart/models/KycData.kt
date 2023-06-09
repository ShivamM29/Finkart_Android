package com.investment.finkart.models

data class KycData(
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: KycItems?
)

data class KycItems(
    val kyc: Kyc?,
    val nominee: Nominee?
)

data class Kyc(
    val _id: String?,
    val customId: String?,
    val name: String?,
    val occupation: String?,
    val aadhaarNumber: String?,
    val panNumber: String?,
    val isVerified: Boolean?
)

data class Nominee(
    val _id: String?,
    val customerId: String?,
    val nomineeName: String?,
    val nomineeRelation: String?,
    val nomineeAadhaarNo: String?
)