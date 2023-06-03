package com.finkart.views

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.finkart.MainActivity
import com.finkart.R
import com.finkart.config.AuthConfig
import com.finkart.databinding.FragmentLoginBinding
import com.finkart.databinding.LoginBottonSheetBinding
import com.finkart.models.LoginData
import com.finkart.network.RetrofitBuilder
import com.finkart.utils.Constants
import com.finkart.utils.FinkartLoading
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginBottomSheetBinding: LoginBottonSheetBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var finkartLoading: FinkartLoading
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var retrofitBuilder: RetrofitBuilder
    private lateinit var authConfig: AuthConfig
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        retrofitBuilder = RetrofitBuilder()
        firebaseAuth = FirebaseAuth.getInstance()
        finkartLoading = FinkartLoading()
        authConfig = AuthConfig(requireContext())
        navController = findNavController()

        binding.btnLogin.setOnClickListener {
            launchBottomSheet()
        }
    }

    private fun launchBottomSheet(){
        val inflater = LayoutInflater.from(context)
        loginBottomSheetBinding = LoginBottonSheetBinding.inflate(inflater)
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog.setContentView(loginBottomSheetBinding.root)
        bottomSheetDialog.create()
        bottomSheetDialog.show()

        loginBottomSheetBinding.btnLoginGetOTP.setOnClickListener {
            if (isValidPhoneInput()){
                // add firebase authentication
                showNextAnimation()
                loginBottomSheetBinding.viewFlipper.showNext()
                firebaseCallback()

                getOtp()
            }
        }

        loginBottomSheetBinding.btnVerify.setOnClickListener{
            if (isValidOTPInput()){
                // add firebase verification
                finkartLoading.showProgress(requireContext())
                val code = loginBottomSheetBinding.etOTP.text.toString()
                val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
                signInWithPhoneAuthCredential(credential)
            }
        }

        loginBottomSheetBinding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val mobileNo = loginBottomSheetBinding.etPhone.text.toString()
                if (mobileNo.length == 10){
                    hideSoftInput()
                }
            }
        })
    }

    private fun firebaseCallback(){
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {}
            override fun onVerificationFailed(e: FirebaseException) {
                finkartLoading.hideProgress()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "Looks like mobile number is  not correct.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken)

                finkartLoading.hideProgress()
                mVerificationId = verificationId
            }
        }
    }

    private fun getOtp() {
        val phone = loginBottomSheetBinding.etPhone.text.toString()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    register()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            requireContext(),
                            "Looks mobile number is  not correct.",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.i("Login", "signInWithPhoneAuthCredential: ${task.exception?.localizedMessage}")
                        finkartLoading.hideProgress()
                    }
                }
            }
    }


    private fun isValidPhoneInput(): Boolean {
        var isValid = true
        val phoneNo = loginBottomSheetBinding.etPhone.text.toString()
        if (phoneNo.isEmpty() && phoneNo.length != 10){
            isValid = false
            loginBottomSheetBinding.etPhone.error = "Please provide valid phone number"
        }
        return isValid
    }

    private fun isValidOTPInput(): Boolean {
        var isValid = true
        val otp = loginBottomSheetBinding.etOTP.text.toString()
        if (otp.isEmpty() && otp.length != 6){
            isValid = false
            loginBottomSheetBinding.etOTP.error = "Please provide valid OTP"
        }
        return isValid
    }

    private fun register(){
        try {
            val phone = loginBottomSheetBinding.etPhone.text.toString()

            val registerDetails = HashMap<String, String>()
            registerDetails["userId"] = firebaseAuth.currentUser?.uid!!
            registerDetails["phone"] = phone

            val call = retrofitBuilder.getFinkartService().register(registerDetails)
            call.enqueue(object : Callback<LoginData>{
                override fun onResponse(call: Call<LoginData>, response: Response<LoginData>) {
                    val data = response.body()
                    if (data != null){
                        if (Constants.RESPONSE_CREATED == data.subCode || Constants.RESPONSE_SUCCESS == data.subCode){
                            val token = data.items?.token
                            if (token != null){
                                authConfig.setAuth(data.items.token, data.items.isOnboarded!!)
                                authConfig.setIsLogin(true)
                                authConfig.setPhone(phone)

                                if (data.items.isOnboarded) {
                                    authConfig.setProfile(data.items.name!!, data.items.profileImage!!, data.items.occupation!!) // if the user is on-boarded then we can get this details otherwise we will not get details
                                    startActivity(Intent(context, MainActivity::class.java))
                                    activity?.finish()
                                }else{
                                    bottomSheetDialog.dismiss()
                                    navController.navigate(R.id.action_loginFragment_to_onBoardFragment)
                                }
                            }else{
                                Toast.makeText(context, Constants.ERROR_MESSAGE_SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                            }
                        }else {
                            Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }

                    finkartLoading.hideProgress()
                }

                override fun onFailure(call: Call<LoginData>, t: Throwable) {
                    finkartLoading.hideProgress()
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }

            })

        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun showNextAnimation(){
        loginBottomSheetBinding.viewFlipper.setInAnimation(context, R.anim.slide_in_right)
        loginBottomSheetBinding.viewFlipper.setOutAnimation(context, R.anim.slide_out_left)
    }

    private fun hideSoftInput(){
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(loginBottomSheetBinding.etPhone.windowToken, 0)
    }
}