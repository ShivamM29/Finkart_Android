package com.finkart.network

import com.finkart.models.BalanceData
import com.finkart.models.GenericResponse
import com.finkart.models.InvestmentData
import com.finkart.models.KycData
import com.finkart.models.LoginData
import com.finkart.models.SettlementData
import com.finkart.models.TransactionData
import com.marvellous.mining.account.models.AddBankData
import com.marvellous.mining.account.models.BankData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface Services {
    @POST("newauth/login/")
    fun register(@Body verifyDetails: HashMap<String, String>): Call<LoginData>

    @GET("customer/getBalanceUser/")
    fun getBalance(@Header("Authorization") authToken: String): Call<BalanceData>

    @POST("customer/onboard/")
    fun userOnBoard(@Header("Authorization") authToken: String, @Body onBoardDetails: HashMap<String, String>): Call<LoginData>

    @GET("transaction/alltransaction/")
    fun getAllTransactions(@Header("Authorization") authToken: String): Call<TransactionData>

    @GET("customer/settlement/")
    fun getAllSettlements(@Header("Authorization") authToken: String): Call<SettlementData>

    @GET("customer/investment")
    fun getInvestment(@Header("Authorization") authToken: String): Call<InvestmentData>

    @GET("customer/kycinfo/nomineeinfo/")
    fun getKyc(@Header("Authorization") authToken: String): Call<KycData>

    @POST("customer/kyc/nominee/")
    fun doKyc(@Header("Authorization") authToken: String, @Body kycDetails: HashMap<String, Any>): Call<GenericResponse>

    @POST("customer/create/investment")
    fun createInvestment(@Header("Authorization") authToken: String, @Body investmentDetails: HashMap<String,Any>): Call<GenericResponse>

    @POST("customer/bank/")
    fun addBank(@Header("Authorization") authToken: String, @Body bankDetails: HashMap<String, Any>): Call<AddBankData>

    @POST("customer/editBank/{id}")
    fun editBank(@Header("Authorization") authToken: String, @Path("id") bankId: String, @Body bankDetails: HashMap<String, Any>): Call<AddBankData>

    @GET("customer/bank/")
    fun getBank(@Header("Authorization") authToken: String): Call<BankData>
}