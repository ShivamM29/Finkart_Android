package com.investment.finkart.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.investment.finkart.R
import com.investment.finkart.databinding.FragmentWalletBinding
import com.investment.finkart.config.AuthConfig
import com.investment.finkart.models.BalanceData
import com.investment.finkart.models.BalanceItems
import com.investment.finkart.network.RetrofitBuilder
import com.investment.finkart.utils.Constants
import com.investment.finkart.utils.OhSnapErrorAlert
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class WalletFragment : Fragment() {
    private lateinit var binding: FragmentWalletBinding
    private lateinit var authConfig: AuthConfig
    private lateinit var retrofitBuilder: RetrofitBuilder
    private lateinit var ohSnapErrorAlert: OhSnapErrorAlert
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    private fun initView() {
        navController = findNavController()
        authConfig = AuthConfig(requireContext())
        retrofitBuilder = RetrofitBuilder()
        ohSnapErrorAlert = OhSnapErrorAlert()

        binding.ibBack.setOnClickListener {
            navController.popBackStack()
        }
        getBalance()
    }

    private fun getBalance(){
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()
        val call = retrofitBuilder.getFinkartService().getBalance(authToken)

        try {
            call.enqueue(object : Callback<BalanceData>{
                override fun onResponse(call: Call<BalanceData>, response: Response<BalanceData>) {
                    if (response.isSuccessful){
                        val data = response.body()
                        if (data != null){
                            when(data.subCode){
                                Constants.RESPONSE_SUCCESS -> {
                                    val items = data.items
                                    binding.tvAmount.visibility = View.VISIBLE
                                    binding.tvProfit.visibility = View.VISIBLE
                                    binding.animationView.setAnimation(R.raw.wallet)
                                    setData(items!!)
                                }

                                else -> {
                                    onError(data.message!!)
                                }
                            }
                        }else{
                            onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
                        }
                    }else{
                        onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
                    }

                    binding.shimmerAmount.stopShimmer()
                    binding.shimmerProfit.stopShimmer()
                    binding.shimmerAmount.visibility = View.GONE
                    binding.shimmerProfit.visibility = View.GONE
                }

                override fun onFailure(call: Call<BalanceData>, t: Throwable) {
                    onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
                    binding.shimmerAmount.stopShimmer()
                    binding.shimmerProfit.stopShimmer()
                    binding.shimmerAmount.visibility = View.GONE
                    binding.shimmerProfit.visibility = View.GONE
                }
            })
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun setData(balanceItems: BalanceItems) {
        val amount = if (balanceItems.investAmount!! % 10 > 0) DecimalFormat("##,##,###.##").format(balanceItems.investAmount) else DecimalFormat("##,##,###").format(balanceItems.investAmount)
        binding.tvAmount.text = Constants.RUPEE_SYMBOL_PREFIX + amount

        val profit = if (balanceItems.profit!! % 10 > 0) DecimalFormat("##,##,###.##").format(balanceItems.profit) else DecimalFormat("##,##,###").format(balanceItems.profit)
        binding.tvProfit.text = Constants.RUPEE_SYMBOL_PREFIX + profit
    }

    private fun onError(message: String){
        when(message){
            Constants.ERROR_MESSAGE_NETWORK -> {
                launchErrorDialog(R.drawable.no_internet, Constants.TEXT_NO_INTERNET_CONNECTION)
            }

            Constants.ERROR_MESSAGE_TIMEOUT -> {
                launchErrorDialog(R.drawable.something_went_wrong, Constants.ERROR_MESSAGE_TIMEOUT)
            }

            else -> {
                launchErrorDialog(R.drawable.something_went_wrong, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
            }
        }
    }

    private fun launchErrorDialog(image: Int, message: String){
        ohSnapErrorAlert.showAlert(requireContext(), navController, message, image)
    }
}