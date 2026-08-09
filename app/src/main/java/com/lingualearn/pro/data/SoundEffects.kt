package com.lingualearn.pro.data

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

object SoundEffects {
    private var tones: ToneGenerator? = null

    private fun generator(): ToneGenerator {
        tones?.let { return it }
        return ToneGenerator(AudioManager.STREAM_MUSIC, 70).also { tones = it }
    }

    fun playCorrect(context: Context, preferences: PreferencesStore) {
        if (!preferences.soundEffects) return
        runCatching {
            generator().startTone(ToneGenerator.TONE_PROP_ACK, 120)
        }
    }

    fun playIncorrect(context: Context, preferences: PreferencesStore) {
        if (!preferences.soundEffects) return
        runCatching {
            generator().startTone(ToneGenerator.TONE_PROP_NACK, 180)
        }
    }

    fun playSuccess(context: Context, preferences: PreferencesStore) {
        if (!preferences.soundEffects) return
        runCatching {
            generator().startTone(ToneGenerator.TONE_CDMA_CONFIRM, 220)
        }
    }

    fun release() {
        runCatching { tones?.release() }
        tones = null
    }
}
