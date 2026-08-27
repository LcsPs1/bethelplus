import 'dart:async';
import 'package:flutter/services.dart';
import 'package:audioplayers/audioplayers.dart';

class AudioService {
  static final AudioService _instance = AudioService._();
  static AudioService get instance => _instance;

  AudioService._() {
    _initPlayers();
  }

  AudioPlayer? _ambientPlayer;
  AudioPlayer? _sfxPlayer;

  bool _isPlayingAmbient = false;
  String _currentSoundType = "HARPE";
  double _ambientVolume = 0.8;

  bool get isPlayingAmbient => _isPlayingAmbient;
  String get currentSoundType => _currentSoundType;
  double get ambientVolume => _ambientVolume;

  void _initPlayers() {
    try {
      _ambientPlayer = AudioPlayer();
      _ambientPlayer?.setReleaseMode(ReleaseMode.loop).catchError((_) {});
      _sfxPlayer = AudioPlayer();
      _sfxPlayer?.setReleaseMode(ReleaseMode.stop).catchError((_) {});
    } catch (_) {}
  }

  Future<void> startAmbient(String soundType) async {
    await stopAmbient();
    if (soundType.toUpperCase() == "SILENCE") return;

    _isPlayingAmbient = true;
    _currentSoundType = soundType;

    final assetPath = _resolveSoundAsset(soundType);
    if (assetPath != null && _ambientPlayer != null) {
      try {
        await _ambientPlayer!.setVolume(_ambientVolume);
        await _ambientPlayer!.setReleaseMode(ReleaseMode.loop);
        await _ambientPlayer!.play(AssetSource(assetPath));
      } catch (_) {}
    }
  }

  Future<void> stopAmbient() async {
    _isPlayingAmbient = false;
    if (_ambientPlayer != null) {
      try {
        await _ambientPlayer!.stop();
      } catch (_) {}
    }
  }

  Future<void> setVolume(double volume) async {
    _ambientVolume = volume.clamp(0.0, 1.0);
    if (_ambientPlayer != null) {
      try {
        await _ambientPlayer!.setVolume(_ambientVolume);
      } catch (_) {}
    }
  }

  Future<void> playSanctuaryBell() async {
    HapticFeedback.heavyImpact();
    Timer(const Duration(milliseconds: 300), () {
      HapticFeedback.mediumImpact();
    });

    if (_sfxPlayer != null) {
      try {
        await _sfxPlayer!.setVolume(1.0);
        await _sfxPlayer!.play(AssetSource('audio/sanctuary_bell.mp3'));
      } catch (_) {}
    }
  }

  void triggerGentleHaptic() {
    HapticFeedback.lightImpact();
  }

  String? _resolveSoundAsset(String soundType) {
    switch (soundType.toUpperCase()) {
      case "HARPE":
        return "audio/harpe.mp3";
      case "PLUIE":
        return "audio/pluie.mp3";
      case "BRISE":
        return "audio/brise.mp3";
      case "CLOCHE":
        return "audio/cloche.mp3";
      default:
        return "audio/harpe.mp3";
    }
  }

  void dispose() {
    _ambientPlayer?.dispose();
    _sfxPlayer?.dispose();
  }
}
