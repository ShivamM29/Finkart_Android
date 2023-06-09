package com.investment.finkart.views

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.investment.finkart.LauncherActivity
import com.investment.finkart.R
import com.investment.finkart.databinding.FragmentAccountBinding
import com.investment.finkart.databinding.HelpAndSupportDialogBinding
import com.investment.finkart.databinding.LogoutAlertDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.investment.finkart.config.AuthConfig
import com.investment.finkart.utils.Constants

class AccountFragment : Fragment(), OnClickListener {
    private var binding: FragmentAccountBinding? = null
    private var navController: NavController? = null
    private lateinit var authConfig: AuthConfig

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        initView()
        return binding?.root
    }

    private fun initView() {
        navController = findNavController()
        authConfig = AuthConfig(requireContext())

        binding?.tvWallet?.setOnClickListener(this)
        binding?.tvMyInvestment?.setOnClickListener(this)
        binding?.tvEkyc?.setOnClickListener(this)
        binding?.tvManageBank?.setOnClickListener(this)
        binding?.tvHelpAndSupport?.setOnClickListener(this)
        binding?.tvTermsConditions?.setOnClickListener(this)
        binding?.tvPrivacyPolicy?.setOnClickListener(this)
        binding?.tvRateUs?.setOnClickListener(this)
        binding?.tvLogOut?.setOnClickListener(this)

        try {
            Glide.with(this)
                .load(authConfig.getProfileImage())
                .into(binding?.ivProfile!!)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.tvWallet -> {
                navController?.navigate(R.id.action_accountFragment_to_walletFragment)
            }
            R.id.tvMyInvestment -> {
                navController?.navigate(R.id.action_accountFragment_to_myInvestmentFragment)
            }
            R.id.tvEkyc -> {
                navController?.navigate(R.id.action_accountFragment_to_kycDetailsFragment)
            }
            R.id.tvManageBank -> {
                navController?.navigate(R.id.action_accountFragment_to_bankDetailsFragment)
            }
            R.id.tvHelpAndSupport -> {
                launchSupportDialog()
            }
            R.id.tvTermsConditions -> {
                val bundle = Bundle()
                bundle.putString(Constants.INTENT_FROM, Constants.INTENT_FROM_TERMS)
                navController?.navigate(R.id.action_accountFragment_to_policiesFragment, bundle)
            }
            R.id.tvPrivacyPolicy -> {
                val bundle = Bundle()
                bundle.putString(Constants.INTENT_FROM, Constants.INTENT_FROM_PRIVACY)
                navController?.navigate(R.id.action_accountFragment_to_policiesFragment, bundle)
            }
            R.id.tvRateUs -> {
                rateMe()
            }
            R.id.tvLogOut -> {
                launchLogoutDialog()
            }
        }
    }

    private fun launchLogoutDialog(){
        val inflater = LayoutInflater.from(context)
        val logoutBinding = LogoutAlertDialogBinding.inflate(inflater)
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setView(logoutBinding.root)
        alertDialog.show()

        logoutBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        logoutBinding.btnProceed.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            authConfig.setIsLogin(false)

            val intent = Intent(context, com.investment.finkart.LauncherActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        alertDialog.setOnCancelListener {
            alertDialog.setView(null)
        }
    }

    private fun rateMe(){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context?.packageName)))
        }catch (e: ActivityNotFoundException){
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context?.packageName)));
        }
    }

    private fun launchSupportDialog(){
        val inflater = LayoutInflater.from(context)
        val bottomSheetBinding = HelpAndSupportDialogBinding.inflate(inflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.create()
        bottomSheetDialog.show()
    }
}