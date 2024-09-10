package com.uri.bolanope

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

suspend fun saveDataToDataStore(context: Context, key: String, value: String) {
    val dataStoreKey = stringPreferencesKey(key)
    context.dataStore.edit { preferences ->
        preferences[dataStoreKey] = value
    }
}

fun getDataFromDataStore(context: Context, key: String): Flow<Any?> {
    val dataStoreKey = stringPreferencesKey(key)
    return context.dataStore.data
        .map { preferences ->
            preferences[dataStoreKey] ?: ""
        }
}

