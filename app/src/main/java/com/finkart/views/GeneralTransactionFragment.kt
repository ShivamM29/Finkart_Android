package com.finkart.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import com.finkart.R
import com.finkart.adapters.TransactionRecyclerAdapter
import com.finkart.config.AuthConfig
import com.finkart.databinding.FragmentGeneralTransactionBinding
import com.finkart.databinding.FragmentTransactionBinding
import com.finkart.models.TransactionData
import com.finkart.models.TransactionItems
import com.finkart.network.RetrofitBuilder
import com.finkart.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeneralTransactionFragment : Fragment() {
    private lateinit var binding: FragmentGeneralTransactionBinding
    private lateinit var retrofitBuilder: RetrofitBuilder
    private lateinit var authConfig: AuthConfig

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentGeneralTransactionBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        retrofitBuilder = RetrofitBuilder()
        authConfig = AuthConfig(requireContext())

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
                            }
                        }else{
                            onError(response.message())
                        }
                    }else{
                        onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
                    }
                }

                override fun onFailure(call: Call<TransactionData>, t: Throwable) {
                    onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
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