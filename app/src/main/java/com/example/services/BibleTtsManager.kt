package com.example.services

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.data.local.BibleVerseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class BibleTtsManager(
    private val context: Context,
    private val scope: CoroutineScope
) : TextToSpeech.OnInitListener {

    private val TAG = "BibleTtsManager"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    // State Flows
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isPlayerVisible = MutableStateFlow(false)
    val isPlayerVisible: StateFlow<Boolean> = _isPlayerVisible.asStateFlow()

    private val _currentVerseIndex = MutableStateFlow(0)
    val currentVerseIndex: StateFlow<Int> = _currentVerseIndex.asStateFlow()

    private val _currentVerseNumber = MutableStateFlow(1)
    val currentVerseNumber: StateFlow<Int> = _currentVerseNumber.asStateFlow()

    private val _speechRate = MutableStateFlow(0.85f) // Calme / posé
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _pitch = MutableStateFlow(0.90f) // Voix plus grave, apaisante
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    // Active playlist
    private var currentVersesList: List<BibleVerseEntity> = emptyList()
    private var currentBookName: String = ""
    private var currentChapterNumber: Int = 1

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.FRENCH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.FRANCE)
            }

            // Configure male/deep serene voice if available
            configureVoice()

            tts?.setPitch(_pitch.value)
            tts?.setSpeechRate(_speechRate.value)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    scope.launch(Dispatchers.Main) {
                        _isPlaying.value = true
                        _isPaused.value = false
                    }
                }

                override fun onDone(utteranceId: String?) {
                    scope.launch(Dispatchers.Main) {
                        if (_isPlaying.value && !_isPaused.value) {
                            val nextIndex = _currentVerseIndex.value + 1
                            if (nextIndex < currentVersesList.size) {
                                _currentVerseIndex.value = nextIndex
                                val nextVerse = currentVersesList[nextIndex]
                                _currentVerseNumber.value = nextVerse.verse
                                speakSingleVerse(nextVerse, nextIndex)
                            } else {
                                // End of chapter reached
                                _isPlaying.value = false
                                _isPaused.value = false
                            }
                        }
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    scope.launch(Dispatchers.Main) {
                        _isPlaying.value = false
                        _isPaused.value = false
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    Log.e(TAG, "TTS Error code: $errorCode for utterance: $utteranceId")
                    scope.launch(Dispatchers.Main) {
                        _isPlaying.value = false
                        _isPaused.value = false
                    }
                }
            })

            isInitialized = true
        } else {
            Log.e(TAG, "TTS Initialization failed with status: $status")
        }
    }

    private fun configureVoice() {
        try {
            val voices = tts?.voices
            if (!voices.isNullOrEmpty()) {
                // Find French male voice
                val frenchMaleVoice = voices.firstOrNull { voice ->
                    val langMatch = voice.locale.language.equals("fr", ignoreCase = true)
                    val nameMatch = voice.name.contains("male", ignoreCase = true) ||
                            voice.name.contains("fr-fr-x-fra", ignoreCase = true) ||
                            voice.name.contains("fr-fr-x-frc", ignoreCase = true) ||
                            voice.name.contains("fr-fr-x-frd", ignoreCase = true) ||
                            voice.name.contains("homme", ignoreCase = true)
                    langMatch && nameMatch && !voice.isNetworkConnectionRequired
                } ?: voices.firstOrNull { voice ->
                    voice.locale.language.equals("fr", ignoreCase = true) &&
                            (voice.name.contains("male", ignoreCase = true) ||
                             voice.name.contains("fr-fr-x-fra", ignoreCase = true) ||
                             voice.name.contains("fr-fr-x-frc", ignoreCase = true))
                } ?: voices.firstOrNull { voice ->
                    voice.locale.language.equals("fr", ignoreCase = true)
                }

                if (frenchMaleVoice != null) {
                    tts?.voice = frenchMaleVoice
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not set custom voice: ${e.message}")
        }
    }

    fun startPlaying(
        verses: List<BibleVerseEntity>,
        startIndex: Int = 0,
        bookName: String = "",
        chapter: Int = 1
    ) {
        if (verses.isEmpty()) return
        currentVersesList = verses
        currentBookName = bookName
        currentChapterNumber = chapter

        _isPlayerVisible.value = true
        val safeIndex = startIndex.coerceIn(0, verses.size - 1)
        _currentVerseIndex.value = safeIndex
        _currentVerseNumber.value = verses[safeIndex].verse
        _isPlaying.value = true
        _isPaused.value = false

        speakSingleVerse(verses[safeIndex], safeIndex)
    }

    private fun speakSingleVerse(verse: BibleVerseEntity, index: Int) {
        if (tts == null) return

        tts?.setPitch(_pitch.value)
        tts?.setSpeechRate(_speechRate.value)

        val textToSpeak = formatVerseForSpeech(verse, index == 0)
        val params = Bundle()
        val utteranceId = "verse_${verse.bookId}_${verse.chapter}_${verse.verse}_$index"

        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    private fun formatVerseForSpeech(verse: BibleVerseEntity, isFirstVerse: Boolean): String {
        val sb = StringBuilder()
        if (isFirstVerse) {
            sb.append("${verse.bookName}, chapitre ${verse.chapter}. ")
        }
        sb.append("Verset ${verse.verse}. ")
        sb.append(verse.text)
        return sb.toString()
    }

    fun pause() {
        if (_isPlaying.value) {
            tts?.stop()
            _isPlaying.value = false
            _isPaused.value = true
        }
    }

    fun resume() {
        if (_isPaused.value && currentVersesList.isNotEmpty()) {
            val currentIndex = _currentVerseIndex.value.coerceIn(0, currentVersesList.size - 1)
            _isPlaying.value = true
            _isPaused.value = false
            speakSingleVerse(currentVersesList[currentIndex], currentIndex)
        } else if (!_isPlaying.value && currentVersesList.isNotEmpty()) {
            startPlaying(currentVersesList, _currentVerseIndex.value, currentBookName, currentChapterNumber)
        }
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = false
        _isPaused.value = false
    }

    fun closePlayer() {
        stop()
        _isPlayerVisible.value = false
    }

    fun showPlayer() {
        _isPlayerVisible.value = true
    }

    fun nextVerse() {
        if (currentVersesList.isNotEmpty()) {
            val nextIndex = (_currentVerseIndex.value + 1).coerceAtMost(currentVersesList.size - 1)
            _currentVerseIndex.value = nextIndex
            _currentVerseNumber.value = currentVersesList[nextIndex].verse
            if (_isPlaying.value || _isPaused.value) {
                _isPlaying.value = true
                _isPaused.value = false
                speakSingleVerse(currentVersesList[nextIndex], nextIndex)
            }
        }
    }

    fun previousVerse() {
        if (currentVersesList.isNotEmpty()) {
            val prevIndex = (_currentVerseIndex.value - 1).coerceAtLeast(0)
            _currentVerseIndex.value = prevIndex
            _currentVerseNumber.value = currentVersesList[prevIndex].verse
            if (_isPlaying.value || _isPaused.value) {
                _isPlaying.value = true
                _isPaused.value = false
                speakSingleVerse(currentVersesList[prevIndex], prevIndex)
            }
        }
    }

    fun seekToVerseIndex(index: Int) {
        if (currentVersesList.isNotEmpty()) {
            val safeIndex = index.coerceIn(0, currentVersesList.size - 1)
            _currentVerseIndex.value = safeIndex
            _currentVerseNumber.value = currentVersesList[safeIndex].verse
            _isPlaying.value = true
            _isPaused.value = false
            _isPlayerVisible.value = true
            speakSingleVerse(currentVersesList[safeIndex], safeIndex)
        }
    }

    fun setSpeed(rate: Float) {
        _speechRate.value = rate
        tts?.setSpeechRate(rate)
        // If playing, re-speak current verse with new rate
        if (_isPlaying.value && currentVersesList.isNotEmpty()) {
            val index = _currentVerseIndex.value.coerceIn(0, currentVersesList.size - 1)
            speakSingleVerse(currentVersesList[index], index)
        }
    }

    fun setPitch(pitchVal: Float) {
        _pitch.value = pitchVal
        tts?.setPitch(pitchVal)
    }

    fun onChapterChanged(newVerses: List<BibleVerseEntity>, bookName: String, chapter: Int) {
        if (_isPlaying.value || _isPaused.value) {
            // Automatically switch audio context to the new chapter or stop
            currentVersesList = newVerses
            currentBookName = bookName
            currentChapterNumber = chapter
            _currentVerseIndex.value = 0
            if (newVerses.isNotEmpty()) {
                _currentVerseNumber.value = newVerses[0].verse
                if (_isPlaying.value) {
                    speakSingleVerse(newVerses[0], 0)
                }
            } else {
                stop()
            }
        } else {
            currentVersesList = newVerses
            currentBookName = bookName
            currentChapterNumber = chapter
            _currentVerseIndex.value = 0
            if (newVerses.isNotEmpty()) {
                _currentVerseNumber.value = newVerses[0].verse
            }
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
