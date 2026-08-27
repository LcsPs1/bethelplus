package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.SpiritualAlarmEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.SanctuaryNavy
import com.example.ui.viewmodel.BethelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SpiritualWakeupFullScreenDialog(
    viewModel: BethelViewModel,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunrise_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val morningVerses = remember {
        listOf(
            Pair("Psaume 143:8", "Fais-moi dès le matin entendre ta bonté ! Car je me confie en toi. Fais-moi connaître le chemin où je dois marcher !"),
            Pair("Lamentations 3:22-23", "Les bontés de l'Éternel ne sont pas épuisées, ses compassions ne sont pas à leur terme ; elles se renouvellent chaque matin. Grande est ta fidélité !"),
            Pair("Psaume 5:4", "Éternel ! le matin tu entends ma voix ; Le matin je me tourne vers toi, et je regarde."),
            Pair("Ésaïe 50:4", "Il éveille, chaque matin, il éveille mon oreille, Pour que j'écoute comme écoutent des disciples.")
        )
    }
    val morningVerse = remember { morningVerses.random() }
    val timeFormat = SimpleDateFormat("HH:mm", Locale.FRENCH)
    val currentTimeStr = remember { timeFormat.format(Date()) }

    Dialog(
        onDismissRequest = {
            viewModel.dismissSpiritualWakeup()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SanctuaryNavy,
                            Color(0xFF1B2B48),
                            Color(0xFF2C3E50),
                            Color(0xFF5A4A3A),
                            Color(0xFF9E7B4F)
                        )
                    )
                )
                .padding(24.dp)
                .testTag("dialog_spiritual_wakeup_fullscreen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = GoldAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.LightMode,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RÉVEIL SPIRITUEL BÉTHEL+",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldAccent
                            )
                        }
                    }

                    IconButton(onClick = {
                        viewModel.dismissSpiritualWakeup()
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
                    }
                }

                // Center Sunrise & Verse
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Pulsing Glowing Sun
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        GoldAccent,
                                        GoldAccent.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.WbSunny,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentTimeStr,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color.White
                    )

                    Text(
                        text = "« Voici le jour que l'Éternel a fait »",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontFamily = FontFamily.Serif
                        ),
                        color = GoldAccent
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Morning Word Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.12f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "« ${morningVerse.second} »",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = FontFamily.Serif,
                                    lineHeight = 26.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "— ${morningVerse.first}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldAccent
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Bénédiction du Matin : Que la grâce et la paix de Dieu gardent votre cœur et dirigent vos pas aujourd'hui.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Center
                                ),
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // Bottom Action buttons
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            viewModel.dismissSpiritualWakeup()
                            onDismiss()
                            viewModel.openFreePrayerTimer(5)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("btn_wakeup_bless_day"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Consacrer ma Journée (Prière 5 min)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.dismissSpiritualWakeup()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Snooze, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Répéter (5 min)")
                        }

                        Button(
                            onClick = {
                                viewModel.dismissSpiritualWakeup()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Entrer dans l'app")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpiritualAlarmConfigCard(
    alarm: SpiritualAlarmEntity?,
    onSaveAlarm: (SpiritualAlarmEntity) -> Unit,
    onTestWakeup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentAlarm = alarm ?: SpiritualAlarmEntity(
        id = 1,
        isEnabled = false,
        hour = 6,
        minute = 30,
        soundscape = "HARPE"
    )

    var showTimePickerDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("spiritual_alarm_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SanctuaryNavy
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.WbSunny,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mode Réveil Spirituel",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Éveillez-vous avec la Parole de Dieu",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Switch(
                    checked = currentAlarm.isEnabled,
                    onCheckedChange = { isChecked ->
                        onSaveAlarm(currentAlarm.copy(isEnabled = isChecked))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SanctuaryNavy,
                        checkedTrackColor = GoldAccent
                    ),
                    modifier = Modifier.testTag("switch_spiritual_alarm")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time selector display (clickable to open manual time picker)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { showTimePickerDialog = true }
                    .testTag("spiritual_alarm_time_box"),
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Heure de réveil programmée",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Modifier",
                                tint = GoldAccent.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = "%02d:%02d".format(currentAlarm.hour, currentAlarm.minute),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = GoldAccent
                        )
                        Text(
                            text = "Mélodie : ${currentAlarm.soundscape.lowercase().replaceFirstChar { it.uppercase() }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Button(
                        onClick = { showTimePickerDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_edit_spiritual_alarm_time")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Régler l'heure",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Test Wakeup Experience
            Button(
                onClick = onTestWakeup,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_test_spiritual_alarm"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent.copy(alpha = 0.2f),
                    contentColor = GoldAccent
                )
            ) {
                Icon(Icons.Filled.WbSunny, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aperçu de l'Éveil Spirituel", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showTimePickerDialog) {
        SpiritualAlarmTimePickerDialog(
            currentAlarm = currentAlarm,
            onDismiss = { showTimePickerDialog = false },
            onSave = { updatedAlarm ->
                onSaveAlarm(updatedAlarm)
                showTimePickerDialog = false
            }
        )
    }
}

@Composable
fun SpiritualAlarmTimePickerDialog(
    currentAlarm: SpiritualAlarmEntity,
    onDismiss: () -> Unit,
    onSave: (SpiritualAlarmEntity) -> Unit
) {
    var hour by remember { mutableIntStateOf(currentAlarm.hour) }
    var minute by remember { mutableIntStateOf(currentAlarm.minute) }
    var hourInput by remember { mutableStateOf("%02d".format(currentAlarm.hour)) }
    var minuteInput by remember { mutableStateOf("%02d".format(currentAlarm.minute)) }
    var soundscape by remember { mutableStateOf(currentAlarm.soundscape) }

    fun updateHour(newH: Int) {
        val clamped = ((newH % 24) + 24) % 24
        hour = clamped
        hourInput = "%02d".format(clamped)
    }

    fun updateMinute(newM: Int) {
        val clamped = ((newM % 60) + 60) % 60
        minute = clamped
        minuteInput = "%02d".format(clamped)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Régler l'heure de réveil",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Définissez l'heure d'éveil pour démarrer votre journée dans la louange et la prière.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Time picker central block (vertical buttons + large input)
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hour Column
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "HEURE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(
                                onClick = { updateHour(hour + 1) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Text("▲", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            OutlinedTextField(
                                value = hourInput,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() }.take(2)
                                    hourInput = filtered
                                    filtered.toIntOrNull()?.let { h ->
                                        if (h in 0..23) hour = h
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .width(68.dp)
                                    .testTag("input_alarm_hour"),
                                textStyle = MaterialTheme.typography.headlineMedium.copy(
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )
                            IconButton(
                                onClick = { updateHour(hour - 1) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Text("▼", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        // Colon separator
                        Text(
                            text = ":",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp)
                        )

                        // Minute Column
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "MINUTE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            IconButton(
                                onClick = { updateMinute(minute + 5) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Text("▲", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            OutlinedTextField(
                                value = minuteInput,
                                onValueChange = { input ->
                                    val filtered = input.filter { it.isDigit() }.take(2)
                                    minuteInput = filtered
                                    filtered.toIntOrNull()?.let { m ->
                                        if (m in 0..59) minute = m
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .width(68.dp)
                                    .testTag("input_alarm_minute"),
                                textStyle = MaterialTheme.typography.headlineMedium.copy(
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )
                            IconButton(
                                onClick = { updateMinute(minute - 5) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Text("▼", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                // Presets (2 lines for clean wrapping)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Heures matinales suggérées :",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair("05:00", Pair(5, 0)),
                            Pair("05:30", Pair(5, 30)),
                            Pair("06:00", Pair(6, 0))
                        ).forEach { (label, timePair) ->
                            OutlinedButton(
                                onClick = {
                                    updateHour(timePair.first)
                                    updateMinute(timePair.second)
                                },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair("06:30", Pair(6, 30)),
                            Pair("07:00", Pair(7, 0)),
                            Pair("07:30", Pair(7, 30))
                        ).forEach { (label, timePair) ->
                            OutlinedButton(
                                onClick = {
                                    updateHour(timePair.first)
                                    updateMinute(timePair.second)
                                },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Sound selection (2x2 grid so no label wraps or gets truncated)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ambiance sonore au réveil :",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("HARPE", "🎵 Harpe"),
                            Pair("CLOCHE", "🔔 Cloche")
                        ).forEach { (id, label) ->
                            FilterChip(
                                selected = soundscape == id,
                                onClick = { soundscape = id },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Pair("PLUIE", "🌧️ Pluie"),
                            Pair("SILENCE", "🕊️ Silence")
                        ).forEach { (id, label) ->
                            FilterChip(
                                selected = soundscape == id,
                                onClick = { soundscape = id },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalH = hourInput.toIntOrNull()?.coerceIn(0, 23) ?: hour
                    val finalM = minuteInput.toIntOrNull()?.coerceIn(0, 59) ?: minute
                    onSave(
                        currentAlarm.copy(
                            hour = finalH,
                            minute = finalM,
                            soundscape = soundscape,
                            isEnabled = true
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_save_spiritual_alarm_time")
            ) {
                Text("Enregistrer l'heure", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(22.dp)
    )
}

