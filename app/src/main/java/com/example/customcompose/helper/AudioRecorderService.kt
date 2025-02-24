package com.example.customcompose.helper

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.example.customcompose.MainActivity
import com.example.customcompose.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorderService : Service() {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    @SuppressLint("NotificationId0")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                0,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(0, notification)
        }

        startRecording()
    }

    private fun startRecording() {
        println("Service Started")
        val cacheDir = cacheDir
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        outputFile = File(cacheDir, "$timestamp.mp3")

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile?.absolutePath)

            try {
                prepare()
                start()
                Log.d("AudioRecorderService", "Recording started: ${outputFile?.absolutePath}")
            } catch (e: Exception) {
                Log.e("AudioRecorderService", "Recording failed: ${e.message}")
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            try {
                stop()
                reset()
                release()
                Log.d("AudioRecorderService", "Recording saved: ${outputFile?.absolutePath}")
            } catch (e: Exception) {
                Log.e("AudioRecorderService", "Error stopping recording: ${e.message}")
            }
        }
        recorder = null
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "AudioRecorderChannel",
                "Audio Recorder",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "AudioRecorderChannel")
                .setContentTitle("Audio Recording")
                .setContentText("Recording in progress...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
}
