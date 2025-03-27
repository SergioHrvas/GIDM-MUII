package com.pandemiagame.org.data.remote.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "SecurePrefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String){
        sharedPreferences.edit().putString("API_TOKEN", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("API_TOKEN", null)
    }

    fun clearToken(){
        sharedPreferences.edit().remove("API_TOKEN").apply()
    }
}