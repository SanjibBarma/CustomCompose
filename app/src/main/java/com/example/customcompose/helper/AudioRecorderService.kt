package com.example.customcompose.helper

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.customcompose.MainActivity
import com.example.customcompose.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorderService : Service() {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(1, notification)
        }

        val fileName = intent?.getStringExtra("AUDIO_FILE_NAME") ?: "${System.currentTimeMillis()}.mp3"
        startRecording(fileName)
//        startRecording()
        return START_STICKY
    }

//    private fun startRecording() {
//        println("Service Started")
//        val cacheDir = cacheDir
//        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        outputFile = File(cacheDir, "$timestamp.mp3")
//
//        recorder = MediaRecorder().apply {
//            setAudioSource(MediaRecorder.AudioSource.MIC)
//            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//            setOutputFile(outputFile?.absolutePath)
//
//            try {
//                prepare()
//                start()
//                Log.d("AudioRecorderService", "Recording started: ${outputFile?.absolutePath}")
//            } catch (e: Exception) {
//                Log.e("AudioRecorderService", "Recording failed: ${e.message}")
//            }
//        }
//    }

    private fun startRecording(fileName: String) {
        println("Service Started")
        val cacheDir = cacheDir
        outputFile = File(cacheDir, fileName)

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

    override fun onDestroy() {
        stopRecording()
        stopForeground(true)
        super.onDestroy()
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

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "AudioRecorderChannel",
                "Audio Recorder",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
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
            NotificationCompat.Builder(this)
                .setContentTitle("Audio Recording")
                .setContentText("Recording in progress...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()
        }
    }
}

