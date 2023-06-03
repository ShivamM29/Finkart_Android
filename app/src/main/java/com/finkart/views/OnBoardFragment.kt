package com.finkart.views

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.finkart.config.AuthConfig
import com.finkart.databinding.FragmentOnBoardBinding
import com.finkart.models.LoginData
import com.finkart.network.RetrofitBuilder
import com.finkart.utils.Constants
import com.finkart.utils.FinkartLoading
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class OnBoardFragment : Fragment() {
    private lateinit var binding: FragmentOnBoardBinding
    private lateinit var finkartLoading: FinkartLoading
    private lateinit var authConfig: AuthConfig
    private lateinit var retrofitBuilder: RetrofitBuilder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    private fun initView(){
        finkartLoading = FinkartLoading()
        authConfig = AuthConfig(requireContext())
        retrofitBuilder = RetrofitBuilder()

        binding.btnAdd.setOnClickListener {
            if (validateInput()){
                showNoticeAlert()
            }
        }
    }

    private fun validateInput(): Boolean{
        var isValidInput = true

        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val city = binding.etCity.text.toString()
        val occupation = binding.etOccupation.text.toString()


        if (name.isEmpty()){
            binding.etName.error = "Name is Required"
            isValidInput = false
        }

        if(email.isEmpty()){
            binding.etEmail.error = "Email is Required"
            isValidInput = false
        }

        if (city.isEmpty()){
            binding.etCity.error = "City is Required"
            isValidInput = false
        }

        if (occupation.isEmpty()){
            binding.etOccupation.error = "Occupation is Required"
            isValidInput = false
        }

        return isValidInput
    }

    private fun showNoticeAlert(){
        AlertDialog.Builder(context)
            .setTitle("Note")
            .setMessage("Make sure all the details filled by you are correct and are not misleading or false. " +
                    "These details will be used to create your investment agreement.")
            .setPositiveButton("Ok"){_,_ ->
                finkartLoading.showProgress(requireContext())
                userOnBoard()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun userOnBoard(){
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()
        val onBoardDetails = HashMap<String, String>()
        onBoardDetails["name"] = binding.etName.text.toString()
        onBoardDetails["email"] = binding.etEmail.text.toString()
        onBoardDetails["city"] = binding.etCity.text.toString()
        onBoardDetails["occupation"] = binding.etOccupation.text.toString()

        try {
            val call = retrofitBuilder.getFinkartService().userOnBoard(authToken, onBoardDetails)
            call.enqueue(object : Callback<LoginData>{
                override fun onResponse(call: Call<LoginData>, response: Response<LoginData>) {
                    if (response.isSuccessful){
                        val data = response.body()
                        when (data?.subCode){
                            Constants.RESPONSE_SUCCESS, Constants.RESPONSE_CREATED -> {
                                val token = data.items?.token
                                if (token != null) {
                                    authConfig.setAuth(data.items.token, true)
                                    authConfig.setProfile(data.items.name!!, data.items.profileImage!!, data.items.occupation!!)
                                    authConfig.setIsLogin(true)

                                }else{
                                    // token is null
                                    Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                                }
                            }
                            Constants.RESPONSE_BAD_REQUEST -> {
                                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }

                    finkartLoading.hideProgress()
                }

                override fun onFailure(call: Call<LoginData>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    finkartLoading.hideProgress()
                }
            })
        }catch (e: Exception){
            e.printStackTrace()
        }

    }
}