package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.data.model.L10n
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan

@Composable
fun GeneratingVideoModal(
    progress: Float,
    stageText: String,
    stageIndex: Int,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gen_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing)),
        label = "rot"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(400),
        label = "smooth_prog"
    )

    val stages = if (language == AppLanguage.SINHALA) {
        listOf("විශ්ලේෂණය", "Storyboard", "4K සංස්ලේෂණය", "ශබ්ද මුසු කිරීම", "අවසන් කිරීම")
    } else {
        listOf("Analyze", "Storyboard", "4K Synthesis", "Audio Sync", "Finalize")
    }

    Dialog(
        onDismissRequest = { /* Prevent dismiss while generating */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.sweepGradient(listOf(PrimaryViolet, SecondaryCyan, PrimaryViolet)),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("generating_video_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Central Glowing AI Core Animation
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(pulse),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation)
                            .border(
                                width = 3.dp,
                                brush = Brush.sweepGradient(
                                    listOf(PrimaryViolet, SecondaryCyan, Color.Transparent, PrimaryViolet)
                                ),
                                shape = CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(PrimaryViolet.copy(alpha = 0.4f), Color.Transparent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = SecondaryCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = L10n.getString("generating", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 17.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stageText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = SecondaryCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryViolet,
                    trackColor = DarkSurfaceBorder,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Stage ${stageIndex + 1}/5",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryVioletLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pipeline Step Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    stages.forEachIndexed { index, name ->
                        val isDone = index < stageIndex
                        val isCurrent = index == stageIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone -> PrimaryViolet
                                            isCurrent -> SecondaryCyan
                                            else -> DarkSurfaceBorder
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = if (isCurrent) SecondaryCyan else Color.White.copy(alpha = 0.5f),
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
