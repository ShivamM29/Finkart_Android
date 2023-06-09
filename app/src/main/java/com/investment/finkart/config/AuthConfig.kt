package com.investment.finkart.config

import android.content.Context

class AuthConfig(val context: Context) {
    fun setAuth(token: String, isOnboarded: Boolean){
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("token", token)
        editor.putBoolean("isOnBoarded", isOnboarded)
        editor.apply()
    }

    fun setPhone(phone: String){
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("phone", phone)
        editor.apply()
    }

    fun setProfile(name: String, profileImage: String, occupation: String){
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("name", name)
        editor.putString("profileImage", profileImage)
        editor.putString("occupation", occupation)
        editor.apply()
    }

    fun setIsLogin(isLogin:Boolean){
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("isLogin", isLogin)
        editor.apply()
    }

    fun getToken(): String?{
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        return preferences.getString("token", "")
    }

    fun getName(): String?{
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        return preferences.getString("name", "")
    }

    fun getProfileImage(): String?{
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        return preferences.getString("profileImage", "")
    }

    fun getOccupation(): String?{
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        return preferences.getString("occupation", "")
    }

    fun getIsLogin(): Boolean{
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        return preferences.getBoolean("isLogin", false)
    }

    fun getIsOnBoarded(): Boolean{
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        return preferences.getBoolean("isOnBoarded", false)
    }

    fun getPhone(): String?{
        val preferences = context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        return preferences.getString("phone", "")
    }
}