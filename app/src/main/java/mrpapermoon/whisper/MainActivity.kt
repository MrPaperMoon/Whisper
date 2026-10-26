package mrpapermoon.whisper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import mrpapermoon.whisper.navigation.LocalNavigationAction
import mrpapermoon.whisper.navigation.NavigationScreen
import mrpapermoon.whisper.screen.SplashScreen
import mrpapermoon.whisper.ui.theme.WhisperTheme
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private val viewModel: SharedViewModel by viewModels()

	fun setContentProvided(
		content: @Composable () -> Unit,
	) {
		setContent {
			WhisperTheme {
				CompositionLocalProvider(
					LocalNavigationAction provides viewModel,
					content = content,
				)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		var isUiReady = false
		splashScreen.setKeepOnScreenCondition {
			!isUiReady
		}

		setContentProvided {
			var hasAllPermissions by remember { mutableStateOf(false) }
			var composeRendered by remember { mutableStateOf(isUiReady) }

			val navController = rememberNavController()

			val permissionsToRequest = remember {
				val list = mutableListOf<String>()
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					list.add(Manifest.permission.NEARBY_WIFI_DEVICES)
				} else {
					list.add(Manifest.permission.ACCESS_FINE_LOCATION)
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
					list.add(Manifest.permission.BLUETOOTH_SCAN)
					list.add(Manifest.permission.BLUETOOTH_ADVERTISE)
					list.add(Manifest.permission.BLUETOOTH_CONNECT)
				}
				list.toTypedArray()
			}

			val launcher = rememberLauncherForActivityResult(
				contract = ActivityResultContracts.RequestMultiplePermissions()
			) { permissions ->
				hasAllPermissions = permissions.all { it.value }

				if (!hasAllPermissions) {
					Toast.makeText(this, "Permissions not granted, service unavailable (${permissions.keys})", Toast.LENGTH_SHORT).show()
					exitProcess(0)
				}
			}

			Scaffold(
				modifier = Modifier.fillMaxSize(),
				// TODO: тонкий статусбар
				topBar = {},
				// TODO: bottomBar
				bottomBar = {},
			) { innerPadding ->
				NavHost(
					navController = navController,
					startDestination = viewModel.verticalNavigationState,
					modifier = Modifier.padding(innerPadding)
				) {
					composable(NavigationScreen.splash.toString()) {
						SplashScreen(hasAllPermissions)
					}
//						composable(NavigationScreen.main.toString()) {
//							MainScreen()
//						}
				}
			}

			LaunchedEffect(composeRendered) {
				if (composeRendered) {
					hasAllPermissions = permissionsToRequest.all { permission ->
						ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_GRANTED
					}

					if (!hasAllPermissions) {
						launcher.launch(permissionsToRequest)
					}
				}
			}

			SideEffect {
				composeRendered = true
				isUiReady = true
			}
		}
	}
}
