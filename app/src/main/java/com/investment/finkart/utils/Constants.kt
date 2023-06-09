package com.investment.finkart.utils

class Constants {
    companion object{
        const val BASE_URL = "http://139.59.25.54:7000/v1/"
        const val TOKEN_PREFIX_BEARER = "Bearer "

        const val RESPONSE_SUCCESS = 200
        const val RESPONSE_CREATED = 201
        const val RESPONSE_BAD_REQUEST = 400
        const val RESPONSE_UNAUTHORIZED = 403
        const val RESPONSE_FORBIDDEN = 401
        const val RESPONSE_PAGE_NOT_FOUND = 404

        const val INTENT_VERSION_NAME = "versionName"
        const val INTENT_PHONE = "phone"

        const val INTENT_REQ_ID = "reqId"
        const val INTENT_RIG_ID = "rigId"
        const val INTENT_BOOKING_ID = "bookingId"
        const val INTENT_RIG_MAP = "rigMap"
        const val INTENT_PARTNERSHIP_RATIO = "ratio"
        const val INTENT_AMOUNT= "amount"
        const val INTENT_ANNUAL_INCOME = "income"
        const val INTENT_AVAILABLE_RIGS = "rigs"
        const val INTENT_AVAILABLE_SLOT = "slot"
        const val INTENT_MOBILE_NO = "mobileNo"
        const val INTENT_FROM = "from"
        const val INTENT_BOOKING_AMOUNT = "bookingAmount"
        const val INTENT_REMAINING_AMOUNT = "remainingAmount"

        const val INTENT_FROM_PURCHASE = "PURCHASE"
        const val INTENT_FROM_BOOKING = "BOOKING"
        const val INTENT_FROM_BOOKED = "BOOKED"
        const val INTENT_FROM_TERMS = "TERMS"
        const val INTENT_FROM_PRIVACY = "PRIVACY"

        const val RUPEE_SYMBOL_PREFIX = "₹ "

        const val TRANSACTION_TYPE_CREDITED = "Credited"
        const val TRANSACTION_TYPE_INVESTED = "Invested"

        const val ERROR_MESSAGE_NETWORK = "Please check your connection"
        const val ERROR_MESSAGE_TIMEOUT = "Server Timeout"
        const val ERROR_MESSAGE_SOMETHING_WENT_WRONG = "Something went wrong"

        const val TEXT_NO_DATA_FOUND = "No data found"
        const val TEXT_NO_INTERNET_CONNECTION = "No Internet Connection"

        const val KYC_IMAGE_AADHAAR_FRONT = "AadhaarFront"
        const val KYC_IMAGE_AADHAAR_BACK = "AadhaarBack"
        const val KYC_IMAGE_SELFIE = "Selfie"
        const val KYC_IMAGE_PAN_CARD = "Pancard"

        const val SETTLEMENT_STATUS_PROCESSING = "Processing"
        const val SETTLEMENT_STATUS_SETTLED = "Settled"

        //        ---------------------------------- version 2 -----------------------------------------
        const val INTENT_PLAN = "Plan"
        const val INTENT_INTEREST = "Interest"

        const val PLAN_BASIC = "Basic"
        const val PLAN_Master = "Master"
        const val PLAN_ADVANCE = "Advance"
        const val PLAN_Exclusive = "Exclusive"

        const val LOCKING_PERIOD = 12

        const val BASIC_PLAN_PERCENT_ANNUAL = 20
        const val MASTER_PLAN_PERCENT_ANNUAL = 25
        const val ADVANCE_PLAN_PERCENT_ANNUAL = 30

        const val BASIC_PLAN_PERCENT_MONTHLY = 1.67
        const val MASTER_PLAN_PERCENT_MONTHLY = 2.08
        const val ADVANCE_PLAN_PERCENT_MONTHLY = 2.50

        const val PLAN_BASIC_START_PRICE = 5000
        const val PLAN_BASIC_END_PRICE = 14999

        const val PLAN_MASTER_START_PRICE = 15000
        const val PLAN_MASTER_END_PRICE = 29999

        const val PLAN_ADVANCE_START_PRICE = 30000
        const val PLAN_ADVANCE_END_PRICE = 100000
    }
}