package com.finkart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finkart.R
import com.finkart.databinding.SettlementItemViewBinding
import com.finkart.models.SettlementItems
import com.finkart.utils.Constants
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class SettlementRecyclerAdapter(val context: Context, private val settlementList: List<SettlementItems?>): RecyclerView.Adapter<SettlementRecyclerAdapter.SettlementViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettlementViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = SettlementItemViewBinding.inflate(inflater, parent, false)
        return SettlementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettlementViewHolder, position: Int) {
        val status = settlementList[position]?.status
        if (status == Constants.SETTLEMENT_STATUS_PROCESSING){
            holder.binding.tvTransactionStatus.setTextColor(ContextCompat.getColor(context, R.color.orange))
//            holder.binding.animationView.setAnimation(R.raw.processing)
        }else{
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green))

            holder.binding.tvTransactionStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
//            holder.binding.animationView.setAnimation(R.raw.success)
        }

        holder.binding.tvTransactionStatus.text = settlementList[position]?.status
        val amount = settlementList[position]?.amount
        holder.binding.tvAmount.text = Constants.RUPEE_SYMBOL_PREFIX + if ((amount!! % 10) > 0) DecimalFormat("##,##,###.##").format(amount) else DecimalFormat("##,##,###").format(amount)

        val createdAt = settlementList[position]?.createdAt

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val transactionDate = sdf.format(createdAt)
        holder.binding.tvTransactionDate.text = transactionDate.toString()
    }

    override fun getItemCount(): Int {
        return settlementList.size
    }

    inner class SettlementViewHolder(val binding: SettlementItemViewBinding): RecyclerView.ViewHolder(binding.root)
}