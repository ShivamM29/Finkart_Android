package com.finkart.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.finkart.R
import com.finkart.adapters.TransactionPagerAdapter
import com.finkart.adapters.TransactionRecyclerAdapter
import com.finkart.config.AuthConfig
import com.finkart.databinding.FragmentSettlementBinding
import com.finkart.databinding.FragmentTransactionBinding
import com.finkart.models.TransactionItems
import com.finkart.network.RetrofitBuilder
import com.finkart.utils.Constants

class TransactionFragment : Fragment() {
    private lateinit var binding: FragmentTransactionBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> showGeneral()
                    1 -> showSettlements()
                }
            }
        })

        val pagerAdapter: FragmentStateAdapter = TransactionPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.viewPager.isSaveEnabled = false               // this will give view pager if the fragment state should be saved or not
        binding.viewPager.adapter = pagerAdapter

        binding.tvGeneral.setOnClickListener {
            showGeneral()
            binding.viewPager.currentItem = 0
        }

        binding.tvSettlement.setOnClickListener {
            showSettlements()
            binding.viewPager.currentItem = 1
        }
    }

    private fun showGeneral() {
        binding.tvGeneral.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.tvGeneral.background = ContextCompat.getDrawable(requireContext(), R.color.blue)

        binding.tvSettlement.setTextColor(ContextCompat.getColor(requireContext() , R.color.black))
        binding.tvSettlement.background = null
    }

    private fun showSettlements(){
        binding.tvSettlement.setTextColor(ContextCompat.getColor(requireContext() , R.color.white))
        binding.tvSettlement.background = ContextCompat.getDrawable(requireContext(), R.color.blue)

        binding.tvGeneral.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.tvGeneral.background = null
    }
}