package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import java.util.Locale

class WorkoutCoach(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (_: Exception) {}
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let {
                val result = it.setLanguage(Locale.US)
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
                it.setSpeechRate(1.05f)
                it.setPitch(1.0f)
            }
        }
    }

    fun speak(text: String, isVoiceEnabled: Boolean) {
        if (!isVoiceEnabled || !isTtsReady) return
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "workout_coach_utterance")
        } catch (_: Exception) {}
    }

    fun tick(isSoundEnabled: Boolean) {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        } catch (_: Exception) {}
    }

    fun go(isSoundEnabled: Boolean) {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
        } catch (_: Exception) {}
    }

    fun finishBeep(isSoundEnabled: Boolean) {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 400)
        } catch (_: Exception) {}
    }

    fun vibrate(pattern: LongArray, isVibrationEnabled: Boolean) {
        if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (_: Exception) {}
    }

    fun vibrateShort(isVibrationEnabled: Boolean) {
        vibrate(longArrayOf(0, 120), isVibrationEnabled)
    }

    fun vibrateLong(isVibrationEnabled: Boolean) {
        vibrate(longArrayOf(0, 250, 100, 250), isVibrationEnabled)
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            toneGenerator?.release()
        } catch (_: Exception) {}
    }
}
