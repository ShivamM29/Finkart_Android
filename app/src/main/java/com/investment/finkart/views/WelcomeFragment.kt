package com.investment.finkart.views

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.investment.finkart.R
import com.investment.finkart.databinding.FragmentWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.investment.finkart.config.AuthConfig

class WelcomeFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var navController: NavController
    private lateinit var authConfig: AuthConfig
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        navController = findNavController()
        initView()
        return binding.root
    }

    private fun initView() {
        authConfig = AuthConfig(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnNext1.setOnClickListener(this)
        binding.btnNext2.setOnClickListener (this)
        binding.btnNext3.setOnClickListener (this)

        binding.ibBack1.setOnClickListener(this)
        binding.ibBack2.setOnClickListener(this)

        binding.skip1.setOnClickListener(this)
        binding.skip2.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnNext_1,
            R.id.btnNext_2 -> {
                showNextAnimation()
                binding.viewFlipper.showNext()
            }
            R.id.ibBack_1,
            R.id.ibBack_2 -> {
                showPreviousAnimation()
                binding.viewFlipper.showPrevious()
            }
            R.id.skip_1,
            R.id.skip_2,
            R.id.btnNext_3 -> {
                // move to another fragment
                if (firebaseAuth.currentUser != null) {
                    if (authConfig.getIsOnBoarded()){
                        moveToMain()
                    }else{
                        moveToOnBoard()
                    }
                }else{
                    moveToLogin()
                }
            }

        }
    }

    private fun showNextAnimation(){
        binding.viewFlipper.setInAnimation(context, R.anim.slide_in_right)
        binding.viewFlipper.setOutAnimation(context, R.anim.slide_out_left)
    }

    private fun showPreviousAnimation(){
        binding.viewFlipper.setInAnimation(context, R.anim.slide_in_left)
        binding.viewFlipper.setOutAnimation(context, R.anim.slide_out_right)
    }

    private fun moveToLogin(){
        navController.navigate(R.id.action_welcomeFragment_to_loginFragment)
    }

    private fun moveToOnBoard(){
        navController.navigate(R.id.action_welcomeFragment_to_onBoardFragment)
    }

    private fun moveToMain(){
        val intent = Intent(context, com.investment.finkart.MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

}