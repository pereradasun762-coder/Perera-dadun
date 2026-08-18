package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.AspectRatioOption
import com.example.data.model.AudioMood
import com.example.data.model.L10n
import com.example.data.model.PromptInspirations
import com.example.data.model.UserProfileEntity
import com.example.data.model.VideoEntity
import com.example.data.model.VideoStyle
import com.example.ui.components.CreditBadge
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    promptText: String,
    selectedStyle: VideoStyle,
    selectedAspectRatio: AspectRatioOption,
    selectedDuration: Int,
    selectedAudioMood: AudioMood,
    userProfile: UserProfileEntity?,
    isEnhancing: Boolean,
    recentVideos: List<VideoEntity>,
    onOpenOutOfCredits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val language = viewModel.currentLanguage
    val credits = userProfile?.creditsRemaining ?: 5
    var showAdvancedSettings by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        // --- Top App Header Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kattiya_logo),
                    contentDescription = "Kattiya Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = L10n.getString("app_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = if (language == AppLanguage.SINHALA) "AI වීඩියෝ නිර්මාපකය" else "AI Video Creator",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SecondaryCyan,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Right side: Language Switcher & Credits
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Switcher Pill
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.toggleLanguage() }
                        .testTag("language_toggle_button"),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E1A38),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = if (language == AppLanguage.ENGLISH) "🇺🇸 EN" else "🇱🇰 සිංහල", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Credit Badge
                CreditBadge(
                    credits = credits,
                    language = language,
                    onClick = onOpenOutOfCredits
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // --- Video Prompt Creation Section ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Brush.linearGradient(listOf(PrimaryViolet.copy(alpha = 0.5f), SecondaryCyan.copy(alpha = 0.3f)))
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.SINHALA) "වීඩියෝ විස්තරය (Prompt)" else "Describe Your AI Video",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                    )

                    // Magic Enhance Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(enabled = !isEnhancing) { viewModel.enhancePrompt() }
                            .testTag("magic_enhance_button"),
                        shape = RoundedCornerShape(14.dp),
                        color = PrimaryViolet.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isEnhancing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = SecondaryCyan
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Enhance",
                                    tint = SecondaryCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = L10n.getString("magic_enhance", language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SecondaryCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prompt Input Field
                OutlinedTextField(
                    value = promptText,
                    onValueChange = { viewModel.setPromptText(it) },
                    placeholder = {
                        Text(
                            text = L10n.getString("prompt_placeholder", language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                        .testTag("video_prompt_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PrimaryViolet,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedContainerColor = Color(0xFF17142B),
                        unfocusedContainerColor = Color(0xFF17142B)
                    ),
                    trailingIcon = {
                        if (promptText.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setPromptText("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Quick Inspiration Section ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "💡 " + L10n.getString("inspiration", language),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentAmber,
                        fontSize = 13.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(PromptInspirations.items) { item ->
                    val title = if (language == AppLanguage.SINHALA) item.titleSi else item.titleEn
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.applyPromptInspiration(item.id) }
                            .testTag("inspiration_chip_${item.id}"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1C1835),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = item.emoji, fontSize = 14.sp)
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- Video Style Selector ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "🎨 " + L10n.getString("choose_style", language),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(VideoStyle.entries) { style ->
                    val isSelected = selectedStyle == style
                    val title = if (language == AppLanguage.SINHALA) style.titleSi else style.titleEn

                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.setStyle(style) }
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(listOf(Color(0xFF2E1955), Color(0xFF1B1536)))
                                } else {
                                    Brush.verticalGradient(listOf(DarkSurfaceElevated, DarkSurfaceElevated))
                                }
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) PrimaryViolet else DarkSurfaceBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                            .testTag("style_chip_${style.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = style.emoji, fontSize = 24.sp)
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Settings Section (Aspect Ratio, Duration, Audio Mood) ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(18.dp),
            color = DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .animateContentSize()
            ) {
                // Header row with toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdvancedSettings = !showAdvancedSettings },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = SecondaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (language == AppLanguage.SINHALA) "වීඩියෝ සැකසුම් (Ratio, Duration, Audio)" else "Video Settings & Audio",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Text(
                        text = "${selectedAspectRatio.ratioText} • ${selectedDuration}s",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SecondaryCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                // Expandable options
                if (showAdvancedSettings) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Aspect Ratio
                    Text(
                        text = L10n.getString("aspect_ratio", language),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AspectRatioOption.entries.forEach { option ->
                            val isSelected = selectedAspectRatio == option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setAspectRatio(option) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PrimaryViolet.copy(alpha = 0.3f) else Color(0xFF19162D),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) PrimaryViolet else DarkSurfaceBorder
                                )
                            ) {
                                Text(
                                    text = option.ratioText,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Duration Selector
                    Text(
                        text = L10n.getString("duration", language),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15).forEach { dur ->
                            val isSelected = selectedDuration == dur
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setDuration(dur) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) SecondaryCyan.copy(alpha = 0.3f) else Color(0xFF19162D),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) SecondaryCyan else DarkSurfaceBorder
                                )
                            ) {
                                Text(
                                    text = "$dur ${L10n.getString("sec", language)}",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Audio Mood Selector
                    Text(
                        text = L10n.getString("audio_mood", language),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AudioMood.entries) { mood ->
                            val isSelected = selectedAudioMood == mood
                            val label = if (language == AppLanguage.SINHALA) mood.labelSi else mood.labelEn
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setAudioMood(mood) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF281C4A) else Color(0xFF19162D),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) PrimaryVioletLight else DarkSurfaceBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = mood.emoji, fontSize = 12.sp)
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Color.White else TextSecondary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Big Luminous "Generate AI Video" Action Button ---
        Button(
            onClick = { viewModel.generateVideo() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
                .testTag("generate_video_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF9333EA), Color(0xFF7E22CE), Color(0xFF06B6D4))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = L10n.getString("generate_video", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "⚡ " + L10n.getString("cost_1_credit", language),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AccentAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Spotlight / Recent Preview Section ---
        if (recentVideos.isNotEmpty()) {
            val spotlightVideo = recentVideos.first()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎬 " + if (language == AppLanguage.SINHALA) "නැවුම් නිර්මාණ පෙරදසුන" else "Spotlight AI Video",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = spotlightVideo.style.titleEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SecondaryCyan,
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Video Player Card
                        VideoPlayerView(
                            video = spotlightVideo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            autoPlay = false,
                            showControls = true,
                            compact = false
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = spotlightVideo.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = spotlightVideo.prompt,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "By ${spotlightVideo.creatorName} • ❤️ ${spotlightVideo.likesCount}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.shareVideo(context, spotlightVideo) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF221C3E)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = SecondaryCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = L10n.getString("share", language),
                                            color = SecondaryCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { viewModel.remixPrompt(spotlightVideo) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = PrimaryViolet.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Remix",
                                            tint = PrimaryVioletLight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = L10n.getString("remix", language),
                                            color = PrimaryVioletLight,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
