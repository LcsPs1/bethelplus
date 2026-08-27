package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.MemorizedVerseEntity
import com.example.data.local.MemoryData
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.SanctuaryNavy
import com.example.ui.viewmodel.BethelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsBottomSheet(
    viewModel: BethelViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val allVerses by viewModel.allMemorizedVerses.collectAsState()
    val dueVerses by viewModel.dueMemorizedVerses.collectAsState()
    val masteredVerses by viewModel.masteredMemorizedVerses.collectAsState()

    var selectedFilter by remember { mutableStateOf("DUE") } // "DUE", "ALL", "MASTERED"
    var showAddVerseDialog by remember { mutableStateOf(false) }

    val displayList = remember(selectedFilter, allVerses, dueVerses, masteredVerses) {
        when (selectedFilter) {
            "DUE" -> if (dueVerses.isNotEmpty()) dueVerses else allVerses
            "MASTERED" -> masteredVerses
            else -> allVerses
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("memorized_verses_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🧠 Mémorisation de Versets",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Répétition espacée (Méthode Leitner) pour graver la Parole",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Leitner Stats Grid
            LeitnerBoxesOverview(allVerses = allVerses)

            Spacer(modifier = Modifier.height(14.dp))

            // Filters and Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFilter == "DUE",
                        onClick = { selectedFilter = "DUE" },
                        label = { Text("À réviser (${dueVerses.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("Tous (${allVerses.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "MASTERED",
                        onClick = { selectedFilter = "MASTERED" },
                        label = { Text("Maîtrisés (${masteredVerses.size})") }
                    )
                }

                IconButton(
                    onClick = { showAddVerseDialog = true },
                    modifier = Modifier.testTag("btn_add_memorize_verse")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter un verset", tint = GoldAccent)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Verses / Flashcards List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                if (displayList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🎉 Aucun verset en attente de révision !",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Vous avez révisé tous vos versets prévus. Ajoutez-en de nouveaux !",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { showAddVerseDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = SanctuaryNavy)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ajouter un verset")
                                }
                            }
                        }
                    }
                } else {
                    items(displayList, key = { it.id }) { verse ->
                        InteractiveFlashcardItem(
                            verse = verse,
                            onReviewResult = { isKnown ->
                                viewModel.recordFlashcardReview(verse, isKnown)
                            },
                            onDelete = {
                                viewModel.deleteMemorizedVerse(verse)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddVerseDialog) {
        AddMemorizedVerseDialog(
            onAddVerse = { ref, text, cat ->
                viewModel.addVerseToMemory(
                    reference = ref,
                    text = text,
                    category = cat
                )
                showAddVerseDialog = false
            },
            onDismiss = { showAddVerseDialog = false }
        )
    }
}

@Composable
fun LeitnerBoxesOverview(
    allVerses: List<MemorizedVerseEntity>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SanctuaryNavy.copy(alpha = 0.08f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Boîtes de Leitner (Fréquence)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = SanctuaryNavy
                )
                Text(
                    text = "${allVerses.size} versets suivis",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..5).forEach { boxNum ->
                    val count = allVerses.count { it.boxLevel == boxNum }
                    val label = when (boxNum) {
                        1 -> "1j"
                        2 -> "2j"
                        3 -> "4j"
                        4 -> "7j"
                        else -> "14j"
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (boxNum == 5) Color(0xFF2E7D32)
                                    else if (count > 0) SanctuaryNavy
                                    else MaterialTheme.colorScheme.outlineVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "B$boxNum ($label)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveFlashcardItem(
    verse: MemorizedVerseEntity,
    onReviewResult: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "flashcard_flip"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isFlipped = !isFlipped }
            .testTag("flashcard_${verse.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFlipped) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Card top row: Box level & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (verse.boxLevel >= 5) Color(0xFF2E7D32) else SanctuaryNavy,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (verse.boxLevel >= 5) "MAÎTRISÉ (B5)" else "BOÎTE ${verse.boxLevel}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = GoldContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = verse.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SanctuaryNavy
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isFlipped = !isFlipped },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Flip,
                            contentDescription = "Retourner la carte",
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Content Side: Front vs Back
            if (!isFlipped) {
                // FRONT: Reference + Fill-in prompt
                Text(
                    text = verse.reference,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                val words = verse.text.split(" ")
                val masked = if (words.size > 5) {
                    words.take(3).joinToString(" ") + " ... [Récitez la suite] ... " + words.takeLast(2).joinToString(" ")
                } else {
                    "« Touchez pour vérifier votre récitation »"
                }

                Text(
                    text = masked,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Toucher pour révéler le verset complet",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldAccent
                    )
                }
            } else {
                // BACK: Full verse text
                Text(
                    text = verse.reference,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "« ${verse.text} »",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Leitner Evaluation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onReviewResult(false)
                            isFlipped = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_review_hard_${verse.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFC62828)
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("À revoir (B1)", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onReviewResult(true)
                            isFlipped = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_review_known_${verse.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Je le sais !", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddMemorizedVerseDialog(
    onAddVerse: (reference: String, text: String, category: String) -> Unit,
    onDismiss: () -> Unit
) {
    var reference by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Foi & Victoire") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dialog_add_memorize_verse")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nouveau Verset à Mémoriser",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("Référence (ex. Jean 3:16, Philippiens 4:13)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_mem_reference"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Texte du verset") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_mem_text"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Thème / Catégorie") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_mem_category"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (reference.isNotBlank() && text.isNotBlank()) {
                            onAddVerse(reference.trim(), text.trim(), category.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_submit_add_mem_verse"),
                    enabled = reference.isNotBlank() && text.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SanctuaryNavy,
                        contentColor = Color.White
                    )
                ) {
                    Text("Ajouter aux cartes (Boîte 1)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
