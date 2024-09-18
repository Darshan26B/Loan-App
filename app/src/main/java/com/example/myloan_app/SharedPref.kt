package com.example.myloan_app

import android.content.Context
class SharedPref(context: Context) {

    private val PREFS_NAME = "MYData"

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    fun saveData(key:String,value:String) {
        val editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}