import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppTheme {
  // Original Spiritual Brand Palette - Béthel+
  static const Color bethelGold = Color(0xFFC49A45);
  static const Color bethelGoldLight = Color(0xFFE6C687);
  static const Color bethelGoldDark = Color(0xFF8F6B21);
  static const Color bethelGoldContainer = Color(0xFFF9F1DC);
  static const Color bethelOnGoldContainer = Color(0xFF4A380A);
  
  // Light Theme Colors
  static const Color bethelSandBackground = Color(0xFFFBF8F2);
  static const Color bethelSandSurface = Color(0xFFFFFFFF);
  static const Color bethelSandSurfaceVariant = Color(0xFFF4EDE2);
  static const Color bethelSandBorder = Color(0xFFE6DCCF);
  static const Color bethelSandTextPrimary = Color(0xFF231F1A);
  static const Color bethelSandTextSecondary = Color(0xFF6E675D);
  static const Color bethelOlive = Color(0xFF506B57);
  static const Color bethelTerracotta = Color(0xFFB85D43);

  // Dark Theme Colors
  static const Color bethelMidnightBackground = Color(0xFF0C131F);
  static const Color bethelMidnightSurface = Color(0xFF141E30);
  static const Color bethelMidnightSurfaceVariant = Color(0xFF1E2C44);
  static const Color bethelMidnightBorder = Color(0xFF2A3B5A);
  static const Color bethelMidnightTextPrimary = Color(0xFFF3EFE6);
  static const Color bethelMidnightTextSecondary = Color(0xFFA8B4C7);
  static const Color bethelOliveContainer = Color(0xFFD8E6DC);
  static const Color bethelTerracottaLight = Color(0xFFF2DCD3);

  static TextTheme _buildTextTheme(Color primary, Color secondary) {
    return GoogleFonts.interTextTheme().copyWith(
      headlineLarge: GoogleFonts.lora(
        fontSize: 32,
        fontWeight: FontWeight.bold,
        color: primary,
      ),
      headlineMedium: GoogleFonts.lora(
        fontSize: 24,
        fontWeight: FontWeight.bold,
        color: primary,
      ),
      titleLarge: GoogleFonts.lora(
        fontSize: 22,
        fontWeight: FontWeight.bold,
        color: primary,
      ),
      titleMedium: GoogleFonts.lora(
        fontSize: 16,
        fontWeight: FontWeight.bold,
        color: primary,
      ),
      titleSmall: GoogleFonts.lora(
        fontSize: 14,
        fontWeight: FontWeight.w600,
        color: primary,
      ),
      bodyLarge: GoogleFonts.inter(
        fontSize: 16,
        color: primary,
      ),
      bodyMedium: GoogleFonts.inter(
        fontSize: 14,
        color: secondary,
      ),
      bodySmall: GoogleFonts.inter(
        fontSize: 12,
        color: secondary,
      ),
      labelLarge: GoogleFonts.inter(
        fontSize: 14,
        fontWeight: FontWeight.w600,
        color: primary,
      ),
      labelMedium: GoogleFonts.inter(
        fontSize: 12,
        fontWeight: FontWeight.w500,
        color: primary,
      ),
      labelSmall: GoogleFonts.inter(
        fontSize: 11,
        fontWeight: FontWeight.w500,
        color: secondary,
      ),
    );
  }

  static final ThemeData lightTheme = ThemeData(
    useMaterial3: true,
    brightness: Brightness.light,
    colorScheme: const ColorScheme.light(
      primary: bethelGoldDark,
      onPrimary: Colors.white,
      primaryContainer: bethelGoldContainer,
      onPrimaryContainer: bethelOnGoldContainer,
      secondary: bethelOlive,
      onSecondary: Colors.white,
      secondaryContainer: bethelOliveContainer,
      onSecondaryContainer: Color(0xFF1B3322),
      tertiary: bethelTerracotta,
      onTertiary: Colors.white,
      surface: bethelSandSurface,
      onSurface: bethelSandTextPrimary,
      surfaceContainerHighest: bethelSandSurfaceVariant,
      onSurfaceVariant: bethelSandTextSecondary,
      outline: bethelSandBorder,
      outlineVariant: Color(0xFFD6C8B7),
    ),
    scaffoldBackgroundColor: bethelSandBackground,
    textTheme: _buildTextTheme(bethelSandTextPrimary, bethelSandTextSecondary),
    appBarTheme: const AppBarTheme(
      backgroundColor: bethelSandBackground,
      foregroundColor: bethelSandTextPrimary,
      elevation: 0,
      centerTitle: true,
    ),
    cardTheme: CardThemeData(
      color: bethelSandSurface,
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
    ),
    navigationBarTheme: NavigationBarThemeData(
      backgroundColor: bethelSandSurface,
      indicatorColor: bethelGoldContainer,
      labelTextStyle: WidgetStateProperty.resolveWith((states) {
        if (states.contains(WidgetState.selected)) {
          return GoogleFonts.inter(
            color: bethelGoldDark,
            fontSize: 12,
            fontWeight: FontWeight.w600,
          );
        }
        return GoogleFonts.inter(
          color: bethelSandTextSecondary,
          fontSize: 12,
        );
      }),
    ),
  );

  static final ThemeData darkTheme = ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    colorScheme: const ColorScheme.dark(
      primary: bethelGoldLight,
      onPrimary: Color(0xFF3F2E05),
      primaryContainer: Color(0xFF584109),
      onPrimaryContainer: bethelGoldContainer,
      secondary: bethelOliveContainer,
      onSecondary: Color(0xFF1E3524),
      secondaryContainer: bethelOlive,
      onSecondaryContainer: Colors.white,
      tertiary: bethelTerracottaLight,
      onTertiary: Color(0xFF4A1F13),
      surface: bethelMidnightSurface,
      onSurface: bethelMidnightTextPrimary,
      surfaceContainerHighest: bethelMidnightSurfaceVariant,
      onSurfaceVariant: bethelMidnightTextSecondary,
      outline: bethelMidnightBorder,
      outlineVariant: Color(0xFF23324C),
    ),
    scaffoldBackgroundColor: bethelMidnightBackground,
    textTheme: _buildTextTheme(bethelMidnightTextPrimary, bethelMidnightTextSecondary),
    appBarTheme: const AppBarTheme(
      backgroundColor: bethelMidnightBackground,
      foregroundColor: bethelMidnightTextPrimary,
      elevation: 0,
      centerTitle: true,
    ),
    cardTheme: CardThemeData(
      color: bethelMidnightSurface,
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
    ),
    navigationBarTheme: NavigationBarThemeData(
      backgroundColor: bethelMidnightSurface,
      indicatorColor: const Color(0xFF584109),
      labelTextStyle: WidgetStateProperty.resolveWith((states) {
        if (states.contains(WidgetState.selected)) {
          return GoogleFonts.inter(
            color: bethelGoldLight,
            fontSize: 12,
            fontWeight: FontWeight.w600,
          );
        }
        return GoogleFonts.inter(
          color: bethelMidnightTextSecondary,
          fontSize: 12,
        );
      }),
    ),
  );
}
