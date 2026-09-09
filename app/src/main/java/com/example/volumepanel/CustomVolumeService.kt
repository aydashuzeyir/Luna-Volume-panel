package com.example.volumepanel

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.graphics.PixelFormat
import android.media.AudioManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

class CustomVolumeService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var customVolumeView: View? = null
    private lateinit var audioManager: AudioManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        serviceInfo = serviceInfo.apply {
            flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val action = event.action

        if (action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            val direction = if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, 0)

            showCustomVolumePanel()
            return true 
        }
        return super.onKeyEvent(event)
    }

    private fun showCustomVolumePanel() {
        if (customVolumeView == null) {
            val inflater = LayoutInflater.from(this)
            customVolumeView = inflater.inflate(R.layout.custom_volume_layout, null)

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLATED
            )

            windowManager.addView(customVolumeView, params)
        }
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
