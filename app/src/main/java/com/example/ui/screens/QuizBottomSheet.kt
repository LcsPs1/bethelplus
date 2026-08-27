package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BethelGold
import com.example.ui.theme.BethelGoldDark
import com.example.ui.theme.BethelGoldLight
import com.example.ui.theme.SanctuaryNavy
import com.example.ui.viewmodel.BethelViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizBottomSheet(
    viewModel: BethelViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isQuizLoading by viewModel.isQuizLoading.collectAsStateWithLifecycle()
    val quizQuestions by viewModel.quizQuestions.collectAsStateWithLifecycle()
    val currentQuestionIndex by viewModel.currentQuizQuestionIndex.collectAsStateWithLifecycle()
    val selectedAnswerIndex by viewModel.selectedQuizAnswerIndex.collectAsStateWithLifecycle()
    val quizScore by viewModel.quizScore.collectAsStateWithLifecycle()
    val isQuizFinished by viewModel.isQuizFinished.collectAsStateWithLifecycle()
    val quizError by viewModel.quizError.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quiz Biblique IA",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = BethelGoldDark
                )
                IconButton(onClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() } }) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            if (isQuizLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BethelGold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("L'IA prépare vos questions...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else if (quizError != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Erreur",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = quizError ?: "", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.startQuiz() },
                            colors = ButtonDefaults.buttonColors(containerColor = BethelGold)
                        ) {
                            Text("Réessayer")
                        }
                    }
                }
            } else if (quizQuestions.isEmpty()) {
                // Topic Selection
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "Choisissez un thème pour tester vos connaissances bibliques avec des questions générées par Gemini IA.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    val topics = listOf(
                        "Général (Ancien & Nouveau Testament)" to "📚",
                        "Les Paraboles de Jésus" to "🌱",
                        "Les Prophètes de l'Ancien Testament" to "📜",
                        "Les Femmes de la Bible" to "🕊️",
                        "Le Livre de la Genèse" to "🌍"
                    )
                    
                    items(topics.size) { i ->
                        val (title, icon) = topics[i]
                        Card(
                            onClick = { viewModel.startQuiz(title) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else if (isQuizFinished) {
                // Results Screen
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🎉", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Quiz Terminé",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Votre score : $quizScore / ${quizQuestions.size}",
                        style = MaterialTheme.typography.titleLarge,
                        color = BethelGoldDark
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { viewModel.resetQuiz() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BethelGold)
                    ) {
                        Text("Rejouer avec un autre thème", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Active Question
                val question = quizQuestions[currentQuestionIndex]
                val isAnswered = selectedAnswerIndex != null
                
                Column(modifier = Modifier.fillMaxSize()) {
                    // Progress
                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / quizQuestions.size.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = BethelGold,
                        trackColor = BethelGoldLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Question ${currentQuestionIndex + 1} sur ${quizQuestions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Options
                    question.options.forEachIndexed { index, option ->
                        val isCorrect = index == question.correctAnswerIndex
                        val isSelected = index == selectedAnswerIndex
                        
                        val backgroundColor = when {
                            !isAnswered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            isCorrect -> Color(0xFFE8F5E9) // Light Green
                            isSelected && !isCorrect -> Color(0xFFFFEBEE) // Light Red
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        }
                        
                        val borderColor = when {
                            !isAnswered -> Color.Transparent
                            isCorrect -> Color(0xFF4CAF50)
                            isSelected && !isCorrect -> Color(0xFFE53935)
                            else -> Color.Transparent
                        }
                        
                        Card(
                            onClick = { if (!isAnswered) viewModel.submitQuizAnswer(index) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, borderColor),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isAnswered && isCorrect) Color(0xFF4CAF50)
                                            else if (isAnswered && isSelected) Color(0xFFE53935)
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A' + index).toString(),
                                        color = if (isAnswered && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isAnswered && !isCorrect && !isSelected) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    // Explanation
                    if (isAnswered) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BethelGoldLight.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💡 Explication",
                                    fontWeight = FontWeight.Bold,
                                    color = BethelGoldDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = question.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = { viewModel.nextQuizQuestion() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BethelGold)
                        ) {
                            Text(
                                text = if (currentQuestionIndex == quizQuestions.size - 1) "Voir les résultats" else "Question suivante",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
