package com.example.model

import androidx.compose.ui.graphics.Color

enum class AppThemePreset(
    val id: String,
    val displayName: String,
    val requiredLevel: Int,
    val requiredRank: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val tertiaryColor: Color,
    val darkBackground: Color,
    val darkSurface: Color,
    val darkSurfaceElevated: Color,
    val accentGlow: Color
) {
    ONYX_FLAME(
        id = "onyx_flame",
        displayName = "Onyx Flame",
        requiredLevel = 1,
        requiredRank = "Novice",
        primaryColor = Color(0xFFFF4500), // Flame Orange
        secondaryColor = Color(0xFFF59E0B), // Amber
        tertiaryColor = Color(0xFF10B981), // Emerald
        darkBackground = Color(0xFF09090B),
        darkSurface = Color(0xFF141418),
        darkSurfaceElevated = Color(0xFF1F1F26),
        accentGlow = Color(0x59FF4500)
    ),
    CYBER_NEON(
        id = "cyber_neon",
        displayName = "Cyber Neon",
        requiredLevel = 2,
        requiredRank = "Challenger",
        primaryColor = Color(0xFF00FF9D), // Electric Neon Green
        secondaryColor = Color(0xFF06D6A0), // Emerald Mint
        tertiaryColor = Color(0xFF00E5FF), // Cyan
        darkBackground = Color(0xFF060D09),
        darkSurface = Color(0xFF0E1A13),
        darkSurfaceElevated = Color(0xFF17281E),
        accentGlow = Color(0x5900FF9D)
    ),
    ELECTRIC_VOLT(
        id = "electric_volt",
        displayName = "Electric Volt",
        requiredLevel = 3,
        requiredRank = "Warrior",
        primaryColor = Color(0xFF00E5FF), // Neon Cyan
        secondaryColor = Color(0xFF3B82F6), // Blue
        tertiaryColor = Color(0xFF8B5CF6), // Purple
        darkBackground = Color(0xFF050B14),
        darkSurface = Color(0xFF0C1626),
        darkSurfaceElevated = Color(0xFF14223A),
        accentGlow = Color(0x5900E5FF)
    ),
    SOLAR_SPARTAN(
        id = "solar_spartan",
        displayName = "Solar Spartan",
        requiredLevel = 5,
        requiredRank = "Titan",
        primaryColor = Color(0xFFFFB800), // Imperial Gold
        secondaryColor = Color(0xFFFF7A00), // Solar Orange
        tertiaryColor = Color(0xFFE11D48), // Rose
        darkBackground = Color(0xFF0F0B04),
        darkSurface = Color(0xFF1C1608),
        darkSurfaceElevated = Color(0xFF2B210B),
        accentGlow = Color(0x59FFB800)
    ),
    CRIMSON_FURY(
        id = "crimson_fury",
        displayName = "Crimson Fury",
        requiredLevel = 7,
        requiredRank = "Beast",
        primaryColor = Color(0xFFFF1E56), // Hyper Crimson
        secondaryColor = Color(0xFFFF6347), // Tomato Flame
        tertiaryColor = Color(0xFFFBBF24), // Amber
        darkBackground = Color(0xFF0F0407),
        darkSurface = Color(0xFF1F0B11),
        darkSurfaceElevated = Color(0xFF2E101B),
        accentGlow = Color(0x59FF1E56)
    ),
    ASTRAL_VOID(
        id = "astral_void",
        displayName = "Astral Void",
        requiredLevel = 10,
        requiredRank = "Legend",
        primaryColor = Color(0xFFA855F7), // Nebula Purple
        secondaryColor = Color(0xFFEC4899), // Neon Pink
        tertiaryColor = Color(0xFF38BDF8), // Sky Blue
        darkBackground = Color(0xFF0A0514),
        darkSurface = Color(0xFF160D2A),
        darkSurfaceElevated = Color(0xFF231640),
        accentGlow = Color(0x59A855F7)
    );

    companion object {
        fun fromId(id: String?): AppThemePreset {
            return entries.find { it.id == id } ?: ONYX_FLAME
        }
    }
}
