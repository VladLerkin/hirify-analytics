package hirify.analytics.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val HirifyYellow = Color(0xFFFFD600)
val HirifyDark = Color(0xFF2B2D33)
val HirifyBackground = Color(0xFFF7F7F9)
val HirifySurface = Color(0xFFFFFFFF)
val HirifyTextPrimary = Color(0xFF1D1D1F)
val HirifyTextSecondary = Color(0xFF6E6E73)
val HirifyAccent = Color(0xFF8B5CF6) // Purple accent for some AI elements

private val HirifyLightColorScheme = lightColorScheme(
    primary = HirifyYellow,
    onPrimary = HirifyTextPrimary,
    secondary = HirifyDark,
    onSecondary = Color.White,
    background = HirifyBackground,
    onBackground = HirifyTextPrimary,
    surface = HirifySurface,
    onSurface = HirifyTextPrimary,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = HirifyTextSecondary,
    tertiary = HirifyAccent,
    onTertiary = Color.White
)

val HirifyTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

val HirifyShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun HirifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = HirifyLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HirifyTypography,
        shapes = HirifyShapes,
        content = content
    )
}
