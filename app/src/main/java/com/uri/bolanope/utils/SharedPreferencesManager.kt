package com.uri.bolanope.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREF_NAME = "user_prefs"
    private const val USER_ID_KEY = "USER_ID"

    fun saveUserId(context: Context, userId: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(USER_ID_KEY, userId)
            apply()
        }
    }

    fun getUserId(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ID_KEY, null)
    }

    fun clearUserId(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove(USER_ID_KEY)
            apply()
        }
    }
}