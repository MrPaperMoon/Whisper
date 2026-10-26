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
		primary = WhisperColors.primary_400,
		primaryVariant = WhisperColors.primary_600,
		secondary = WhisperColors.accent_200,
		secondaryVariant = WhisperColors.accent_800,
		background = WhisperColors.primary,
		surface = WhisperColors.primary_100,
		error = WhisperColors.primary_900,
		onPrimary = WhisperColors.colorOnPrimary,
		onSecondary = WhisperColors.colorOnAccent,
		onBackground = WhisperColors.colorOnPrimary,
		onSurface = WhisperColors.colorOnPrimary,
		onError = WhisperColors.primary,
	)

	MaterialTheme(
		colors = lightColors,
		content = content
	)
}