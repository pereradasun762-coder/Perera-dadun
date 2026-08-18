package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.L10n
import com.example.data.model.UserProfileEntity
import com.example.data.model.VideoEntity
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

enum class ProfileTab {
    MY_VIDEOS,
    LIKED,
    SAVED
}

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    userProfile: UserProfileEntity?,
    userVideos: List<VideoEntity>,
    likedVideos: List<VideoEntity>,
    savedVideos: List<VideoEntity>,
    language: AppLanguage,
    onOpenOutOfCredits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(ProfileTab.MY_VIDEOS) }
    val profile = userProfile ?: UserProfileEntity()

    val currentList = when (selectedTab) {
        ProfileTab.MY_VIDEOS -> userVideos
        ProfileTab.LIKED -> likedVideos
        ProfileTab.SAVED -> savedVideos
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Profile Header Card ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(PrimaryViolet.copy(alpha = 0.5f), SecondaryCyan.copy(alpha = 0.3f)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(PrimaryViolet, SecondaryCyan))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = profile.avatarEmoji, fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 17.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF262046)
                    ) {
                        Text(
                            text = if (language == AppLanguage.SINHALA) "නොමිලේ සාමාජිකයා • Free Plan" else "Free Creator Tier",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SecondaryCyan,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF141126))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${userVideos.size}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = L10n.getString("generated_videos", language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(DarkSurfaceBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${likedVideos.size}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                            )
                            Text(
                                text = L10n.getString("liked_videos", language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(DarkSurfaceBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${savedVideos.size}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryVioletLight
                                )
                            )
                            Text(
                                text = L10n.getString("saved", language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Credit Balance & Claim Card ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1B1736),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentAmber.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AccentAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = AccentAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "${profile.creditsRemaining} ${L10n.getString("credits", language)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = if (language == AppLanguage.SINHALA) "වීඩියෝ එකක් සඳහා 1 Credit" else "1 Credit per AI Video generation",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Button(
                        onClick = onOpenOutOfCredits,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentAmber
                        ),
                        modifier = Modifier.testTag("profile_claim_credits_button")
                    ) {
                        Text(
                            text = "+ " + L10n.getString("claim_free_credits", language),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // --- 3. App Settings & Language Selector Row ---
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = SecondaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = L10n.getString("language", language),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (language == AppLanguage.SINHALA) "දැනට: සිංහල (Sinhala 🇱🇰)" else "Current: English (United States 🇺🇸)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.toggleLanguage() },
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryViolet.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = if (language == AppLanguage.ENGLISH) "සිංහලට මාරු වන්න" else "Switch to English",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PrimaryVioletLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // --- 4. Content Filter Tabs (My Videos, Liked, Saved) ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val title = when (tab) {
                        ProfileTab.MY_VIDEOS -> L10n.getString("my_videos", language)
                        ProfileTab.LIKED -> L10n.getString("liked_videos", language)
                        ProfileTab.SAVED -> L10n.getString("saved", language)
                    }
                    val icon = when (tab) {
                        ProfileTab.MY_VIDEOS -> Icons.Default.Videocam
                        ProfileTab.LIKED -> Icons.Default.Favorite
                        ProfileTab.SAVED -> Icons.Default.Bookmark
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedTab = tab }
                            .testTag("profile_tab_${tab.name}"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) PrimaryViolet.copy(alpha = 0.3f) else DarkSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PrimaryViolet else DarkSurfaceBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) PrimaryVioletLight else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = title,
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

        // --- 5. Videos List ---
        if (currentList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎬", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (selectedTab) {
                                ProfileTab.MY_VIDEOS -> if (language == AppLanguage.SINHALA) "ඔබ තවම වීඩියෝවක් සාදා නැත. Home වෙතින් පළමු වීඩියෝව සාදන්න!" else "No videos created yet. Create your first AI video on the Home tab!"
                                ProfileTab.LIKED -> if (language == AppLanguage.SINHALA) "තවම Like කළ වීඩියෝ නැත" else "No liked videos yet"
                                ProfileTab.SAVED -> if (language == AppLanguage.SINHALA) "තවම Save කළ වීඩියෝ නැත" else "No saved videos yet"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 13.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(currentList, key = { it.id }) { video ->
                ProfileVideoItemCard(
                    video = video,
                    language = language,
                    onPlay = { viewModel.setSelectedVideoForDetail(video) },
                    onShare = { viewModel.shareVideo(context, video) },
                    onDelete = if (video.isUserCreated) { { viewModel.deleteVideo(video.id) } } else null,
                    onRemix = { viewModel.remixPrompt(video) }
                )
            }
        }

        // Reset Feed helper at the bottom
        item {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { viewModel.resetFeedData() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_feed_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (language == AppLanguage.SINHALA) "දත්ත නැවත පිහිටුවන්න (Reset Demo Feed)" else "Reset Demo Feed Data",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileVideoItemCard(
    video: VideoEntity,
    language: AppLanguage,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onDelete: (() -> Unit)?,
    onRemix: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceElevated
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Player
            VideoPlayerView(
                video = video,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                autoPlay = false,
                showControls = true,
                compact = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = video.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = video.prompt,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${video.style.titleEn} • ${video.durationSec}s • ❤️ ${video.likesCount}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryCyan,
                        fontSize = 10.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (onDelete != null) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onRemix,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Remix",
                            tint = PrimaryVioletLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
