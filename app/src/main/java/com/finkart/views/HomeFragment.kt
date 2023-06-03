package com.finkart.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.finkart.MainActivity
import com.finkart.R
import com.finkart.databinding.FragmentHomeBinding
import com.finkart.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    private fun initView(){
        val bundle = Bundle()
        val navController = findNavController()

        binding.cvPlan1.setOnClickListener {
            bundle.putString(Constants.INTENT_PLAN, Constants.PLAN_BASIC)
            navController.navigate(R.id.action_homeFragment_to_investmentFragment, bundle)
        }

        binding.cvPlan2.setOnClickListener {
            bundle.putString(Constants.INTENT_PLAN, Constants.PLAN_Master)
            findNavController().navigate(R.id.action_homeFragment_to_investmentFragment, bundle)
        }

        binding.cvPlan3.setOnClickListener {
            bundle.putString(Constants.INTENT_PLAN, Constants.PLAN_ADVANCE)
            findNavController().navigate(R.id.action_homeFragment_to_investmentFragment, bundle)
        }
    }
}