package com.finkart.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.finkart.R
import com.finkart.config.AuthConfig
import com.finkart.databinding.EditBankBottomSheetBinding
import com.finkart.databinding.FragmentBankDetailsBinding
import com.finkart.network.RetrofitBuilder
import com.finkart.utils.Constants
import com.finkart.utils.FinkartLoading
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.marvellous.mining.account.models.AddBankData
import com.marvellous.mining.account.models.BankData
import com.marvellous.mining.account.models.BankItems
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBankDetailsBinding
    private lateinit var authConfig: AuthConfig
    private lateinit var retrofitBuilder: RetrofitBuilder
    private lateinit var bankItems: BankItems
    private lateinit var editBottomSheetBinding: EditBankBottomSheetBinding
    private lateinit var editBottomSheetDialog: BottomSheetDialog
    private lateinit var finkartLoading: FinkartLoading

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        initView()
        return inflater.inflate(R.layout.fragment_bank_details, container, false)
    }

    private fun initView() {
        authConfig = AuthConfig(requireContext())
        retrofitBuilder = RetrofitBuilder()
        finkartLoading = FinkartLoading()

        binding.lvBankCard.setOnClickListener{
            launchEditBottomSheet()
            setEditBankDetails()
        }
    }

    private fun getBank() {
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()

        val call = retrofitBuilder.getFinkartService().getBank(authToken)
        call.enqueue(object : Callback<BankData> {
            override fun onResponse(call: Call<BankData>, response: Response<BankData>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        val subCode = data.subCode
                        val items = data.items
                        when (subCode) {
                            Constants.RESPONSE_SUCCESS -> {
                                if (items != null) {
                                    try {
                                        binding.lvBankCard.visibility = View.VISIBLE
                                        bankItems = items[0]
                                        setBankDetails()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    binding.lvBankCard.visibility = View.GONE
                                    binding.lvInfo.visibility = View.VISIBLE
                                }
                            }

                            Constants.RESPONSE_BAD_REQUEST -> {
                                // check for bad request
                                val message = data.message
                                if (message == "Bank not added") {
                                    binding.scrollAdd.visibility = View.VISIBLE


                                    binding.lvInfo.visibility = View.VISIBLE
                                } else {
                                    onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "get")
                                }
                            }
                        }
                    }
                } else {
                    onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "get")
                }
                binding.shimmerContainer.visibility = View.GONE
                binding.shimmerContainer.stopShimmer()
            }

            override fun onFailure(call: Call<BankData>, t: Throwable) {
                binding.shimmerContainer.visibility = View.GONE
                binding.shimmerContainer.stopShimmer()
                onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "get")
            }

        })
    }

    private fun setBankDetails(){
        binding.tvBankName.text = bankItems.bankName
        binding.tvAccountHolderName.text = bankItems.accountHolderName
        binding.tvAccountNumber.text = bankItems.accountNumber
        binding.tvIfsc.text = bankItems.ifsc
        val upiId = bankItems.upiId
        upiId?.let {
            binding.tvUpiId.text = it
        }
    }

    private fun isValidInput(): Boolean{
        var isValid = true

        val name = binding.etName.text.toString()
        val accountNo = binding.etAccountNumber.text.toString()
        val ifsc = binding.etIFSC.text.toString()
        val bankName = binding.etBankName.text.toString()

        if (name.isEmpty()){
            binding.etName.error = "Name is required"
            isValid = false
        }

        if (accountNo.isEmpty()){
            binding.etAccountNumber.error = "Account No. is required"
            isValid = false
        }

        if (ifsc.isEmpty()){
            binding.etIFSC.error = "IFSC is required"
            isValid = false
        }

        if (bankName.isEmpty()){
            binding.etBankName.error = "Bank Name is required"
            isValid = false
        }

        return isValid
    }

    private fun addBank(){
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()

        val name = binding.etName.text.toString()
        val accountNo = binding.etAccountNumber.text.toString()
        val ifsc = binding.etIFSC.text.toString()
        val bankName = binding.etBankName.text.toString()
        val upiId = binding.etUpiId.text.toString()

        val bankDetails = HashMap<String, Any>()
        bankDetails["accountHolderName"] = name
        bankDetails["accountNumber"] = accountNo
        bankDetails["ifsc"] = ifsc
        bankDetails["bankName"] = bankName
        bankDetails["upiId"] = upiId

        val call = retrofitBuilder.getFinkartService().addBank(authToken, bankDetails)
        call.enqueue(object : Callback<AddBankData>{
            override fun onResponse(call: Call<AddBankData>, response: Response<AddBankData>) {
                if (response.isSuccessful){
                    val data = response.body()
                    if (data != null){
                        val status = data.status
                        when(data.subCode){
                            Constants.RESPONSE_SUCCESS, Constants.RESPONSE_CREATED -> {
                                binding.scrollAdd.visibility = View.GONE
                                binding.lvInfo.visibility = View.GONE
                                bankItems = data.items!!
                                setBankDetails()
                            }

                            Constants.RESPONSE_BAD_REQUEST -> {
                                val message = data.message
                                if (message == "cannot add multiple bank"){
                                    Toast.makeText(context, "Bank already added", Toast.LENGTH_SHORT).show()
                                }else {
                                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "add")
                    }
                }else{
                    onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "add")
                }
                finkartLoading.hideProgress()
            }

            override fun onFailure(call: Call<AddBankData>, t: Throwable) {
                onError(Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, "add")
                finkartLoading.hideProgress()
            }

        })
    }

    private fun onError(message: String, from: String){
        when(message){
            Constants.ERROR_MESSAGE_NETWORK -> {
                showError(R.drawable.no_internet, Constants.TEXT_NO_INTERNET_CONNECTION, from)
            }

            Constants.ERROR_MESSAGE_TIMEOUT -> {
                showError(R.drawable.something_went_wrong, Constants.ERROR_MESSAGE_TIMEOUT,from)
            }

            else -> {
                showError(R.drawable.something_went_wrong, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, from)
            }
        }
    }

    private fun showError(image: Int, message: String, from: String){
        binding.lvInfo.visibility = View.GONE
        binding.shimmerContainer.visibility = View.GONE
        binding.scrollAdd.visibility = View.GONE

        binding.lvError.visibility = View.VISIBLE
        binding.ivErrorImage.setImageResource(image)
        binding.tvErrorMessage.text = message

        binding.btnRetry.setOnClickListener {

            if (from == "add"){
                finkartLoading.showProgress(requireContext())
                binding.scrollAdd.visibility = View.VISIBLE
                addBank()
            }else{
                getBank()
                binding.shimmerContainer.visibility = View.VISIBLE
                binding.shimmerContainer.startShimmer()
            }

            binding.lvError.visibility = View.GONE
//            binding.scrollAdd.visibility = View.VISIBLE
        }
    }

    private fun launchEditBottomSheet(){
        val inflater = LayoutInflater.from(context)
        editBottomSheetBinding = EditBankBottomSheetBinding.inflate(inflater)
        editBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        editBottomSheetDialog.setContentView(editBottomSheetBinding.root)
        editBottomSheetDialog.create()
        editBottomSheetDialog.show()

        editBottomSheetBinding.btnUpdate.setOnClickListener {
            editBank()
        }
    }

    private fun setEditBankDetails(){             // this function will set the bank details on Edit bottom sheet
        editBottomSheetBinding.etName.setText(bankItems.accountHolderName)
        editBottomSheetBinding.etBankName.setText(bankItems.bankName)
        editBottomSheetBinding.etAccountNo.setText(bankItems.accountNumber)
        editBottomSheetBinding.etIFSC.setText(bankItems.ifsc)
        val upiId = bankItems.upiId
        upiId?.let {
            editBottomSheetBinding.etUPI.setText(it)
        }
    }

    private fun editBank() {
        finkartLoading.showProgress(requireContext())
        editBottomSheetDialog.dismiss()

        var isValidInput = false
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()
        val bankDetails = HashMap<String, Any>()

        val name = editBottomSheetBinding.etName.text.toString()
        val accountNo = editBottomSheetBinding.etAccountNo.text.toString()
        val ifsc = editBottomSheetBinding.etIFSC.text.toString()
        val bankName = editBottomSheetBinding.etBankName.text.toString()
        val upiId = editBottomSheetBinding.etUPI.text.toString()


        if (name.isNotEmpty()) {
            isValidInput = true
            bankDetails["accountHolderName"] = name
        }
        if (accountNo.isNotEmpty()){
            isValidInput = true
            bankDetails["accountNumber"] = accountNo
        }
        if (ifsc.isNotEmpty()){
            isValidInput = true
            bankDetails["ifsc"] = ifsc
        }
        if (bankName.isNotEmpty()){
            isValidInput = true
            bankDetails["bankName"] = bankName
        }
        if (upiId.isNotEmpty()){
            isValidInput = true
            bankDetails["upiId"] = upiId
        }

        if (isValidInput){
            val call = retrofitBuilder.getFinkartService().editBank(authToken, bankItems.bankId!!, bankDetails)
            call.enqueue(object: Callback<AddBankData>{
                override fun onResponse(call: Call<AddBankData>, response: Response<AddBankData>) {
                    if (response.isSuccessful){
                        val data = response.body()
                        if (data != null){
                            when(data.subCode){
                                Constants.RESPONSE_SUCCESS -> {
                                    bankItems = data.items!!

                                    editBottomSheetDialog.dismiss()
                                }

                                Constants.RESPONSE_BAD_REQUEST -> {
                                    Toast.makeText(requireContext(), "Invalid Input", Toast.LENGTH_SHORT).show()
                                }

                                else -> {
                                    Toast.makeText(requireContext(), Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                    }
                    finkartLoading.hideProgress()
                }

                override fun onFailure(call: Call<AddBankData>, t: Throwable) {
                    finkartLoading.hideProgress()
                    Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                }

            })
        }else{
            finkartLoading.hideProgress()
            Toast.makeText(context, "Nothing has changed", Toast.LENGTH_SHORT).show()
        }
    }
}