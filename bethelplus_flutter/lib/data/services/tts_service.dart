import 'package:flutter_tts/flutter_tts.dart';

class TtsService {
  final FlutterTts _flutterTts = FlutterTts();
  bool _isPlaying = false;
  bool _isPaused = false;
  double _speechRate = 0.5; // FlutterTts normal speed is 0.5 on iOS/Android

  bool get isPlaying => _isPlaying;
  bool get isPaused => _isPaused;
  double get speechRate => _speechRate;

  Function(int currentVerseIndex)? onVerseChanged;
  Function()? onCompleted;

  TtsService() {
    _initTts();
  }

  Future<void> _initTts() async {
    await _flutterTts.setLanguage("fr-FR");
    await _flutterTts.setSpeechRate(_speechRate);
    await _flutterTts.setVolume(1.0);
    await _flutterTts.setPitch(1.0);

    // iOS specific audio category
    await _flutterTts.setIosAudioCategory(
      IosTextToSpeechAudioCategory.ambientSolo,
      [
        IosTextToSpeechAudioCategoryOptions.allowBluetooth,
        IosTextToSpeechAudioCategoryOptions.allowBluetoothA2DP,
        IosTextToSpeechAudioCategoryOptions.mixWithOthers,
      ],
    );

    _flutterTts.setCompletionHandler(() {
      _isPlaying = false;
      _isPaused = false;
      onCompleted?.call();
    });

    _flutterTts.setErrorHandler((msg) {
      _isPlaying = false;
      _isPaused = false;
    });
  }

  Future<void> speak(String text) async {
    if (text.isEmpty) return;
    _isPlaying = true;
    _isPaused = false;
    await _flutterTts.speak(text);
  }

  Future<void> pause() async {
    if (_isPlaying) {
      await _flutterTts.pause();
      _isPlaying = false;
      _isPaused = true;
    }
  }

  Future<void> stop() async {
    await _flutterTts.stop();
    _isPlaying = false;
    _isPaused = false;
  }

  Future<void> setRate(double rate) async {
    _speechRate = rate;
    await _flutterTts.setSpeechRate(rate);
  }
}
