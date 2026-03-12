package com.example.financekeeper.auth.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenStorage(private val context: Context) {
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }


    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[KEY_ACCESS_TOKEN] }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[KEY_REFRESH_TOKEN] }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN]  = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getAccessToken(): String? = accessTokenFlow.first()

    suspend fun getRefreshToken(): String? = refreshTokenFlow.first()

    suspend fun hasTokens(): Boolean =
        getAccessToken() != null && getRefreshToken() != null

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
        }
    }

}