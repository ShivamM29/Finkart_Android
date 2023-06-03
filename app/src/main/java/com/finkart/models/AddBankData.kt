package com.marvellous.mining.account.models

data class AddBankData(
    val status: Boolean?,
    val subCode: Int?,
    val message: String?,
    val items: BankItems?
)
