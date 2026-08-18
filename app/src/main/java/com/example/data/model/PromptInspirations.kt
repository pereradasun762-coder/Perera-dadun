package com.example.data.model

data class PromptInspiration(
    val id: String,
    val titleEn: String,
    val titleSi: String,
    val promptEn: String,
    val promptSi: String,
    val style: VideoStyle,
    val audioMood: AudioMood,
    val emoji: String,
    val category: String
)

object PromptInspirations {
    val items = listOf(
        PromptInspiration(
            id = "sigiriya_cyber",
            titleEn = "Sigiriya 2077 Cyberpunk",
            titleSi = "සීගිරිය 2077 සයිබර්පන්ක්",
            promptEn = "Ancient Sigiriya Lion Rock in futuristic cyberpunk Sri Lanka 2077, glowing neon purple holographic signs, flying sci-fi speeders, dusk rain reflections",
            promptSi = "2077 අනාගත සයිබර්පන්ක් සීගිරිය පර්වතය, දීප්තිමත් දම් පාට නියොන් හොලෝග්‍රෑම් ආලෝක, අහසේ පියාසර කරන රථ වාහන",
            style = VideoStyle.CYBERPUNK,
            audioMood = AudioMood.SYNTHWAVE,
            emoji = "⚡",
            category = "Sri Lanka 🇱🇰"
        ),
        PromptInspiration(
            id = "yala_leopard",
            titleEn = "Yala Leopard at Sunset",
            titleSi = "යාල දිවියා හිරු බැසයන මොහොතේ",
            promptEn = "Cinematic 8K documentary slow motion of a majestic Sri Lankan leopard stalking across granite rocks in Yala National Park during golden hour sunset",
            promptSi = "යාල ජාතික වනෝද්‍යානයේ හිරු බැසයන සන්ධ්‍යාවේ කළුගල් පර්වතයක් මත සිටින තේජාන්විත ශ්‍රී ලාංකීය දිවියාගේ සිනමාත්මක වීඩියෝවක්",
            style = VideoStyle.WILDLIFE,
            audioMood = AudioMood.NATURE_AMBIENCE,
            emoji = "🐅",
            category = "Nature"
        ),
        PromptInspiration(
            id = "colombo_future",
            titleEn = "Futuristic Colombo Lotus Tower",
            titleSi = "අනාගත කොළඹ නෙළුම් කුළුණ",
            promptEn = "Ultra cinematic aerial drone shot of futuristic Colombo metropolis with illuminated Lotus Tower glowing in neon magenta, autonomous yachts in Port City harbor",
            promptSi = "අනාගත කොළඹ නගරයේ මනරම් ඩ්‍රෝන දර්ශනයක්, රෝස සහ නිල් ආලෝකයෙන් බබළන නෙළුම් කුළුණ සහ පෝර්ට් සිටි වරාය",
            style = VideoStyle.CINEMATIC,
            audioMood = AudioMood.CINEMATIC_ORCHESTRA,
            emoji = "🏙️",
            category = "Sci-Fi"
        ),
        PromptInspiration(
            id = "ella_train",
            titleEn = "Nine Arch Train Ghibli Anime",
            titleSi = "ඇල්ල දෙමෝදර දුම්රිය ඇනිමෙ",
            promptEn = "Studio Ghibli style lush green Ella tea plantation with vintage blue train slowly crossing Nine Arch Bridge, misty mountain morning, sakura petals floating",
            promptSi = "ස්ටුඩියෝ ගිබ්ලි ඇනිමෙ ශෛලියෙන් යුත් ඇල්ල තේ වතු මැදින් දෙමෝදර නව ආරුක්කු පාලම මතින් ධාවනය වන නිල් පැහැති දුම්රිය",
            style = VideoStyle.ANIME,
            audioMood = AudioMood.LOFI_BEATS,
            emoji = "🌸",
            category = "Anime"
        ),
        PromptInspiration(
            id = "perahera_dance",
            titleEn = "Kandy Perahera Fire Dance",
            titleSi = "මහනුවර පෙරහැර ගිනි බෝල නැටුම",
            promptEn = "Dramatic slow-motion capture of traditional Sri Lankan Kandyan dancers performing fire acrobatics at night, glowing ember sparks, traditional drums beat",
            promptSi = "මහනුවර ඇසළ පෙරහැරේ රාත්‍රියේ ගිනි බෝල කරකවමින් නර්තනයේ යෙදෙන සම්ප්‍රදායික උඩරට නැට්ටුවන්ගේ අලංකාර දර්ශනයක්",
            style = VideoStyle.SRI_LANKAN,
            audioMood = AudioMood.SRI_LANKAN_DRUMS,
            emoji = "🔥",
            category = "Sri Lanka 🇱🇰"
        ),
        PromptInspiration(
            id = "whale_ocean",
            titleEn = "Mirissa Ocean Blue Whale",
            titleSi = "මිරිස්ස මුහුදේ නිල් තල්මසා",
            promptEn = "Cinematic ocean drone 4K footage of a giant Blue Whale breaching clear turquoise waters near Mirissa coastline with tropical sun rays piercing the sea",
            promptSi = "මිරිස්ස මුහුදු තීරයේ පැහැදිලි නිල්වන් දිය රැළි අතරින් උඩට පනින දැවැන්ත නිල් තල්මසෙකුගේ ඩ්‍රෝන වීඩියෝවක්",
            style = VideoStyle.WILDLIFE,
            audioMood = AudioMood.NATURE_AMBIENCE,
            emoji = "🌊",
            category = "Nature"
        )
    )
}
