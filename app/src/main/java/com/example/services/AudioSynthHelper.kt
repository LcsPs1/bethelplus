package com.example.services

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

object AudioSynthHelper {

    private var ambientJob: Job? = null
    private var isPlayingAmbient = false
    private const val SAMPLE_RATE = 22050

    /**
     * Plays a pleasant, meditative sanctuary chime bell (soft bell chime at 528Hz and 1056Hz harmonic with exponential decay)
     */
    fun playSanctuaryBell(scope: CoroutineScope, onFinish: (() -> Unit)? = null) {
        scope.launch(Dispatchers.Default) {
            try {
                val durationSec = 3.5
                val numSamples = (durationSec * SAMPLE_RATE).toInt()
                val buffer = ShortArray(numSamples)

                val freq1 = 528.0 // Solfeggio frequency (transformation / miracles)
                val freq2 = 1056.0 // Harmonic octave
                val freq3 = 1320.0 // Pure fifth

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val decay = exp(-1.4 * t)
                    val sample = (
                        0.6 * sin(2.0 * PI * freq1 * t) +
                        0.3 * sin(2.0 * PI * freq2 * t) +
                        0.1 * sin(2.0 * PI * freq3 * t)
                    ) * decay
                    buffer[i] = (sample * 24000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                delay((durationSec * 1000).toLong())
                audioTrack.stop()
                audioTrack.release()
                onFinish?.invoke()
            } catch (e: Exception) {
                onFinish?.invoke()
            }
        }
    }

    /**
     * Starts looping ambient soundscape
     */
    fun startAmbient(scope: CoroutineScope, soundType: String) {
        stopAmbient()
        if (soundType == "SILENCE") return

        isPlayingAmbient = true
        ambientJob = scope.launch(Dispatchers.Default) {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(4096)

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize * 4)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                val buffer = ShortArray(minBufferSize)
                var phase = 0.0
                var noteIndex = 0
                val harpNotes = doubleArrayOf(261.63, 329.63, 392.00, 523.25, 440.00, 392.00) // C, E, G, C5, A, G
                var sampleCounter = 0L

                while (isActive && isPlayingAmbient) {
                    when (soundType) {
                        "HARPE" -> {
                            val currentFreq = harpNotes[noteIndex % harpNotes.size]
                            for (i in 0 until buffer.size) {
                                val t = sampleCounter.toDouble() / SAMPLE_RATE
                                val noteProgress = (sampleCounter % (SAMPLE_RATE * 2)).toDouble() / (SAMPLE_RATE * 2)
                                val envelope = exp(-2.0 * noteProgress) * 0.45
                                val sample = (sin(2.0 * PI * currentFreq * t) + 0.3 * sin(4.0 * PI * currentFreq * t)) * envelope
                                buffer[i] = (sample * 16000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                                sampleCounter++
                                if (sampleCounter % (SAMPLE_RATE * 2) == 0L) {
                                    noteIndex++
                                }
                            }
                        }
                        "PLUIE" -> {
                            // Pink noise generator for gentle soothing rainfall
                            var b0 = 0.0
                            var b1 = 0.0
                            var b2 = 0.0
                            for (i in 0 until buffer.size) {
                                val white = Random.nextDouble(-1.0, 1.0)
                                b0 = 0.99886 * b0 + white * 0.0555179
                                b1 = 0.99332 * b1 + white * 0.0750759
                                b2 = 0.96900 * b2 + white * 0.1538520
                                val pink = (b0 + b1 + b2 + white * 0.5362) * 0.08
                                buffer[i] = (pink * 14000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                            }
                        }
                        "BRISE" -> {
                            // Low frequency breath-like wave modulation
                            for (i in 0 until buffer.size) {
                                val t = sampleCounter.toDouble() / SAMPLE_RATE
                                val waveMod = (sin(2.0 * PI * 0.15 * t) + 1.0) * 0.5
                                val white = Random.nextDouble(-1.0, 1.0)
                                val sample = white * waveMod * 0.09
                                buffer[i] = (sample * 12000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                                sampleCounter++
                            }
                        }
                        "CLOCHE" -> {
                            // Ambient deep drone with periodic gentle bell tone
                            for (i in 0 until buffer.size) {
                                val t = sampleCounter.toDouble() / SAMPLE_RATE
                                val cycle = (sampleCounter % (SAMPLE_RATE * 8)).toDouble() / (SAMPLE_RATE * 8)
                                val strike = if (cycle < 0.3) exp(-3.0 * (cycle / 0.3)) else 0.0
                                val sample = (0.2 * sin(2.0 * PI * 130.81 * t) + strike * 0.35 * sin(2.0 * PI * 523.25 * t))
                                buffer[i] = (sample * 15000.0).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                                sampleCounter++
                            }
                        }
                        else -> {
                            buffer.fill(0)
                        }
                    }
                    audioTrack.write(buffer, 0, buffer.size)
                }

                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // Audio track graceful handling
            }
        }
    }

    fun stopAmbient() {
        isPlayingAmbient = false
        ambientJob?.cancel()
        ambientJob = null
    }

    fun triggerGentleHaptic(vibrator: Vibrator?) {
        try {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrationEffect = VibrationEffect.createWaveform(
                        longArrayOf(0, 150, 100, 200),
                        intArrayOf(0, 80, 0, 120),
                        -1
                    )
                    vibrator.vibrate(vibrationEffect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(300)
                }
            }
        } catch (e: Exception) {
            // Ignored
        }
    }
}
