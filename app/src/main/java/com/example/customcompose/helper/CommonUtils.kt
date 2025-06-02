package com.example.customcompose.helper

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.helper.Constants.surveyBasicInfo
import com.example.customcompose.model.Block
import com.example.customcompose.model.Result
import com.example.customcompose.model.TapAnalysisModel
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger
import java.net.Inet4Address
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.TimeZone

object CommonUtils {
    fun generateOtp(): String {
        val rnd = Random()
        val letters = ('a'..'z').toList()

        // Generate 2 random letters
        val randomLtr1 = letters[rnd.nextInt(letters.size)]
        val randomLtr2 = letters[rnd.nextInt(letters.size)]

        // Generate a 4-digit number
        val number = rnd.nextInt(9999)
        val formattedNumber = String.format(Locale.ENGLISH, "%04d", number)

        // Concatenate the result
        return "$randomLtr1$randomLtr2$formattedNumber"
    }

    fun rotateImage(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun createImageFile(context: Context): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"
        val storageDir: File? = context.cacheDir
        return File(storageDir, fileName)
    }

    fun saveCapturedImageToCache(context: Context, uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val fileName = uri.lastPathSegment ?: "captured_image.jpg"
        val file = File(context.cacheDir, fileName)

        try {
            FileOutputStream(file).use { out ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun rotateImageIfRequired(context: Context, uri: Uri, file: File) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = inputStream?.use { ExifInterface(it) }
            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }

            FileOutputStream(file).use { out ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getVideoPathFromCache(context: Context, fileName: String): String? {
        val cacheDir = context.cacheDir
        val videoFile = File(cacheDir, fileName)

        return if (videoFile.exists()) videoFile.absolutePath else null
    }

    fun createVideoThumbnail(videoPath: String): Bitmap? {
        return try {
            ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND)
        } catch (ex: Exception) {
            Log.e("Thumbnail", "Error creating thumbnail for: $videoPath", ex)
            null
        }
    }

    fun getAppVersionCode(context: Context): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace().toString()
        }
    }

    @SuppressLint("HardwareIds")
    fun getDeviceInfo(context: Context): HashMap<String, Any> {
        val deviceInfoMap = HashMap<String, Any>()

        deviceInfoMap.apply {
            put("device_id", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
            put("security_patch", if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Build.VERSION.SECURITY_PATCH
            } else {
                "Unknown"
            })
            put("ui_version", getSystemProperty("ro.build.version.incremental") ?: "Unknown")
            put("android_version", Build.VERSION.RELEASE)
            put("ip_address", getIPAddress())
            put("api_version", Build.VERSION.SDK_INT)
            put("manufacture", Build.MANUFACTURER)
            put("user_type", getSystemProperty("ro.build.type") ?: "Unknown")
            put("imei", "")
            put("model", Build.MODEL)
            put("network_type", getNetworkType(context))
            put("mobile_number", "")
            put("brand", Build.BRAND)

            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName?.let {
                    put("app_version", it)
                }
                put("app_version_code", packageInfo.versionCode)

                packageInfo.versionCode

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace().toString()
            }
        }

        return deviceInfoMap
    }

    private fun getSystemProperty(propName: String): String? {
        return try {
            val process = Runtime.getRuntime().exec("getprop $propName")
            process.inputStream.bufferedReader().use { it.readLine() }
        } catch (e: Exception) {
            null
        }
    }

    private fun getIPAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in interfaces) {
                val addresses = networkInterface.inetAddresses
                for (address in addresses) {
                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.hostAddress ?: "Unknown"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Unknown"
    }

    private fun getNetworkType(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return "No Connection"
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "No Connection"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobile"
            else -> "unknown"
        }
    }

    fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className == serviceClass.name) {
                return true
            }
        }
        return false
    }

    fun getContactDate(date: String?): String {
        val outputPattern = "yyyy-MM-dd HH:mm:ss"
        var str = ""

        //   date = inputFormat.parse(time.substring(0,19));
        var spf = SimpleDateFormat(outputPattern)
        var newDate: Date? = null
        try {
            newDate = spf.parse(date)
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
        spf = SimpleDateFormat("yyyy-MM-dd")
        str = spf.format(newDate)

        return str
    }

    fun getdatetime(): String {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        val sdt = df.format(Date(System.currentTimeMillis()))
        Log.d("getdatetie", sdt)
        return sdt
    }

    fun calculateMD5(updateFile: File?): String? {
        val digest: MessageDigest
        try {
            digest = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            return null
        }

        val `is`: InputStream
        try {
            `is` = FileInputStream(updateFile)
        } catch (e: FileNotFoundException) {
            return null
        }

        val buffer = ByteArray(8192)
        var read: Int
        try {
            while ((`is`.read(buffer).also { read = it }) > 0) {
                digest.update(buffer, 0, read)
            }
            val md5sum = digest.digest()
            val bigInt = BigInteger(1, md5sum)
            var output = bigInt.toString(16)
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0')
            return output
        } catch (e: IOException) {
            throw RuntimeException("Unable to process file for MD5", e)
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {

            }
        }
    }

    fun getTapDateTime(startTime: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Dhaka")
        return sdf.format(Date(System.currentTimeMillis() + (startTime.toLong() - System.nanoTime()) / 1000000))
    }

    fun saveTapAnalysisData(block: Block, result: List<Result?>, question: String) {
        if (block.id != null) {
            val distinctResults = result.filterNotNull().distinctBy { it.option }

            val tapData = appSessionManager.getStartTimeTapAnalysis()?.let {
                getTapDateTime(it)
            }?.let {
                TapAnalysisModel(
                    block_id = block.id,
                    question = question,
                    result = distinctResults,
                    survey_start_time = it
                )
            }

            println("saveTapAnalysisData: $tapData")

            if (tapData != null) {
                appSessionManager.saveTapAnalysis(tapData)
            }

            if (appSessionManager.getTapAnalysisList().isNotEmpty()) {
                surveyBasicInfo["tap_analysis"] = appSessionManager.getTapAnalysisList()
            }
        }
    }

    fun getTapAnalysisElapsedTime(): Long? {
        val startTimeStr = appSessionManager.getStartTimeTapAnalysis()
        return if (!startTimeStr.isNullOrEmpty()) {
            val startTime = startTimeStr.toLongOrNull()
            startTime?.let {
                System.nanoTime() - it
            }
        } else {
            null
        }
    }


}