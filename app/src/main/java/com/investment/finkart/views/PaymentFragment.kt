package com.investment.finkart.views

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.investment.finkart.R
import com.investment.finkart.databinding.FragmentPaymentBinding
import com.investment.finkart.databinding.PaymentRecommendationDialogBinding
import com.investment.finkart.databinding.PaymentSuccessAlertViewBinding
import com.investment.finkart.databinding.PaymentUnsuccessAlertViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.investment.finkart.config.AuthConfig
import com.investment.finkart.models.GenericResponse
import com.investment.finkart.network.RetrofitBuilder
import com.investment.finkart.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding
    private lateinit var authConfig: AuthConfig

    private var totalAmount: Float = 0f
    private lateinit var retrofitBuilder: RetrofitBuilder
    private var upiActivityResult: ActivityResultLauncher<Intent>? = null
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        activityResult()
        initView()

        return binding.root
    }

    private fun initView() {
        navController = findNavController()
        retrofitBuilder = RetrofitBuilder()
        authConfig = AuthConfig(requireContext())

        totalAmount = arguments?.getString(Constants.INTENT_AMOUNT).toString().toFloat()

        showRecommendationNotice()
    }

    private fun showRecommendationNotice(){
        val layoutInflater = LayoutInflater.from(context)
        val alertBinding = PaymentRecommendationDialogBinding.inflate(layoutInflater)

        var alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)
        alertDialog.setView(alertBinding.root)
        alertDialog.show()

        alertBinding.btnDone.setOnClickListener {
            alertDialog.dismiss()
            alertDialog = null

//            launchUpiPayment()
        }

    }

    private fun activityResult(){
        upiActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it?.let { result ->
                try {
                    val orderId = arguments?.getString("receipt")
                    val paymentId = "pay_$orderId"
                    val data: Intent = result.data!!

                    when (data.getStringExtra("Status")) {
                        "SUCCESS" -> {
                            createInvestment()
                        }
                        "FAILURE", "Failed" -> {
                            launchUnSuccessDialog()
                        }
                        else -> {
                            launchUnSuccessDialog()
                        }
                    }
                } catch (e: java.lang.Exception) {
                    launchUnSuccessDialog()
                }
            }
        }
    }

    private fun launchUpiPayment() {
//        try {
//            val amount = "am=$totalAmount"
//            val UPI = "upi://pay?pa=merchant1259991.augp@aubank&tn=&$amount&mam=&orgid=159765&pn=merchant1103141&mode=02&purpose=00&mc=0000&tr=&url=&category=&ver=01&cu=INR&mid=&msid=&mtid=&enTips=&mg=OFFLINE&qrMedium=04&invoiceNo=&invoiceDate=&QRts=2022-06-05 03:24:28&QRexpire=&Split=&PinCode=&Tier=&txntype=&Consent=&mn=&type=&validitystart=&validityend=&Amrule=&Recur=&Recurvalue=&RecureType=&Rev=&Share=&Block=&Umn=&Skip=&sign=AKadyYMqlMJRHDp6CBlgGPYvLq3mf9CP0uYqCpmJ5X3m8hTRjJgMW076zylZ+eIkdsQ+nSaLEp2Xs6v6N/LSso9YADPpvXIIXvdUukF5YzsKBGt4pwjmLgYSUtedXEEZlJEyRYKheD2rHTJNWgLR0hg6VKLO8tdUgFnFOzy0q66JdZFy"
//            val intent = Intent()
//            intent.action = Intent.ACTION_VIEW
//            intent.data = Uri.parse(UPI)
//            val chooser = Intent.createChooser(intent, "Pay with...")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                upiActivityResult?.launch(chooser)
//            }
//        } catch (e: Exception) {
//            Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
    }

    private fun createInvestment(){
        val authToken = Constants.TOKEN_PREFIX_BEARER + authConfig.getToken()
        val investmentDetails = HashMap<String, Any>()
        investmentDetails["planName"] = arguments?.getString(Constants.INTENT_PLAN).toString()
        investmentDetails["amount"] = arguments?.getString(Constants.INTENT_AMOUNT).toString().toInt()
        investmentDetails["locking"] = Constants.LOCKING_PERIOD
        investmentDetails["interest"] = arguments?.getString(Constants.INTENT_INTEREST).toString().toInt()

        val call = retrofitBuilder.getFinkartService().createInvestment(authToken, investmentDetails)
        call.enqueue(object : Callback<GenericResponse>{
            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                if (response.isSuccessful){
                    val data = response.body()
                    if (data != null){
                        when(data.subCode){
                            Constants.RESPONSE_SUCCESS -> {
                                launchSuccessDialog("You have successfully invested.")
                            }

                            Constants.RESPONSE_BAD_REQUEST -> {
                                // Invalid input
                                Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                                // finish the fragment
                                navController.popBackStack()
                            }
                        }
                    }else {
                        Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                        // finish the fragment
                        navController.popBackStack()
                    }
                }else{
                    Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                    // finish the fragment
                    navController.popBackStack()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                // finish the fragment
                navController.popBackStack()
            }

        })
    }

    private fun launchSuccessDialog(message: String){
        val inflater = LayoutInflater.from(context)
        val bottomSheetBinding = PaymentSuccessAlertViewBinding.inflate(inflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.create()
        bottomSheetDialog.show()

        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setCancelable(false)

        val currentTime = Calendar.getInstance().time
        val sdfD = SimpleDateFormat("dd")
        val sdfM = SimpleDateFormat("MM")
        val sdfY = SimpleDateFormat("yyyy")
        val currentDay = sdfD.format(currentTime).toInt()
        val currentMonth = sdfM.format(currentTime).toInt()
        val currentYear = sdfY.format(currentTime).toInt()

        bottomSheetBinding.tvTotalAmount.text = Constants.RUPEE_SYMBOL_PREFIX + if (totalAmount % 10 > 0) DecimalFormat("##,##,###.##").format(totalAmount) else DecimalFormat("##,##,###").format(totalAmount)
        bottomSheetBinding.tvPaymentDate.text = "$currentDay-$currentMonth-$currentYear"
        bottomSheetBinding.tvMessage.text = message

        bottomSheetBinding.btnDone.setOnClickListener {
            bottomSheetDialog.dismiss()
            navController.popBackStack()

        }
    }

    private fun launchUnSuccessDialog(){
        val inflater = LayoutInflater.from(context)
        val bottomSheetBinding = PaymentUnsuccessAlertViewBinding.inflate(inflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.create()
        bottomSheetDialog.show()


        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.setCancelable(false)

        bottomSheetBinding.btnDone.setOnClickListener {
            bottomSheetDialog.dismiss()
            navController.popBackStack()
//            onBackPressedDispatcher.onBackPressed()
        }
    }
}