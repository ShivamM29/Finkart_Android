package com.investment.finkart.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.investment.finkart.R
import com.investment.finkart.adapters.InvestmentRecyclerAdapter
import com.investment.finkart.databinding.FragmentMyInvestmentBinding
import com.investment.finkart.config.AuthConfig
import com.investment.finkart.models.InvestmentData
import com.investment.finkart.models.InvestmentItems
import com.investment.finkart.network.RetrofitBuilder
import com.investment.finkart.utils.CommonLogout
import com.investment.finkart.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyInvestmentFragment : Fragment() {
    private lateinit var binding: FragmentMyInvestmentBinding
    private lateinit var authConfig: AuthConfig
    private lateinit var retrofitBuilder: RetrofitBuilder
    private lateinit var navController: NavController
    private lateinit var commonLogout: CommonLogout                 // this is the class called when the token has expired and user should redirect to login screen

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentMyInvestmentBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    private fun initView() {
        authConfig = AuthConfig(requireContext())
        retrofitBuilder = RetrofitBuilder()
        navController = findNavController()
        commonLogout = CommonLogout(requireContext())

        getInvestment()

        binding.ibBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun getInvestment(){
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()
        val call = retrofitBuilder.getFinkartService().getInvestment(authToken)

        try {
            call.enqueue(object: Callback<InvestmentData>{
                override fun onResponse(call: Call<InvestmentData>, response: Response<InvestmentData>) {
                    if(response.isSuccessful){
                        val data = response.body()
                        if (data != null){
                            when(data.subCode){
                                Constants.RESPONSE_SUCCESS -> {
                                    val investmentList = data.items
                                    if(!investmentList.isNullOrEmpty()){
                                        setData(investmentList)
                                    }else{
                                        binding.rvInvestments.visibility = View.GONE
                                        binding.lvInfo.visibility = View.VISIBLE
                                    }
                                }
                                Constants.RESPONSE_FORBIDDEN -> {
                                    commonLogout.logout(authConfig)
                                }
                                else -> {
                                    binding.rvInvestments.visibility = View.GONE
                                    binding.lvInfo.visibility = View.VISIBLE
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

                override fun onFailure(call: Call<InvestmentData>, t: Throwable) {
                    onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG)
                    binding.shimmerContainer.visibility = View.GONE
                    binding.shimmerContainer.stopShimmer()
                }
            })
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun setData(investmentList: List<InvestmentItems>){
        val adapter = InvestmentRecyclerAdapter(
            requireContext(),
            investmentList
        )
        binding.rvInvestments.adapter = adapter
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
        binding.rvInvestments.visibility = View.GONE
        binding.lvInfo.visibility = View.GONE


        binding.lvError.visibility = View.VISIBLE
        binding.ivErrorImage.setImageResource(image)
        binding.tvErrorMessage.text = message

        binding.btnRetry.setOnClickListener {
            binding.rvInvestments.visibility = View.VISIBLE
            binding.lvError.visibility = View.GONE

            binding.shimmerContainer.visibility = View.VISIBLE
            binding.shimmerContainer.startShimmer()

            getInvestment()
        }
    }
}