package com.investment.finkart.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.investment.finkart.R

class FinkartLoading{
    private var mDialog: Dialog? = null

    fun showProgress(context: Context) {
        mDialog = Dialog(context, R.style.FinkartLoading)
        mDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog?.setContentView(R.layout.finkart_loading)
        mDialog?.setCancelable(false)
        mDialog?.setCanceledOnTouchOutside(false)
        mDialog?.show()
    }

    fun hideProgress() {
        if (mDialog != null) {
            mDialog?.dismiss()
            mDialog = null
        }
    }
}