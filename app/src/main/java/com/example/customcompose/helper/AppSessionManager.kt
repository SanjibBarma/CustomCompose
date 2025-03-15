package com.example.customcompose.helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val PREVIOUS_GROUP = "PREVIOUS_GROUP";
    private val PRIMARY_BRAND = "PRIMARY_BRAND";
    private val CHECK_LIST = "CHECK_LIST";
    private val USERNAME = "USERNAME"
    private val PASSWORD = "PASSWORD"
    private val REMEMBER_ME = "REMEMBER_ME"
    private val USER_TOKEN = "USER_TOKEN"
    private val USER_IS_LOGIN = "USER_IS_LOGIN"
    private val USER_NAME = "USER_NAME"
    private val AGENCY_NAME = "AGENCY_NAME"
    private val ORG_NAME = "ORG_NAME"
    private val USER_ID = "USER_ID"
    private val USER_EXPIRESIN = "USER_EXPIRESIN"
    private val USER_DESIGNATION = "USER_DESIGNATION"
    private val LOCATION = "LOCATION"
    private val KEY_FIREBASETOKEN = "KEY_FIREBASETOKEN"
    private val USER_IMAGE = "USER_IMAGE"
    private val LOGIN_TIME = "LOGIN_TIME"
    private val ID = "ID"
    private val USER_TYPE = "USER_TYPE"
    private val RESET_STATUS = "RESET_STATUS"
    private val CAMPAIGN_ID = "CAMPAIGN_ID"
    private val MOBILE_VERIFICATION_DATA = "MOBILE_VERIFICATION_DATA"
    private val EXTRA_SERVICE_VAMP_INFO = "EXTRA_SERVICE_VAMP_INFO"

    fun saveItem(newItem: String) {
        val existingSet = getCheckListSet().toMutableSet()
        if (existingSet.add(newItem)) { // Add only if it's new
            val jsonString = Gson().toJson(existingSet)
            prefs.edit().putString(CHECK_LIST, jsonString).apply()
        }
    }

    fun getCheckListSet(): Set<String> {
        val jsonString = prefs.getString(CHECK_LIST, "[]") ?: "[]"
        val type = object : TypeToken<Set<String>>() {}.type
        return Gson().fromJson(jsonString, type) ?: emptySet()
    }

    fun existsItem(item: String): Boolean {
        return getCheckListSet().contains(item)
    }

    fun removeItem(itemToRemove: String) {
        val existingSet = getCheckListSet().toMutableSet()
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

    fun createMerchantLoginSession(agencyName: String?, orgName: String?, designation: String?, Location: String?, user_image: String?, crrTime: String?){
        prefs.edit().putString(USER_IS_LOGIN, true.toString()).apply()
//        prefs.edit().putString(USER_NAME, userName).apply()
        prefs.edit().putString(AGENCY_NAME, agencyName).apply()
        prefs.edit().putString(ORG_NAME, orgName).apply()
        prefs.edit().putString(USER_DESIGNATION, designation).apply()
        prefs.edit().putString(LOCATION, Location).apply()
        prefs.edit().putString(USER_IMAGE, user_image).apply()
        prefs.edit().putString(LOGIN_TIME, crrTime).apply()
    }

    fun getUserDetails(): Map<String, Any?> {
        return mapOf(
            "isUserLoggedIn" to prefs.getString(USER_IS_LOGIN, "false").toBoolean(),
//            "userName" to prefs.getString(USER_NAME, null),
            "agencyName" to prefs.getString(AGENCY_NAME, null),
            "orgName" to prefs.getString(ORG_NAME, null),
            "userDesignation" to prefs.getString(USER_DESIGNATION, null),
            "location" to prefs.getString(LOCATION, null),
            "userImage" to prefs.getString(USER_IMAGE, null),
            "loginTime" to prefs.getString(LOGIN_TIME, null),
        )
    }


    fun setSessionToken(value: String) {
        prefs.edit().putString(USER_TOKEN, value).apply()
    }

    fun getSessionToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun setBrId(value: String) {
        prefs.edit().putString(USER_ID, value).apply()
    }

    fun getBrId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun setCampaignId(value: String) {
        prefs.edit().putString(CAMPAIGN_ID, value).apply()
    }

    fun getCampaignId(): String? {
        return prefs.getString(CAMPAIGN_ID, null)
    }

    fun setMobileVerificationData(value: String) {
        prefs.edit().putString(MOBILE_VERIFICATION_DATA, value).apply()
    }

    fun getMobileVerificationData(): String? {
        return prefs.getString(MOBILE_VERIFICATION_DATA, null)
    }

    fun setExtraServiceCamInfo(camInfo: String) {
        prefs.edit().putString(EXTRA_SERVICE_VAMP_INFO, camInfo).apply()
    }

    fun getExtraServiceCamInfo(): String? {
        return prefs.getString(EXTRA_SERVICE_VAMP_INFO, null)
    }

}