package com.example.customcompose.views.compose.referring.video

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.views.compose.helper_compose.KeepScreenOnEffect

@Composable
fun FullScreenVideoPopup(
    videoUri: Uri,
    blockId: String?,
    groupId: String?,
    destination: String?,
    position: String?,
) {
    KeepScreenOnEffect()

    val context = LocalContext.current
    val activity = context.findActivity()
    println("videoUri_link: ${videoUri.toString()}" )

    BackHandler {
        //Toast.makeText(context, "Back press is disabled during video playback", Toast.LENGTH_SHORT).show()
    }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val window = activity?.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            window?.setDecorFitsSystemWindows(false)
            window?.insetsController?.apply {
                hide(WindowInsets.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Legacy method for older devices
            @Suppress("DEPRECATION")
            window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }

        onDispose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window?.insetsController?.show(WindowInsets.Type.systemBars())
                window?.setDecorFitsSystemWindows(true)
            } else {
                @Suppress("DEPRECATION")
                window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }

            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }


    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                setVideoURI(videoUri)
                setOnCompletionListener {
                    if (destination == "mainSurvey") {
                        if (blockId != null && groupId != null && position != null) {
                            blockListViewModel.addBlockToTheSurveyFlow(
                                blockId,
                                groupId,
                                position.toInt()
                            )

                            println("mainSurvey_jump")

                        }

                    } else {
                        if (blockId != null && groupId != null) {
                            blockListViewModel.addBlockToTheCheckList(
                                blockId,
                                groupId
                            )
                        }
                    }
                    activity?.finish()
                }
                setOnPreparedListener {
                    start()
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    )
}


fun android.content.Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
