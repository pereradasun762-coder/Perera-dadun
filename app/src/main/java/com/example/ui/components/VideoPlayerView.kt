package com.example.ui.components

import android.net.Uri
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.AspectRatioOption
import com.example.data.model.VideoEntity
import com.example.data.model.VideoStyle
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun VideoPlayerView(
    video: VideoEntity,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    showControls: Boolean = true,
    compact: Boolean = false,
    onFullScreenClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(autoPlay) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var isMuted by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var areControlsVisible by remember { mutableStateOf(true) }

    val hasRealVideoFile = remember(video.videoFilePath, video.videoUrl) {
        (video.videoFilePath.isNotBlank() && File(video.videoFilePath).exists()) ||
                (video.videoUrl.isNotBlank() && (video.videoUrl.startsWith("http") || video.videoUrl.startsWith("file://")))
    }

    val durationSec = remember(video.durationSec) { if (video.durationSec > 0) video.durationSec else 10 }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    // Playback ticker coroutine for animated canvas mode or synced progress
    LaunchedEffect(isPlaying, playbackSpeed, durationSec, hasRealVideoFile) {
        if (isPlaying && !hasRealVideoFile) {
            val stepMs = 50L
            val stepFraction = (stepMs / 1000f * playbackSpeed) / durationSec
            while (isPlaying) {
                delay(stepMs)
                currentProgress += stepFraction
                if (currentProgress >= 1f) {
                    currentProgress = 0f // loop playback
                }
            }
        }
    }

    // Auto-hide controls after 3.5 seconds
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying && !compact) {
            delay(3500)
            areControlsVisible = false
        }
    }

    // Determine current scene index (0, 1, or 2)
    val currentSceneIndex = (currentProgress * 3).toInt().coerceIn(0, 2)
    val currentSceneDesc = when (currentSceneIndex) {
        0 -> if (video.scene1Desc.isNotBlank()) video.scene1Desc else "Scene 1: Establishing Shot"
        1 -> if (video.scene2Desc.isNotBlank()) video.scene2Desc else "Scene 2: Dynamic Action"
        else -> if (video.scene3Desc.isNotBlank()) video.scene3Desc else "Scene 3: Cinematic Climax"
    }

    // Camera Motion Ken Burns Pan & Zoom factor (for canvas mode)
    val cameraScale = 1.05f + 0.12f * sin(currentProgress * Math.PI.toFloat())
    val cameraOffsetX = sin(currentProgress * 2 * Math.PI.toFloat()) * 20f
    val cameraOffsetY = (currentProgress - 0.5f) * 15f

    // Resolve resource ID for thumbnail
    val thumbResId = remember(video.thumbnailResName) {
        val id = context.resources.getIdentifier(video.thumbnailResName, "drawable", context.packageName)
        if (id != 0) id else null
    }

    // Color gradient fallback based on style
    val gradientBrush = remember(video.styleId) {
        when (video.style) {
            VideoStyle.CYBERPUNK -> Brush.verticalGradient(listOf(Color(0xFF1E1035), Color(0xFF0D0B1C), Color(0xFF06B6D4)))
            VideoStyle.ANIME -> Brush.verticalGradient(listOf(Color(0xFF2E1065), Color(0xFF064E3B), Color(0xFFF472B6)))
            VideoStyle.WILDLIFE -> Brush.verticalGradient(listOf(Color(0xFF451A03), Color(0xFF1C1917), Color(0xFF78350F)))
            VideoStyle.SRI_LANKAN -> Brush.verticalGradient(listOf(Color(0xFF881337), Color(0xFF1C1917), Color(0xFFF59E0B)))
            else -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81)))
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (compact) 12.dp else 16.dp))
            .background(DarkBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                areControlsVisible = !areControlsVisible
            }
            .testTag("video_player_container_${video.id}"),
        contentAlignment = Alignment.Center
    ) {
        if (hasRealVideoFile) {
            // Real Video Playback Engine via AndroidView (VideoView)
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        val videoUri = if (video.videoFilePath.isNotBlank()) {
                            Uri.fromFile(File(video.videoFilePath))
                        } else {
                            Uri.parse(video.videoUrl)
                        }
                        setVideoURI(videoUri)
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            if (isMuted) mp.setVolume(0f, 0f) else mp.setVolume(1f, 1f)
                            if (autoPlay) {
                                start()
                                isPlaying = true
                            }
                        }
                        setOnErrorListener { _, what, extra ->
                            android.util.Log.w("VideoPlayerView", "VideoView playback error: what=$what, extra=$extra")
                            true // Handled gracefully to prevent OS popup
                        }
                        setOnCompletionListener {
                            if (isPlaying) start()
                        }
                        videoViewRef = this
                    }
                },
                update = { vv ->
                    if (isPlaying && !vv.isPlaying) {
                        vv.start()
                    } else if (!isPlaying && vv.isPlaying) {
                        vv.pause()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            DisposableEffect(Unit) {
                onDispose {
                    videoViewRef?.stopPlayback()
                }
            }
        } else {
            // Animated Canvas Scene Layer (Fallback / Prepopulated Feed)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = cameraScale
                        scaleY = cameraScale
                        translationX = cameraOffsetX
                        translationY = cameraOffsetY
                    }
            ) {
                if (thumbResId != null) {
                    Image(
                        painter = painterResource(id = thumbResId),
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(gradientBrush)
                    )
                }
            }

            // Cinematic Particle / Atmosphere Layer
            CinematicParticleOverlay(
                style = video.style,
                isPlaying = isPlaying,
                progress = currentProgress
            )
        }

        // Ambient Vignette Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Live Audio Equalizer Waveform
        if (!isMuted && isPlaying) {
            AudioWaveformIndicator(
                audioMood = video.audioMood.labelEn,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )
        }

        // Scene / Resolution Indicator Badge
        if (!compact && isPlaying) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (hasRealVideoFile) SecondaryCyan else PrimaryViolet)
                    )
                    Text(
                        text = if (hasRealVideoFile) "Google Veo • 720p HD" else "Scene ${currentSceneIndex + 1}/3 • ${video.durationSec}s",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Center Play/Pause Indicator (Tap Feedback)
        AnimatedVisibility(
            visible = areControlsVisible || !isPlaying,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 44.dp else 56.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable {
                        isPlaying = !isPlaying
                        videoViewRef?.let { vv ->
                            if (isPlaying) vv.start() else vv.pause()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(if (compact) 26.dp else 34.dp)
                )
            }
        }

        // Bottom Playback Controls & Scrubber
        AnimatedVisibility(
            visible = (areControlsVisible || !isPlaying) && showControls,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                        )
                    )
                    .padding(horizontal = if (compact) 8.dp else 14.dp, vertical = if (compact) 6.dp else 10.dp)
            ) {
                // Live Scene Caption
                if (!compact && currentSceneDesc.isNotBlank() && !hasRealVideoFile) {
                    Text(
                        text = currentSceneDesc,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        maxLines = 1,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                // Scrubber Seekbar
                Slider(
                    value = currentProgress,
                    onValueChange = {
                        currentProgress = it
                        isPlaying = false
                        videoViewRef?.let { vv ->
                            val seekMs = (it * vv.duration).toInt()
                            vv.seekTo(seekMs)
                        }
                    },
                    onValueChangeFinished = {
                        isPlaying = true
                        videoViewRef?.start()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryViolet,
                        activeTrackColor = SecondaryCyan,
                        inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                    )
                )

                // Timestamp & Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentSec = (currentProgress * durationSec).toInt()
                    Text(
                        text = String.format("%02d:%02d / %02d:%02d", currentSec / 60, currentSec % 60, durationSec / 60, durationSec % 60),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Speed Chip
                        if (!compact) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.clickable {
                                    playbackSpeed = when (playbackSpeed) {
                                        1.0f -> 1.5f
                                        1.5f -> 2.0f
                                        2.0f -> 0.5f
                                        else -> 1.0f
                                    }
                                }
                            ) {
                                Text(
                                    text = "${playbackSpeed}x",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Mute / Unmute Button
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Replay Button
                        IconButton(
                            onClick = {
                                currentProgress = 0f
                                isPlaying = true
                                videoViewRef?.let { vv ->
                                    vv.seekTo(0)
                                    vv.start()
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Replay",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AudioWaveformIndicator(
    audioMood: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(290, easing = LinearEasing), RepeatMode.Reverse),
        label = "b3"
    )
    val bar4 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(480, easing = LinearEasing), RepeatMode.Reverse),
        label = "b4"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.65f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = SecondaryCyan,
                modifier = Modifier.size(14.dp)
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.height(12.dp)
            ) {
                Box(modifier = Modifier.width(2.dp).height((12 * bar1).dp).background(SecondaryCyan, RoundedCornerShape(1.dp)))
                Box(modifier = Modifier.width(2.dp).height((12 * bar2).dp).background(PrimaryViolet, RoundedCornerShape(1.dp)))
                Box(modifier = Modifier.width(2.dp).height((12 * bar3).dp).background(SecondaryCyan, RoundedCornerShape(1.dp)))
                Box(modifier = Modifier.width(2.dp).height((12 * bar4).dp).background(PrimaryViolet, RoundedCornerShape(1.dp)))
            }
        }
    }
}

@Composable
fun CinematicParticleOverlay(
    style: VideoStyle,
    isPlaying: Boolean,
    progress: Float
) {
    val particles = remember(style) {
        List(24) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3f + 1f,
                speed = Random.nextFloat() * 0.4f + 0.2f,
                alpha = Random.nextFloat() * 0.7f + 0.3f
            )
        }
    }

    val particleColor = remember(style) {
        when (style) {
            VideoStyle.CYBERPUNK -> SecondaryCyan
            VideoStyle.ANIME -> Color(0xFFFFB6C1)
            VideoStyle.WILDLIFE -> AccentAmber
            VideoStyle.SRI_LANKAN -> Color(0xFFFFD700)
            else -> PrimaryViolet
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        particles.forEach { p ->
            val curY = ((p.y + progress * p.speed) % 1f) * h
            val curX = ((p.x + sin((progress + p.x) * 6f) * 0.05f) % 1f) * w
            drawCircle(
                color = particleColor.copy(alpha = (p.alpha * (0.5f + 0.5f * sin((progress + p.speed) * 10f))).coerceIn(0.1f, 0.9f)),
                radius = p.radius,
                center = Offset(curX, curY)
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val alpha: Float
)
