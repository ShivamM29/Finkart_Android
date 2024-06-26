package com.investment.finkart.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.investment.finkart.R
import com.investment.finkart.adapters.TransactionRecyclerAdapter
import com.investment.finkart.config.AuthConfig
import com.investment.finkart.databinding.FragmentGeneralTransactionBinding
import com.investment.finkart.models.TransactionData
import com.investment.finkart.models.TransactionItems
import com.investment.finkart.network.RetrofitBuilder
import com.investment.finkart.utils.CommonLogout
import com.investment.finkart.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeneralTransactionFragment : Fragment() {
    private lateinit var binding: FragmentGeneralTransactionBinding
    private lateinit var retrofitBuilder: RetrofitBuilder
    private lateinit var authConfig: AuthConfig
    private lateinit var commonLogout: CommonLogout                 // this is the class called when the token has expired and user should redirect to login screen

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentGeneralTransactionBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        retrofitBuilder = RetrofitBuilder()
        authConfig = AuthConfig(requireContext())
        commonLogout = CommonLogout(requireContext())

        getTransaction()
    }

    private fun getTransaction(){
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()
        val call = retrofitBuilder.getFinkartService().getAllTransactions(authToken)

        try {
            call.enqueue(object : Callback<TransactionData>{
                override fun onResponse(call: Call<TransactionData>, response: Response<TransactionData>) {
                    if (response.isSuccessful){
                        val data = response.body()
                        if (data != null){

                            when (data.subCode) {
                                Constants.RESPONSE_SUCCESS -> {
                                    val transactionList = data.items
                                    setData(transactionList!!)
                                }

                                Constants.RESPONSE_BAD_REQUEST -> {
                                    // no data found
                                    binding.rvTransaction.visibility = View.GONE
                                    binding.lvInfo.visibility = View.VISIBLE
                                }

                                Constants.RESPONSE_FORBIDDEN -> {
                                    commonLogout.logout(authConfig)
                                }
                            }
                        }else{
                            onError(response.message())
                        }
                    }else{
                        onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
                    }

                    binding.shimmerContainer.visibility = View.GONE
                    binding.shimmerContainer.stopShimmer()
                }

                override fun onFailure(call: Call<TransactionData>, t: Throwable) {
                    onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
                    binding.shimmerContainer.visibility = View.GONE
                    binding.shimmerContainer.stopShimmer()
                }
            })
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun setData(transactionList: List<TransactionItems>){
        val adapter = TransactionRecyclerAdapter(requireContext(), transactionList)
        binding.rvTransaction.adapter = adapter
    }

    private fun onError(message: String){
        when(message){
            Constants.ERROR_MESSAGE_NETWORK -> {
                showError(R.drawable.no_internet, Constants.TEXT_NO_INTERNET_CONNECTION)
            }

            Constants.ERROR_MESSAGE_TIMEOUT -> {
                showError(R.drawable.something_went_wrong, Constants.ERROR_MESSAGE_TIMEOUT)
            }

            else -> {
                showError(R.drawable.something_went_wrong, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
            }
        }
    }

    private fun showError(image: Int, message: String){
        binding.shimmerContainer.visibility = View.GONE
        binding.lvInfo.visibility = View.GONE
        binding.rvTransaction.visibility = View.GONE

        binding.lvError.visibility = View.VISIBLE
        binding.ivErrorImage.setImageResource(image)
        binding.tvErrorMessage.text = message

        binding.btnRetry.setOnClickListener {
            getTransaction()

            binding.lvError.visibility = View.GONE
            binding.rvTransaction.visibility = View.VISIBLE
            binding.shimmerContainer.visibility = View.VISIBLE
            binding.shimmerContainer.startShimmer()
        }
    }
}