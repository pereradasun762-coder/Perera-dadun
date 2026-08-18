package com.example

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.ui.components.CategoryChips
import com.example.ui.components.CreditBadge
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun credit_badge_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme {
                CreditBadge(
                    credits = 5,
                    language = AppLanguage.ENGLISH,
                    onClick = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/credit_badge.png")
    }

    @Test
    fun category_chips_screenshot() {
        composeTestRule.setContent {
            MyApplicationTheme {
                CategoryChips(
                    selectedCategory = "All",
                    language = AppLanguage.ENGLISH,
                    onSelectCategory = {},
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/category_chips.png")
    }
}
