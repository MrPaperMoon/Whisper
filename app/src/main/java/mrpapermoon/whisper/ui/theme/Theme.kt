package mrpapermoon.whisper.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun WhisperTheme(
//	darkTheme: Boolean = isSystemInDarkTheme(),
//	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val lightColors = lightColors(
		primary = Colors.primary_400,
		primaryVariant = Colors.primary_600,
		secondary = Colors.accent_200,
		secondaryVariant = Colors.accent_800,
		background = Colors.primary,
		surface = Colors.primary_100,
		error = Colors.primary_900,
		onPrimary = Colors.colorOnPrimary,
		onSecondary = Colors.colorOnAccent,
		onBackground = Colors.colorOnPrimary,
		onSurface = Colors.colorOnPrimary,
		onError = Colors.primary,
	)

	MaterialTheme(
		colors = lightColors,
		content = content
	)
}