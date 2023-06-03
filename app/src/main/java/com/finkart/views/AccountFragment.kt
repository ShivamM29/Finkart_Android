package com.finkart.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.finkart.R
import com.finkart.config.AuthConfig
import com.finkart.databinding.FragmentAccountBinding

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
        binding?.tvNominee?.setOnClickListener(this)
        binding?.tvManageBank?.setOnClickListener(this)

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
            R.id.tvNominee -> {
                navController?.navigate(R.id.action_accountFragment_to_nomineeDetailsFragment)
            }
            R.id.tvManageBank -> {
                navController?.navigate(R.id.action_accountFragment_to_bankDetailsFragment)
            }
        }
    }
}