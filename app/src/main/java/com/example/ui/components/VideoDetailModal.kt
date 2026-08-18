package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.data.model.L10n
import com.example.data.model.VideoEntity
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoDetailModal(
    video: VideoEntity,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onToggleLike: (VideoEntity) -> Unit,
    onToggleSave: (VideoEntity) -> Unit,
    onShare: (VideoEntity) -> Unit,
    onRemix: (VideoEntity) -> Unit,
    onOpenComments: (Long) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6080712))
                .padding(top = 28.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = DarkSurfaceElevated
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    // Header with Close & Style Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PrimaryViolet.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryVioletLight.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = if (language == AppLanguage.SINHALA) video.style.titleSi else video.style.titleEn,
                                    color = PrimaryVioletLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SecondaryCyan.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = video.aspectRatio.ratioText,
                                    color = SecondaryCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E1A38))
                                .testTag("close_detail_modal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Video Player Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        VideoPlayerView(
                            video = video,
                            modifier = Modifier.fillMaxWidth(),
                            autoPlay = true,
                            showControls = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title & Creator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "By ${video.creatorName} • ${video.viewsCount} views",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Interactive Action Bar (Like, Save, Comments, Share, Remix)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF161328))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleLike(video) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("detail_like_button")
                        ) {
                            Icon(
                                imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (video.isLiked) Color(0xFFEF4444) else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "${video.likesCount}",
                                fontSize = 11.sp,
                                color = if (video.isLiked) Color(0xFFEF4444) else TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Save
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleSave(video) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("detail_save_button")
                        ) {
                            Icon(
                                imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (video.isSaved) AccentAmber else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = if (video.isSaved) "Saved" else "Save",
                                fontSize = 11.sp,
                                color = if (video.isSaved) AccentAmber else TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Comments
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onOpenComments(video.id) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("detail_comments_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = SecondaryCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = if (language == AppLanguage.SINHALA) "අදහස්" else "Chat",
                                fontSize = 11.sp,
                                color = SecondaryCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Share
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onShare(video) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("detail_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Share",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Remix
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onRemix(video) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("detail_remix_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Remix",
                                tint = PrimaryVioletLight,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Remix",
                                fontSize = 11.sp,
                                color = PrimaryVioletLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Prompt Box with Copy Action
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF131024),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == AppLanguage.SINHALA) "AI වීඩියෝ විස්තරය (Prompt)" else "Generation Prompt",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(video.prompt))
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryViolet.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (language == AppLanguage.SINHALA) "පිටපත් කරන්න" else "Copy Prompt",
                                        color = PrimaryVioletLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = video.prompt,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Technical specs pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF18152B)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Music Mood", fontSize = 10.sp, color = TextSecondary)
                                Text(
                                    text = if (language == AppLanguage.SINHALA) video.audioMood.labelSi else video.audioMood.labelEn,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF18152B)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Duration", fontSize = 10.sp, color = TextSecondary)
                                Text("${video.durationSec}s @ 60fps", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF18152B)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Resolution", fontSize = 10.sp, color = TextSecondary)
                                Text("4K UHD Ultra", fontSize = 12.sp, color = SecondaryCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (video.isUserCreated && onDelete != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                onDelete(video.id)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("delete_user_video_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.SINHALA) "වීඩියෝව මකා දමන්න" else "Delete My Video",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
