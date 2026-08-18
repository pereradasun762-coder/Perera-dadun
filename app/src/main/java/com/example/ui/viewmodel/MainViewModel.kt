package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.AspectRatioOption
import com.example.data.model.AudioMood
import com.example.data.model.CommentEntity
import com.example.data.model.L10n
import com.example.data.model.PromptInspirations
import com.example.data.model.UserProfileEntity
import com.example.data.model.VideoEntity
import com.example.data.model.VideoStyle
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GenerationState {
    data object Idle : GenerationState
    data class Generating(
        val progress: Float,
        val stageText: String,
        val stageIndex: Int
    ) : GenerationState
    data class Success(val video: VideoEntity) : GenerationState
    data class Error(val message: String) : GenerationState
}

enum class NavigationTab {
    HOME,
    FEED,
    PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VideoRepository

    init {
        val db = AppDatabase.getDatabase(application.applicationContext, viewModelScope)
        repository = VideoRepository(application.applicationContext, db.videoDao(), db.userProfileDao(), db.commentDao())
        viewModelScope.launch(Dispatchers.IO) {
            repository.initDefaultDataIfNeeded()
        }
    }

    val allVideos: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userVideos: StateFlow<List<VideoEntity>> = repository.userVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedVideos: StateFlow<List<VideoEntity>> = repository.likedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedVideos: StateFlow<List<VideoEntity>> = repository.savedVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation & UI State
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    // Prompt & Generation Controls
    private val _promptText = MutableStateFlow("")
    val promptText: StateFlow<String> = _promptText.asStateFlow()

    private val _selectedStyle = MutableStateFlow(VideoStyle.CINEMATIC)
    val selectedStyle: StateFlow<VideoStyle> = _selectedStyle.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow(AspectRatioOption.PORTRAIT_9_16)
    val selectedAspectRatio: StateFlow<AspectRatioOption> = _selectedAspectRatio.asStateFlow()

    private val _selectedDuration = MutableStateFlow(10)
    val selectedDuration: StateFlow<Int> = _selectedDuration.asStateFlow()

    private val _selectedAudioMood = MutableStateFlow(AudioMood.SYNTHWAVE)
    val selectedAudioMood: StateFlow<AudioMood> = _selectedAudioMood.asStateFlow()

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _isEnhancingPrompt = MutableStateFlow(false)
    val isEnhancingPrompt: StateFlow<Boolean> = _isEnhancingPrompt.asStateFlow()

    // Active preview / Fullscreen playback modal
    private val _selectedVideoForDetail = MutableStateFlow<VideoEntity?>(null)
    val selectedVideoForDetail: StateFlow<VideoEntity?> = _selectedVideoForDetail.asStateFlow()

    // Feed Filters & Search
    private val _feedCategory = MutableStateFlow("All")
    val feedCategory: StateFlow<String> = _feedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active Comments Drawer
    private val _commentingVideoId = MutableStateFlow<Long?>(null)
    val commentingVideoId: StateFlow<Long?> = _commentingVideoId.asStateFlow()

    private val _activeComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val activeComments: StateFlow<List<CommentEntity>> = _activeComments.asStateFlow()

    // Snackbar / Toast message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Out of credits dialog
    private val _showOutOfCreditsDialog = MutableStateFlow(false)
    val showOutOfCreditsDialog: StateFlow<Boolean> = _showOutOfCreditsDialog.asStateFlow()

    // Rewarded ad simulation state
    private val _isWatchingAd = MutableStateFlow(false)
    val isWatchingAd: StateFlow<Boolean> = _isWatchingAd.asStateFlow()

    private val _adWatchProgress = MutableStateFlow(0f)
    val adWatchProgress: StateFlow<Float> = _adWatchProgress.asStateFlow()

    // Language
    val currentLanguage: AppLanguage
        get() = userProfile.value?.language ?: AppLanguage.ENGLISH

    fun setTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setPromptText(text: String) {
        _promptText.value = text
    }

    fun setStyle(style: VideoStyle) {
        _selectedStyle.value = style
    }

    fun setAspectRatio(ratio: AspectRatioOption) {
        _selectedAspectRatio.value = ratio
    }

    fun setDuration(duration: Int) {
        _selectedDuration.value = duration
    }

    fun setAudioMood(mood: AudioMood) {
        _selectedAudioMood.value = mood
    }

    fun setFeedCategory(category: String) {
        _feedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedVideoForDetail(video: VideoEntity?) {
        _selectedVideoForDetail.value = video
        if (video != null) {
            viewModelScope.launch {
                repository.incrementViews(video.id)
            }
        }
    }

    fun dismissToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun dismissOutOfCreditsDialog() {
        _showOutOfCreditsDialog.value = false
    }

    fun applyPromptInspiration(id: String) {
        val item = PromptInspirations.items.find { it.id == id } ?: return
        val lang = currentLanguage
        _promptText.value = if (lang == AppLanguage.SINHALA) item.promptSi else item.promptEn
        _selectedStyle.value = item.style
        _selectedAudioMood.value = item.audioMood
        showToast(if (lang == AppLanguage.SINHALA) "Prompt එක යොදන ලදී!" else "Inspiration loaded!")
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val newLang = if (currentLanguage == AppLanguage.ENGLISH) AppLanguage.SINHALA else AppLanguage.ENGLISH
            repository.setLanguage(newLang)
            val msg = if (newLang == AppLanguage.SINHALA) "භාෂාව සිංහලට මාරු කරන ලදී 🇱🇰" else "Language switched to English 🇺🇸"
            showToast(msg)
        }
    }

