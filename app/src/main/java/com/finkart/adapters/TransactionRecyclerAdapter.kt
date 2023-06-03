package com.finkart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finkart.R
import com.finkart.databinding.TransactionItemViewBinding
import com.finkart.models.TransactionItems
import com.finkart.utils.Constants
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class TransactionRecyclerAdapter(val context: Context, private val transactionList: List<TransactionItems>): RecyclerView.Adapter<TransactionRecyclerAdapter.TransactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = TransactionItemViewBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val type = transactionList[position].type
        if (type == Constants.TRANSACTION_TYPE_INVESTED){
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.orange))
            holder.binding.tvTransactionStatus.setTextColor(ContextCompat.getColor(context, R.color.orange))
//            holder.binding.ivTransaction.setImageResource(R.drawable.invested)
        }else{
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
            holder.binding.tvTransactionStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
//            holder.binding.ivTransaction.setImageResource(R.drawable.credited)
        }

        holder.binding.tvTransactionStatus.text = transactionList[position].type
        val amount = transactionList[position].amount
        holder.binding.tvAmount.text = Constants.RUPEE_SYMBOL_PREFIX + if ((amount!! % 10) > 0) DecimalFormat("##,##,###.##").format(amount) else DecimalFormat("##,##,###").format(amount)

        val createdAt = transactionList[position].createdAt

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val transactionDate = sdf.format(createdAt)
        holder.binding.tvTransactionDate.text = transactionDate.toString()
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class TransactionViewHolder(val binding: TransactionItemViewBinding): RecyclerView.ViewHolder(binding.root)
}