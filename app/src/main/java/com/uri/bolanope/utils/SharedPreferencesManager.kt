package com.uri.bolanope.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREF_NAME = "user_prefs"
    private const val USER_ID_KEY = "USER_ID"
    private const val USER_ROLE_KEY = "USER_ROLE"
    private const val TOKEN = "TOKEN"

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

    fun saveUserRole(context: Context, userRole: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(USER_ROLE_KEY, userRole)
            apply()
        }
    }

    fun getUserRole(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_ROLE_KEY, null)
    }

    fun clearUserRole(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove(USER_ROLE_KEY)
            apply()
        }
    }

    fun saveToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(TOKEN, token)
            apply()
        }
    }

    fun getToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN, null)
    }

    fun clearToken(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove(TOKEN)
            apply()
        }
    }
}