    fun enhancePrompt() {
        val current = _promptText.value.trim()
        if (current.isBlank()) {
            showToast(L10n.getString("prompt_placeholder", currentLanguage))
            return
        }
        viewModelScope.launch {
            _isEnhancingPrompt.value = true
            val enhanced = repository.enhancePrompt(current, _selectedStyle.value)
            _promptText.value = enhanced
            _isEnhancingPrompt.value = false
            showToast(L10n.getString("enhanced_prompt", currentLanguage))
        }
    }

    fun generateVideo() {
        val prompt = _promptText.value.trim()
        val lang = currentLanguage
        if (prompt.isBlank()) {
            showToast(L10n.getString("prompt_placeholder", lang))
            return
        }
        if (prompt.length < 5) {
            showToast(L10n.getString("prompt_too_short", lang))
            return
        }

        val credits = userProfile.value?.creditsRemaining ?: 0
        if (credits <= 0) {
            _showOutOfCreditsDialog.value = true
            return
        }

        viewModelScope.launch {
            _generationState.value = GenerationState.Generating(
                progress = 0.08f,
                stageText = if (lang == AppLanguage.SINHALA) "Google Veo ආකෘතිය සූදානම් කරමින්..." else "Connecting to Google Veo...",
                stageIndex = 0
            )

            val result = repository.generateVideo(
                prompt = prompt,
                style = _selectedStyle.value,
                aspectRatio = _selectedAspectRatio.value,
                durationSec = _selectedDuration.value,
                audioMood = _selectedAudioMood.value,
                language = lang,
                onProgressUpdate = { progress, stageText, stageIndex ->
                    _generationState.value = GenerationState.Generating(progress, stageText, stageIndex)
                }
            )

            result.onSuccess { generatedVideo ->
                _generationState.value = GenerationState.Success(generatedVideo)
                _selectedVideoForDetail.value = generatedVideo
                showToast(L10n.getString("video_ready", lang))
            }.onFailure { error ->
                val errorMsg = error.localizedMessage ?: error.message ?: "Video generation failed"
                _generationState.value = GenerationState.Error(errorMsg)
            }
        }
    }

    fun dismissGenerationState() {
        _generationState.value = GenerationState.Idle
    }

    fun toggleLike(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleLike(video)
            // Update selected detail video if open
            if (_selectedVideoForDetail.value?.id == video.id) {
                _selectedVideoForDetail.value = _selectedVideoForDetail.value?.copy(
                    isLiked = !video.isLiked,
                    likesCount = video.likesCount + if (!video.isLiked) 1 else -1
                )
            }
        }
    }

    fun toggleSave(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleSave(video)
            if (_selectedVideoForDetail.value?.id == video.id) {
                _selectedVideoForDetail.value = _selectedVideoForDetail.value?.copy(
                    isSaved = !video.isSaved
                )
            }
            val msg = if (!video.isSaved) {
                L10n.getString("saved", currentLanguage)
            } else {
                if (currentLanguage == AppLanguage.SINHALA) "සේව් කිරීම ඉවත් කරන ලදී" else "Removed from saved"
            }
            showToast(msg)
        }
    }

    fun deleteVideo(videoId: Long) {
        viewModelScope.launch {
            repository.deleteVideo(videoId)
            if (_selectedVideoForDetail.value?.id == videoId) {
                _selectedVideoForDetail.value = null
            }
            showToast(if (currentLanguage == AppLanguage.SINHALA) "වීඩියෝව මකා දමන ලදී" else "Video deleted")
        }
    }

    fun claimDailyCredits() {
        viewModelScope.launch {
            repository.addCredits(3)
            _showOutOfCreditsDialog.value = false
            showToast(L10n.getString("bonus_claimed", currentLanguage))
        }
    }

    fun watchRewardedAdSimulation() {
        viewModelScope.launch {
            _isWatchingAd.value = true
            _adWatchProgress.value = 0f
            for (i in 1..10) {
                delay(300)
                _adWatchProgress.value = i / 10f
            }
            repository.addCredits(2)
            _isWatchingAd.value = false
            _showOutOfCreditsDialog.value = false
            showToast(L10n.getString("ad_bonus_claimed", currentLanguage))
        }
    }

    fun openComments(videoId: Long) {
        _commentingVideoId.value = videoId
        viewModelScope.launch {
            repository.getCommentsForVideo(videoId).collect { comments ->
                _activeComments.value = comments
            }
        }
    }

    fun closeComments() {
        _commentingVideoId.value = null
        _activeComments.value = emptyList()
    }

    fun addComment(text: String) {
        val vid = _commentingVideoId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.postComment(vid, text.trim())
        }
    }

    fun shareVideo(context: Context, video: VideoEntity) {
        val prefix = L10n.getString("share_text_prefix", currentLanguage)
        val shareBody = "$prefix🎬 \"${video.title}\"\n\n✨ Prompt: ${video.prompt}\n\nCreated with Kattiya AI Video App (කට්ටිය AI වීඩියෝ)"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareBody)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Kattiya AI Video")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun remixPrompt(video: VideoEntity) {
        _promptText.value = video.prompt
        _selectedStyle.value = video.style
        _selectedAudioMood.value = video.audioMood
        _currentTab.value = NavigationTab.HOME
        _selectedVideoForDetail.value = null
        showToast(if (currentLanguage == AppLanguage.SINHALA) "Prompt එක සාදනය සඳහා යොදන ලදී!" else "Prompt loaded into studio!")
    }

    fun resetFeedData() {
        viewModelScope.launch {
            repository.resetFeedData()
            showToast(if (currentLanguage == AppLanguage.SINHALA) "දත්ත නැවත මුල් තත්වයට පත් කරන ලදී" else "Feed reset to default!")
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    return MainViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
