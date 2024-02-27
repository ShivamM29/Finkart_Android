package com.investment.finkart.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.investment.finkart.LauncherActivity
import com.investment.finkart.config.AuthConfig

class CommonLogout(val context: Context) {
    fun logout(authConfig: AuthConfig){
        FirebaseAuth.getInstance().signOut()
        authConfig.setIsLogin(false)

        val intent = Intent(context, LauncherActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        (context as Activity).startActivity(intent)
    }
}