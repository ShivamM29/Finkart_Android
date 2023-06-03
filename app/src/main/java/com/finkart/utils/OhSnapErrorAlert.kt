package com.finkart.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.finkart.R
import com.google.android.material.button.MaterialButton
import javax.inject.Inject

class OhSnapErrorAlert @Inject constructor() {
    private var ohSnapErrorAlert: OhSnapErrorAlert? = null
    private var mDialog: Dialog? = null

    fun getInstance(): OhSnapErrorAlert? {
        if (ohSnapErrorAlert == null) {
            ohSnapErrorAlert = OhSnapErrorAlert()
        }
        return ohSnapErrorAlert
    }

    fun showAlert(context: Context, notice: String?, image: Int) {
        mDialog = Dialog(context)
//        context.setTheme(R.style.AlertStyle)
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog?.setContentView(R.layout.dialog_alert_oh_snap)
        mDialog?.setCanceledOnTouchOutside(false)
        val btnDismiss: MaterialButton = mDialog?.findViewById(R.id.btnDismiss)!!
        val tvNotice: TextView = mDialog?.findViewById(R.id.tvNotice)!!
        val ivError: ImageView = mDialog?.findViewById(R.id.ivErrorImage)!!
        ivError.setImageResource(image)

        tvNotice.text = notice
        btnDismiss.setOnClickListener {
            hideAlert()
            (context as Activity).finish()
        }
        mDialog?.show()
    }

    private fun hideAlert() {
        if (mDialog != null) {
            mDialog?.dismiss()
            mDialog = null
        }
    }
}