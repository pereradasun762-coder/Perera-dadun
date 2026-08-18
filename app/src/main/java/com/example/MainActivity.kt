package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppLanguage
import com.example.data.model.L10n
import com.example.data.model.VideoEntity
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.GeneratingVideoModal
import com.example.ui.components.GenerationErrorDialog
import com.example.ui.components.OutOfCreditsDialog
import com.example.ui.components.VideoDetailModal
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GenerationState
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModel.provideFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KattiyaApp(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val tab: NavigationTab,
    val titleKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun KattiyaApp(viewModel: MainViewModel) {
    val context = LocalContext.current

    // Observe ViewModel States
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val promptText by viewModel.promptText.collectAsStateWithLifecycle()
    val selectedStyle by viewModel.selectedStyle.collectAsStateWithLifecycle()
    val selectedAspectRatio by viewModel.selectedAspectRatio.collectAsStateWithLifecycle()
    val selectedDuration by viewModel.selectedDuration.collectAsStateWithLifecycle()
    val selectedAudioMood by viewModel.selectedAudioMood.collectAsStateWithLifecycle()
    val generationState by viewModel.generationState.collectAsStateWithLifecycle()
    val isEnhancing by viewModel.isEnhancingPrompt.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val allVideos by viewModel.allVideos.collectAsStateWithLifecycle()
    val userVideos by viewModel.userVideos.collectAsStateWithLifecycle()
    val likedVideos by viewModel.likedVideos.collectAsStateWithLifecycle()
    val savedVideos by viewModel.savedVideos.collectAsStateWithLifecycle()

    val selectedVideoForDetail by viewModel.selectedVideoForDetail.collectAsStateWithLifecycle()
    val feedCategory by viewModel.feedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val commentingVideoId by viewModel.commentingVideoId.collectAsStateWithLifecycle()
    val activeComments by viewModel.activeComments.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val showOutOfCredits by viewModel.showOutOfCreditsDialog.collectAsStateWithLifecycle()
    val isWatchingAd by viewModel.isWatchingAd.collectAsStateWithLifecycle()
    val adWatchProgress by viewModel.adWatchProgress.collectAsStateWithLifecycle()

    val language = viewModel.currentLanguage

    val navItems = listOf(
        NavItem(
            tab = NavigationTab.HOME,
            titleKey = "tab_home",
            selectedIcon = Icons.Filled.Movie,
            unselectedIcon = Icons.Outlined.Movie,
            testTag = "nav_tab_home"
        ),
        NavItem(
            tab = NavigationTab.FEED,
            titleKey = "tab_feed",
            selectedIcon = Icons.Filled.Explore,
            unselectedIcon = Icons.Outlined.Explore,
            testTag = "nav_tab_feed"
        ),
        NavItem(
            tab = NavigationTab.PROFILE,
            titleKey = "tab_profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            testTag = "nav_tab_profile"
        )
    )

    // Auto-dismiss toast after 3.5 seconds
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3500)
            viewModel.dismissToast()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(DarkSurfaceBorder.copy(alpha = 0.8f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = DarkSurfaceElevated.copy(alpha = 0.96f),
                tonalElevation = 8.dp
            ) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(68.dp),
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentTab == item.tab
                        val title = L10n.getString(item.titleKey, language)

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setTab(item.tab) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.tab == NavigationTab.PROFILE && userVideos.isNotEmpty()) {
                                            Badge(
                                                containerColor = PrimaryVioletLight,
                                                contentColor = Color.Black
                                            ) {
                                                Text(
                                                    text = "${userVideos.size}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SecondaryCyan,
                                selectedTextColor = SecondaryCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = PrimaryViolet.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            // Main Animated Content Switching
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "tab_navigation"
            ) { targetTab ->
                when (targetTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            promptText = promptText,
                            selectedStyle = selectedStyle,
                            selectedAspectRatio = selectedAspectRatio,
                            selectedDuration = selectedDuration,
                            selectedAudioMood = selectedAudioMood,
                            userProfile = userProfile,
                            isEnhancing = isEnhancing,
                            recentVideos = allVideos,
                            onOpenOutOfCredits = { viewModel.claimDailyCredits() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    NavigationTab.FEED -> {
                        FeedScreen(
                            viewModel = viewModel,
                            videos = allVideos,
                            selectedCategory = feedCategory,
                            searchQuery = searchQuery,
                            language = language,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    NavigationTab.PROFILE -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            userProfile = userProfile,
                            userVideos = userVideos,
                            likedVideos = likedVideos,
                            savedVideos = savedVideos,
                            language = language,
                            onOpenOutOfCredits = { viewModel.claimDailyCredits() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Toast / Notification Overlay Banner
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E1A38),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryViolet.copy(alpha = 0.5f)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_toast_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Notification",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = toastMessage.orEmpty(),
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = TextSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { viewModel.dismissToast() }
                        )
                    }
                }
            }

            // Modal: Video Generation In-Progress
            val genState = generationState
            if (genState is GenerationState.Generating) {
                GeneratingVideoModal(
                    progress = genState.progress,
                    stageText = genState.stageText,
                    stageIndex = genState.stageIndex,
                    language = language,
                    onDismiss = { viewModel.dismissGenerationState() }
                )
            }

            // Modal: Video Generation Error
            if (genState is GenerationState.Error) {
                GenerationErrorDialog(
                    errorMessage = genState.message,
                    language = language,
                    onRetry = {
                        viewModel.dismissGenerationState()
                        viewModel.generateVideo()
                    },
                    onDismiss = { viewModel.dismissGenerationState() }
                )
            }

            // Modal: Out of Credits / Rewarded Ad Simulation
            if (showOutOfCredits) {
                OutOfCreditsDialog(
                    language = language,
                    isWatchingAd = isWatchingAd,
                    adWatchProgress = adWatchProgress,
                    onClaimDaily = { viewModel.claimDailyCredits() },
                    onWatchAd = { viewModel.watchRewardedAdSimulation() },
                    onDismiss = { viewModel.dismissOutOfCreditsDialog() }
                )
            }

            // Modal: Comments Bottom Sheet
            if (commentingVideoId != null) {
                CommentBottomSheet(
                    comments = activeComments,
                    language = language,
                    onAddComment = { text -> viewModel.addComment(text) },
                    onDismiss = { viewModel.closeComments() }
                )
            }

            // Modal: Full Video Player & Specs Detail View
            selectedVideoForDetail?.let { detailVideo ->
                VideoDetailModal(
                    video = detailVideo,
                    language = language,
                    onDismiss = { viewModel.setSelectedVideoForDetail(null) },
                    onToggleLike = { v -> viewModel.toggleLike(v) },
                    onToggleSave = { v -> viewModel.toggleSave(v) },
                    onShare = { v -> viewModel.shareVideo(context, v) },
                    onRemix = { v -> viewModel.remixPrompt(v) },
                    onOpenComments = { id -> viewModel.openComments(id) },
                    onDelete = if (detailVideo.isUserCreated) { id -> viewModel.deleteVideo(id) } else null
                )
            }
        }
    }
}
