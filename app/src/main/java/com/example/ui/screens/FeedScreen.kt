package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.L10n
import com.example.data.model.VideoEntity
import com.example.ui.components.CategoryChips
import com.example.ui.components.VideoPlayerView
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
fun FeedScreen(
    viewModel: MainViewModel,
    videos: List<VideoEntity>,
    selectedCategory: String,
    searchQuery: String,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Filter videos by category and search
    val filteredVideos = remember(videos, selectedCategory, searchQuery) {
        videos.filter { video ->
            val matchesCategory = when (selectedCategory.lowercase()) {
                "all" -> true
                "trending" -> video.tags.contains("Trending", ignoreCase = true) || video.likesCount > 1000
                "sri lanka 🇱🇰" -> video.tags.contains("Sri Lanka", ignoreCase = true) || video.prompt.contains("Sri Lanka", ignoreCase = true)
                "sci-fi" -> video.tags.contains("Sci-Fi", ignoreCase = true) || video.styleId == "cyberpunk"
                "nature" -> video.tags.contains("Nature", ignoreCase = true) || video.styleId == "wildlife"
                "anime" -> video.tags.contains("Anime", ignoreCase = true) || video.styleId == "anime"
                else -> video.tags.contains(selectedCategory, ignoreCase = true)
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                video.title.contains(searchQuery, ignoreCase = true) ||
                        video.prompt.contains(searchQuery, ignoreCase = true) ||
                        video.tags.contains(searchQuery, ignoreCase = true) ||
                        video.creatorName.contains(searchQuery, ignoreCase = true)
            }

            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // --- Feed Header & Search ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = L10n.getString("community", language),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = if (language == AppLanguage.SINHALA) "ප්‍රජාව විසින් සාදන ලද AI වීඩියෝ" else "Trending AI creations from community",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SecondaryCyan,
                            fontSize = 12.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1F1A3A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Text(
                        text = "${filteredVideos.size} ${if (language == AppLanguage.SINHALA) "වීඩියෝ" else "Videos"}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = L10n.getString("search_hint", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("feed_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = PrimaryViolet,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedContainerColor = Color(0xFF141224),
                    unfocusedContainerColor = Color(0xFF141224)
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true
            )
        }

        // --- Category Filters ---
        CategoryChips(
            selectedCategory = selectedCategory,
            language = language,
            onSelectCategory = { viewModel.setFeedCategory(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- Videos Feed List ---
        if (filteredVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔍", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = L10n.getString("no_videos_found", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag("community_feed_list"),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp)
            ) {
                items(filteredVideos, key = { it.id }) { video ->
                    FeedVideoCard(
                        video = video,
                        language = language,
                        onLike = { viewModel.toggleLike(video) },
                        onSave = { viewModel.toggleSave(video) },
                        onShare = { viewModel.shareVideo(context, video) },
                        onComment = { viewModel.openComments(video.id) },
                        onRemix = { viewModel.remixPrompt(video) },
                        onDetail = { viewModel.setSelectedVideoForDetail(video) }
                    )
                }
            }
        }
    }
}

@Composable
fun FeedVideoCard(
    video: VideoEntity,
    language: AppLanguage,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onComment: () -> Unit,
    onRemix: () -> Unit,
    onDetail: () -> Unit
) {
    var isExpandedPrompt by remember { mutableStateOf(false) }

    // Heart bounce animation
    val heartScale by animateFloatAsState(
        targetValue = if (video.isLiked) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heart_bounce"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
            .testTag("feed_card_${video.id}"),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurfaceElevated,
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // --- Creator Info Header ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF262046)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = video.creatorAvatar, fontSize = 18.sp)
                    }

                    Column {
                        Text(
                            text = video.creatorName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = video.style.titleEn + " • " + video.aspectRatio.ratioText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SecondaryCyan,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Tag badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryViolet.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = video.tags.split(",").firstOrNull() ?: "AI Video",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryVioletLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- Video Canvas / Player View ---
            VideoPlayerView(
                video = video,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                autoPlay = false,
                showControls = true,
                compact = false
            )

            Spacer(modifier = Modifier.height(10.dp))

            // --- Title & Prompt ---
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = video.prompt,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                maxLines = if (isExpandedPrompt) 10 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isExpandedPrompt = !isExpandedPrompt }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Interactive Action Bar (Like, Comment, Share, Save, Remix) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Actions: Like & Comment
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Like Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onLike() }
                            .padding(4.dp)
                            .testTag("like_button_${video.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (video.isLiked) Color(0xFFEF4444) else TextSecondary,
                            modifier = Modifier
                                .size(20.dp)
                                .scale(heartScale)
                        )
                        Text(
                            text = "${video.likesCount}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (video.isLiked) Color(0xFFEF4444) else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    // Comments Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onComment() }
                            .padding(4.dp)
                            .testTag("comment_button_${video.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = L10n.getString("comments", language),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Views count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${video.viewsCount}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Right Actions: Share, Save, Remix
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Share Button
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("share_button_${video.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Save Bookmark Button
                    IconButton(
                        onClick = onSave,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("save_button_${video.id}")
                    ) {
                        Icon(
                            imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (video.isSaved) PrimaryVioletLight else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Remix Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onRemix() }
                            .testTag("remix_button_${video.id}"),
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryViolet.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Remix",
                                tint = PrimaryVioletLight,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = L10n.getString("remix", language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PrimaryVioletLight,
                                    fontWeight = FontWeight.Bold,
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
