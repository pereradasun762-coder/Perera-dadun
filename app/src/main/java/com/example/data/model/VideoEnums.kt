package com.example.data.model

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ENGLISH("en", "English", "🇺🇸"),
    SINHALA("si", "සිංහල", "🇱🇰")
}

enum class VideoStyle(
    val id: String,
    val titleEn: String,
    val titleSi: String,
    val emoji: String,
    val promptSuffix: String
) {
    CINEMATIC("cinematic", "Cinematic 8K", "සිනමාත්මක 8K", "🎬", "cinematic lighting, photorealistic, 8k resolution, film grain, dramatic atmosphere"),
    CYBERPUNK("cyberpunk", "Cyberpunk Neon", "සයිබර්පන්ක් නියොන්", "⚡", "cyberpunk neon glow, volumetric lights, futuristic sci-fi, reflective wet ground, 8k"),
    ANIME("anime", "Anime Ghibli", "ඇනිමෙ ශෛලිය", "🌸", "Studio Ghibli aesthetic, anime landscape, hand-drawn vibrant colors, dreamy clouds"),
    WILDLIFE("wildlife", "Nature & Wildlife", "ස්වභාවධර්ම", "🐅", "National geographic 8k documentary, ultra sharp focus, golden hour lighting, wild natural habitat"),
    REALISTIC_3D("3d_realistic", "3D CGI Unreal", "3D යථාර්ථවාදී", "🎮", "Unreal Engine 5 render, ray tracing, ultra detailed 3d textures, dynamic lighting"),
    SRI_LANKAN("sri_lankan", "Sri Lankan Heritage", "ශ්‍රී ලාංකීය උරුමය", "🇱🇰", "rich Sri Lankan traditional culture, ancient king era, tropical vibrant colors, majestic architecture"),
    VINTAGE("vintage", "Retro Film 90s", "පැරණි චිත්‍රපට", "📼", "90s retro film look, warm film stock, nostalgic tones, vintage aesthetic")
}

enum class AspectRatioOption(val label: String, val ratioText: String, val widthWeight: Float, val heightWeight: Float) {
    PORTRAIT_9_16("9:16 (Reels/TikTok)", "9:16", 9f, 16f),
    LANDSCAPE_16_9("16:9 (Cinema/TV)", "16:9", 16f, 9f),
    SQUARE_1_1("1:1 (Feed)", "1:1", 1f, 1f)
}

enum class AudioMood(val id: String, val labelEn: String, val labelSi: String, val emoji: String) {
    SYNTHWAVE("synth", "Cyber Synthwave", "සයිබර් සින්ත්", "🎹"),
    CINEMATIC_ORCHESTRA("orchestra", "Cinematic Epic", "සිනමා වාද්‍ය", "🎻"),
    LOFI_BEATS("lofi", "Chill Lofi", "චිල් ලෝෆයි", "☕"),
    SRI_LANKAN_DRUMS("drums", "Acoustic Folk / Drums", "දේශීය බෙර වාදන", "🥁"),
    NATURE_AMBIENCE("nature", "Nature & Rain", "ස්වභාවික ශබ්ද", "🌿")
}
