package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.L10n
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan

data class FeedCategoryItem(val id: String, val labelKey: String, val emoji: String)

@Composable
fun CategoryChips(
    selectedCategory: String,
    language: AppLanguage,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        FeedCategoryItem("All", "all", "✨"),
        FeedCategoryItem("Trending", "trending", "🔥"),
        FeedCategoryItem("Sri Lanka 🇱🇰", "sri_lankan_tag", "🇱🇰"),
        FeedCategoryItem("Sci-Fi", "sci_fi", "⚡"),
        FeedCategoryItem("Nature", "nature", "🐅"),
        FeedCategoryItem("Anime", "anime_tag", "🌸")
    )

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { item ->
            val isSelected = selectedCategory.equals(item.id, ignoreCase = true)
            val title = L10n.getString(item.labelKey, language)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectCategory(item.id) }
                    .background(
                        if (isSelected) {
                            Brush.horizontalGradient(listOf(PrimaryViolet, SecondaryCyan))
                        } else {
                            Brush.linearGradient(listOf(DarkSurfaceElevated, DarkSurfaceElevated))
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else DarkSurfaceBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("category_chip_${item.id}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = item.emoji, fontSize = 13.sp)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}
