package com.investment.finkart.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.investment.finkart.R
import com.investment.finkart.databinding.FragmentInvestmentBinding
import com.investment.finkart.utils.Constants
import java.text.DecimalFormat

class InvestmentFragment : Fragment() {
    private lateinit var binding: FragmentInvestmentBinding
    private var annualPlanDetails = HashMap<String, Int>()
    private var monthlyPlanDetails = HashMap<String, Float>()
    private var planName = ""
    private var percentAnnual: Int? = null
    private var percentMonthly: Float? = null
    private var startPrice: Int? = null
    private var endPrice: Int? = null
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentInvestmentBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        navController = findNavController()

        planName = arguments?.getString(Constants.INTENT_PLAN)!!
        binding.tvPlanName.text = planName

        annualPlanDetails[Constants.PLAN_BASIC] = Constants.BASIC_PLAN_PERCENT_ANNUAL
        annualPlanDetails[Constants.PLAN_Master] = Constants.MASTER_PLAN_PERCENT_ANNUAL
        annualPlanDetails[Constants.PLAN_ADVANCE] = Constants.ADVANCE_PLAN_PERCENT_ANNUAL

        monthlyPlanDetails[Constants.PLAN_BASIC] = Constants.BASIC_PLAN_PERCENT_MONTHLY.toFloat()
        monthlyPlanDetails[Constants.PLAN_Master] = Constants.MASTER_PLAN_PERCENT_MONTHLY.toFloat()
        monthlyPlanDetails[Constants.PLAN_ADVANCE] = Constants.ADVANCE_PLAN_PERCENT_MONTHLY.toFloat()

        percentAnnual = annualPlanDetails[planName]
        percentMonthly = monthlyPlanDetails[planName]

        checkRange()

        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.etAmount.removeTextChangedListener(this);
                try {
                    val amount = binding.etAmount.text.toString()
                    var investAmount = 0.0

                    if (!amount.contains("""[0-9]""".toRegex())){
                        binding.etAmount.text = Editable.Factory().newEditable("")
                        binding.etAmount.setSelection(0)
                        calculateProfit(0.0)
                    }else{
                        investAmount = amount.replace(Regex("""[₹, ]"""), "").toDouble()
                        val formatAmount = Constants.RUPEE_SYMBOL_PREFIX + DecimalFormat("##,##,###").format(investAmount)

                        binding.etAmount.text = Editable.Factory().newEditable(formatAmount)
                        binding.etAmount.setSelection(formatAmount.length)

                        calculateProfit(investAmount)
                    }
                    if (investAmount < startPrice!! || investAmount > endPrice!!){
                        binding.btnPayNow.isEnabled = false
                        binding.tvError.visibility = View.VISIBLE
                        val error = "Should be in Range (₹ ${DecimalFormat("##,##,###").format(startPrice)} ~ ₹ ${DecimalFormat("##,##,###").format(endPrice)})"
                        binding.tvError.text = error
                    }else{
                        binding.tvError.visibility = View.INVISIBLE
                        binding.btnPayNow.isEnabled = true
                    }
                    binding.etAmount.addTextChangedListener(this)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        })

        binding.tvRecFirst.setOnClickListener {
            setRecommendedAmount(binding.tvRecFirst.text.toString())
        }

        binding.tvRecSecond.setOnClickListener {
            setRecommendedAmount(binding.tvRecSecond.text.toString())
        }

        binding.tvRecThird.setOnClickListener {
            setRecommendedAmount(binding.tvRecThird.text.toString())
        }

        binding.ibBack.setOnClickListener {
            navController.popBackStack()
        }

        binding.btnPayNow.setOnClickListener {
            val bundle = Bundle()
            val navBuilder = NavOptions.Builder()
            val navOptions: NavOptions = navBuilder.setPopUpTo(R.id.homeFragment, false).build()
            val amount = binding.etAmount.text.replace(Regex("""[₹, ]"""), "")

            bundle.putString(Constants.INTENT_PLAN, planName)
            bundle.putString(Constants.INTENT_AMOUNT, amount)
            bundle.putString(Constants.INTENT_INTEREST, percentAnnual.toString())
//            navController.navigate(R.id.action_investmentFragment_to_paymentFragment, bundle, navOptions)
        }
    }

    private fun setRecommendedAmount(amountStr: String){
        val amount = amountStr.replace(Regex("""[₹, ]"""), "")
        binding.etAmount.setText(amount)
    }

    private fun calculateProfit(amount: Double){
        val annualProfit = (amount*percentAnnual!!)/100
        binding.tvAnnualIncome.text = Constants.RUPEE_SYMBOL_PREFIX + if ((annualProfit % 10) > 0) DecimalFormat("##,##,###.##").format(annualProfit) else DecimalFormat("##,##,###").format(annualProfit)
        binding.tvMonthlyIncome.text = Constants.RUPEE_SYMBOL_PREFIX + if (((annualProfit/12) % 10) > 0) DecimalFormat("##,##,###.##").format(annualProfit/12) else DecimalFormat("##,##,###").format(annualProfit/12)
        binding.tvAnnualPercent.text = "@ $percentAnnual%"
        binding.tvMonthlyPercent.text = "@ $percentMonthly%"
    }

    private fun checkRange(){
        when(planName){
            Constants.PLAN_BASIC -> {
                startPrice = Constants.PLAN_BASIC_START_PRICE
                endPrice = Constants.PLAN_BASIC_END_PRICE
                binding.tvRecFirst.text = "₹ 5,000"
                binding.tvRecSecond.text = "₹ 8,000"
                binding.tvRecThird.text = "₹ 12,000"
            }

            Constants.PLAN_Master -> {
                startPrice = Constants.PLAN_MASTER_START_PRICE
                endPrice = Constants.PLAN_MASTER_END_PRICE

                binding.tvRecFirst.text = "₹ 15,000"
                binding.tvRecSecond.text = "₹ 20,000"
                binding.tvRecThird.text = "₹ 25,000"
            }

            Constants.PLAN_ADVANCE -> {
                startPrice = Constants.PLAN_ADVANCE_START_PRICE
                endPrice = Constants.PLAN_ADVANCE_END_PRICE

                binding.tvRecFirst.text = "₹ 30,000"
                binding.tvRecSecond.text = "₹ 40,000"
                binding.tvRecThird.text = "₹ 60,000"
            }
        }

        binding.tvTitleRange.text = "₹ ${DecimalFormat("##,##,###").format(startPrice)}  ~  ₹ ${DecimalFormat("##,##,###").format(endPrice)}"
    }
}