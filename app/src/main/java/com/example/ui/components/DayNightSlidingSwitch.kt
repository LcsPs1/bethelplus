package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BethelGold
import com.example.ui.theme.BethelGoldDark

/**
 * Animated sliding switch (bouton glissant) exclusively for Day (Jour) and Night (Nuit) modes.
 */
@Composable
fun DayNightSlidingSwitch(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 62.dp,
    height: Dp = 32.dp,
    thumbSize: Dp = 26.dp
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Track colors: Day = Warm amber/sunlit parchment, Night = Deep celestial sanctuary navy
    val trackBgColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF0F172A) else Color(0xFFFEF3C7),
        animationSpec = tween(300),
        label = "trackBgColor"
    )

    val trackBorderColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFF59E0B).copy(alpha = 0.6f),
        animationSpec = tween(300),
        label = "trackBorderColor"
    )

    // Thumb animation (sliding horizontally)
    val maxOffset = width - thumbSize - 6.dp
    val thumbOffset by animateDpAsState(
        targetValue = if (isDark) maxOffset else 3.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "thumbOffset"
    )

    // Thumb background color
    val thumbBgColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF1E293B) else Color(0xFFF59E0B),
        animationSpec = tween(300),
        label = "thumbBgColor"
    )

    // Thumb icon tint
    val thumbIconColor by animateColorAsState(
        targetValue = if (isDark) BethelGold else Color(0xFFFFFFFF),
        animationSpec = tween(300),
        label = "thumbIconColor"
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackBgColor)
            .border(1.dp, trackBorderColor, RoundedCornerShape(percent = 50))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onToggle(!isDark)
            }
            .testTag("day_night_sliding_switch"),
        contentAlignment = Alignment.CenterStart
    ) {
        // Background stationary icons
        Row(
            modifier = Modifier
                .width(width)
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LightMode,
                contentDescription = null,
                tint = if (isDark) Color(0xFF64748B) else Color(0xFFD97706).copy(alpha = 0.7f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                tint = if (isDark) Color(0xFF93C5FD) else Color(0xFFCBD5E1),
                modifier = Modifier.size(14.dp)
            )
        }

        // Sliding Thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .shadow(elevation = 3.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(thumbBgColor)
                .border(
                    BorderStroke(
                        1.dp,
                        if (isDark) Color(0xFF475569) else Color(0xFFFDE68A)
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.WbSunny,
                contentDescription = if (isDark) "Mode Nuit actif" else "Mode Jour actif",
                tint = thumbIconColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Large card-style Day/Night sliding switch with detailed labels for settings & profile.
 */
@Composable
fun LargeDayNightSlidingSwitch(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            // Day button area
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isDark) Color(0xFFFEF3C7) else Color.Transparent)
                    .border(
                        1.dp,
                        if (!isDark) Color(0xFFF59E0B) else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(interactionSource = interactionSource, indication = null) {
                        if (isDark) onToggle(false)
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = null,
                    tint = if (!isDark) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Jour",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (!isDark) FontWeight.Bold else FontWeight.Normal,
                    color = if (!isDark) Color(0xFF92400E) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // The animated sliding switch
            DayNightSlidingSwitch(
                isDark = isDark,
                onToggle = onToggle,
                width = 68.dp,
                height = 36.dp,
                thumbSize = 30.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Night button area
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF0F172A) else Color.Transparent)
                    .border(
                        1.dp,
                        if (isDark) Color(0xFF38BDF8).copy(alpha = 0.5f) else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(interactionSource = interactionSource, indication = null) {
                        if (!isDark) onToggle(true)
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = null,
                    tint = if (isDark) BethelGold else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Nuit",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isDark) FontWeight.Bold else FontWeight.Normal,
                    color = if (isDark) Color(0xFFE2E8F0) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
