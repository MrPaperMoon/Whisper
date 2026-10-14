package mrpapermoon.whisper.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import mrpapermoon.whisper.R

object Colors {

	//region Theme colors
	val primary: Color
		@Composable get() = colorResource(id = R.color.primary)

	val colorOnPrimary: Color
		@Composable get() = colorResource(id = R.color.colorOnPrimary)

	val accent: Color
		@Composable get() = colorResource(id = R.color.accent)

	val colorOnAccent: Color
		@Composable get() = colorResource(id = R.color.colorOnAccent)
	//endregion

	//region Primary set
	val primary_50: Color
		@Composable get() = colorResource(id = R.color.primary_50)

	val primary_100: Color
		@Composable get() = colorResource(id = R.color.primary_100)

	val primary_200: Color
		@Composable get() = colorResource(id = R.color.primary_200)

	val primary_300: Color
		@Composable get() = colorResource(id = R.color.primary_300)

	val primary_400: Color
		@Composable get() = colorResource(id = R.color.primary_400)

	val primary_500: Color
		@Composable get() = colorResource(id = R.color.primary_500)

	val primary_600: Color
		@Composable get() = colorResource(id = R.color.primary_600)

	val primary_700: Color
		@Composable get() = colorResource(id = R.color.primary_700)

	val primary_800: Color
		@Composable get() = colorResource(id = R.color.primary_800)

	val primary_900: Color
		@Composable get() = colorResource(id = R.color.primary_900)

	val primary_triadic_1: Color
		@Composable get() = colorResource(id = R.color.primary_triadic_1)

	val primary_triadic_2: Color
		@Composable get() = colorResource(id = R.color.primary_triadic_2)

	val primary_analogous_1: Color
		@Composable get() = colorResource(id = R.color.primary_analogous_1)

	val primary_analogous_2: Color
		@Composable get() = colorResource(id = R.color.primary_analogous_2)

	val primary_complementary: Color
		@Composable get() = colorResource(id = R.color.primary_complementary)
	//endregion

	//region Accent set
	val accent_50: Color
		@Composable get() = colorResource(id = R.color.accent_50)

	val accent_100: Color
		@Composable get() = colorResource(id = R.color.accent_100)

	val accent_200: Color
		@Composable get() = colorResource(id = R.color.accent_200)

	val accent_300: Color
		@Composable get() = colorResource(id = R.color.accent_300)

	val accent_400: Color
		@Composable get() = colorResource(id = R.color.accent_400)

	val accent_500: Color
		@Composable get() = colorResource(id = R.color.accent_500)

	val accent_600: Color
		@Composable get() = colorResource(id = R.color.accent_600)

	val accent_700: Color
		@Composable get() = colorResource(id = R.color.accent_700)

	val accent_800: Color
		@Composable get() = colorResource(id = R.color.accent_800)

	val accent_900: Color
		@Composable get() = colorResource(id = R.color.accent_900)

	val accent_triadic_1: Color
		@Composable get() = colorResource(id = R.color.accent_triadic_1)

	val accent_triadic_2: Color
		@Composable get() = colorResource(id = R.color.accent_triadic_2)

	val accent_analogous_1: Color
		@Composable get() = colorResource(id = R.color.accent_analogous_1)

	val accent_analogous_2: Color
		@Composable get() = colorResource(id = R.color.accent_analogous_2)

	val accent_complementary: Color
		@Composable get() = colorResource(id = R.color.accent_complementary)
	//endregion

	//region Other colors
	val iconColor: Color
		@Composable get() = colorResource(id = R.color.iconColor)
	//endregion
}
