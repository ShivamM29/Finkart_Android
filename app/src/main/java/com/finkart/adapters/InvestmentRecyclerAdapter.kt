package com.finkart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finkart.databinding.InvestmentItemViewBinding
import com.finkart.models.InvestmentItems
import com.finkart.utils.Constants
import java.text.DecimalFormat

class InvestmentRecyclerAdapter(val context: Context, private val investmentList: List<InvestmentItems>): RecyclerView.Adapter<InvestmentRecyclerAdapter.MyInvestmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyInvestmentViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = InvestmentItemViewBinding.inflate(inflater, parent, false)
        return MyInvestmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyInvestmentViewHolder, position: Int) {
        val model = investmentList[position]
        val _percent = model.interest
        val percent = if (_percent!! % 10 > 0) DecimalFormat("##.##").format(_percent) else DecimalFormat("##").format(_percent)
        holder.binding.tvInterest.text = "$percent%"

        val _investedAmount = model.amount
        val investedAmount = if (_investedAmount!! % 10 > 0) DecimalFormat("##,##,###.##").format(_investedAmount) else DecimalFormat("##,##,###").format(_investedAmount)
        holder.binding.tvAmountInvested.text = Constants.RUPEE_SYMBOL_PREFIX + investedAmount.toString()

        val _profit = model.profit
        val profit = if (_profit!! % 10 > 0) DecimalFormat("##,##,###.##").format(_profit) else DecimalFormat("##,##,###").format(_profit)
        holder.binding.tvIncome.text = Constants.RUPEE_SYMBOL_PREFIX + profit.toString()

        holder.binding.tvDate.text = model.date
        holder.binding.tvExpiryDate.text = model.expireDate
//        holder.binding.tvPlanName.text = model.planName
    }

    override fun getItemCount(): Int {
        return investmentList.size
    }

    inner class MyInvestmentViewHolder(val binding: InvestmentItemViewBinding): RecyclerView.ViewHolder(binding.root)
}