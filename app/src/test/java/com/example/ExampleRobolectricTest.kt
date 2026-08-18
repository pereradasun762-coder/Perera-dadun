package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PrepopulatedData
import com.example.data.model.AppLanguage
import com.example.data.model.AspectRatioOption
import com.example.data.model.AudioMood
import com.example.data.model.L10n
import com.example.data.model.PromptInspirations
import com.example.data.model.VideoEntity
import com.example.data.model.VideoStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kattiya AI Video", appName)
    }

    @Test
    fun `verify localization dictionary`() {
        val enTitle = L10n.getString("app_title", AppLanguage.ENGLISH)
        val siTitle = L10n.getString("app_title", AppLanguage.SINHALA)
        assertEquals("Kattiya AI Video", enTitle)
        assertTrue(siTitle.contains("කට්ටිය"))

        val enGenerate = L10n.getString("generate_video", AppLanguage.ENGLISH)
        val siGenerate = L10n.getString("generate_video", AppLanguage.SINHALA)
        assertTrue(enGenerate.contains("Generate"))
        assertTrue(siGenerate.contains("සාදන්න"))

        val enError = L10n.getString("prompt_too_short", AppLanguage.ENGLISH)
        val siError = L10n.getString("prompt_too_short", AppLanguage.SINHALA)
        assertTrue(enError.contains("at least 5 characters"))
        assertTrue(siError.contains("අකුරු 5"))
    }

    @Test
    fun `verify prompt inspirations list`() {
        assertTrue(PromptInspirations.items.isNotEmpty())
        val first = PromptInspirations.items.first()
        assertTrue(first.promptEn.isNotBlank())
        assertTrue(first.promptSi.isNotBlank())
        assertNotNull(first.style)
    }

    @Test
    fun `verify video styles and aspect ratios`() {
        assertTrue(VideoStyle.entries.size >= 5)
        assertTrue(AspectRatioOption.entries.size >= 3)
        assertTrue(AudioMood.entries.size >= 4)
    }

    @Test
    fun `verify prepopulated feed items`() {
        val initialVideos = PrepopulatedData.initialVideos
        assertTrue(initialVideos.isNotEmpty())
        assertTrue(initialVideos.any { it.tags.contains("Sri Lanka") })
        assertTrue(initialVideos.all { it.title.isNotBlank() && it.prompt.isNotBlank() })
    }

    @Test
    fun `verify video entity model with video file path and url`() {
        val video = VideoEntity(
            title = "Test AI Video",
            prompt = "A glowing cyber temple at sunset",
            styleId = "cyberpunk",
            aspectRatioName = "PORTRAIT_9_16",
            durationSec = 10,
            audioMoodId = "synth",
            thumbnailResName = "thumb_sigiriya_cyber",
            videoSceneType = "sigiriya",
            creatorName = "TestCreator",
            creatorAvatar = "⚡",
            videoUrl = "https://example.com/video.mp4",
            videoFilePath = "/data/user/0/com.aistudio.kattiya/files/video.mp4"
        )
        assertEquals("Test AI Video", video.title)
        assertEquals(VideoStyle.CYBERPUNK, video.style)
        assertEquals(AspectRatioOption.PORTRAIT_9_16, video.aspectRatio)
        assertEquals("/data/user/0/com.aistudio.kattiya/files/video.mp4", video.videoFilePath)
    }
}
