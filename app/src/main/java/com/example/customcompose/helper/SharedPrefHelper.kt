package com.example.customcompose.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveItem(newItem: String) {
        val existingSet = getSet().toMutableSet()
        if (existingSet.add(newItem)) { // Add only if it's new
            val jsonString = Gson().toJson(existingSet)
            prefs.edit().putString("checkList", jsonString).apply()
        }
    }

    fun getSet(): Set<String> {
        val jsonString = prefs.getString("checkList", "[]") ?: "[]"
        val type = object : TypeToken<Set<String>>() {}.type
        return Gson().fromJson(jsonString, type) ?: emptySet()
    }

    fun removeItem(itemToRemove: String) {
        val existingSet = getSet().toMutableSet()
        if (existingSet.remove(itemToRemove)) { // Remove if it exists
            val jsonString = Gson().toJson(existingSet)
            prefs.edit().putString("checkList", jsonString).apply()
        }
    }

    fun existsItem(item: String): Boolean {
        return getSet().contains(item)
    }
}