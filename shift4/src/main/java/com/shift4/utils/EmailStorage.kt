package com.shift4.utils

import android.content.Context
import android.content.SharedPreferences

internal class EmailStorage(private val context: Context) {
    object Constants {
        const val EMAIL_STORAGE_KEY = "EMAIL_STORAGE_KEY"
        const val LAST_KEY = "LAST_KEY"
    }

    private val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences(
            Constants.EMAIL_STORAGE_KEY,
            0
        )

    var lastEmail: String?
        get() = sharedPreferences.getString(Constants.LAST_KEY, null)
        set(newValue) {
            with(sharedPreferences.edit()) {
                putString(Constants.LAST_KEY, newValue)
                apply()
            }
        }

    fun cleanSavedEmails() {
        lastEmail = null
    }
}