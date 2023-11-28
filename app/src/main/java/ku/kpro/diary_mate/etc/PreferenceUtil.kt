package com.renju_note.isoo.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context : Context) {

    private val prefs : SharedPreferences = context.getSharedPreferences("storage_pk", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    fun setBoolean(key: String, str: Boolean) {
        prefs.edit().putBoolean(key, str).apply()
    }

    fun setInt(key: String, str: Int) {
        prefs.edit().putInt(key, str).apply()
    }

}