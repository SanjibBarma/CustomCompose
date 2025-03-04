package com.example.customcompose.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val PREVIOUS_GROUP = "PREVIOUS_GROUP";
    private val PRIMARY_BRAND = "PRIMARY_BRAND";
    private val CHECK_LIST = "CHECK_LIST";
    private val USERNAME = "USERNAME"
    private val PASSWORD = "PASSWORD"
    private val REMEMBER_ME = "REMEMBER_ME"
    private val SESSION_TOKEN = "SESSION_TOKEN"

    fun saveItem(newItem: String) {
        val existingSet = getSet().toMutableSet()
        if (existingSet.add(newItem)) { // Add only if it's new
            val jsonString = Gson().toJson(existingSet)
            prefs.edit().putString(CHECK_LIST, jsonString).apply()
        }
    }

    fun getSet(): Set<String> {
        val jsonString = prefs.getString(CHECK_LIST, "[]") ?: "[]"
        val type = object : TypeToken<Set<String>>() {}.type
        return Gson().fromJson(jsonString, type) ?: emptySet()
    }

    fun existsItem(item: String): Boolean {
        return getSet().contains(item)
    }

    fun removeItem(itemToRemove: String) {
        val existingSet = getSet().toMutableSet()
        if (existingSet.remove(itemToRemove)) { // Remove if it exists
            val jsonString = Gson().toJson(existingSet)
            prefs.edit().putString(CHECK_LIST, jsonString).apply()
        }
    }

    fun clearCheckList() {
        prefs.edit().remove(CHECK_LIST).apply()
    }

    fun savePreviousGroupId(value: String) {
        prefs.edit().putString(PREVIOUS_GROUP, value).apply()
    }

    fun getPreviousGroupId(): String? {
        return prefs.getString(PREVIOUS_GROUP, null)
    }

    fun setPrimaryBrandName(value: String) {
        prefs.edit().putString(PRIMARY_BRAND, value).apply()
    }

    fun getPrimaryBrandName(): String? {
        return prefs.getString(PRIMARY_BRAND, null)
    }

    fun saveLoginData(username: String, password: String) {
        prefs.edit().putString(USERNAME, username).apply()
        prefs.edit().putString(PASSWORD, password).apply()
        prefs.edit().putBoolean(REMEMBER_ME, true).apply()
    }

    fun getUsername(): String? = prefs.getString(USERNAME, null)
    fun getPassword(): String? = prefs.getString(PASSWORD, null)
    fun isRemembered(): Boolean = prefs.getBoolean(REMEMBER_ME, false)

    fun clearLoginData() {
        prefs.edit().remove(USERNAME).apply()
        prefs.edit().remove(PASSWORD).apply()
        prefs.edit().putBoolean(REMEMBER_ME, false).apply()
    }

    fun setRemembered(value: Boolean) {
        prefs.edit().putBoolean(REMEMBER_ME, value).apply()
    }

    fun setSessionToken(value: String) {
        prefs.edit().putString(SESSION_TOKEN, value).apply()
    }

    fun getSessionToken(): String? {
        return prefs.getString(SESSION_TOKEN, null)
    }
}