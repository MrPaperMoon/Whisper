package mrpapermoon.whisper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import mrpapermoon.whisper.service.MeshClient
import mrpapermoon.whisper.ui.theme.WhisperTheme
import kotlin.uuid.Uuid

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContent {
			WhisperTheme {
				val uuid = remember { Uuid.random().toHexDashString() }
				val context = LocalContext.current

				var meshClient: MeshClient? by remember { mutableStateOf(null) }
				var hasAllPermissions: Boolean by remember { mutableStateOf(false) }

				val statusField = remember { mutableStateOf("Initializing...") }
				val chatHistory = remember { mutableStateListOf<String>() }

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
						statusField.value = "Permissions denied. Cannot start mesh network."
					}
				}

				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					Column(
						modifier = Modifier
							.fillMaxSize()
							.padding(innerPadding),
						verticalArrangement = Arrangement.spacedBy(8.dp)
					) {
						Text(
							text = statusField.value,
							modifier = Modifier.padding(16.dp)
						)

						LazyColumn(
							modifier = Modifier
								.weight(1f)
								.fillMaxWidth()
								.padding(horizontal = 16.dp)
						) {
							items(chatHistory) { entry ->
								Text(text = entry, modifier = Modifier.padding(vertical = 4.dp))
							}
						}

						PingButton(meshClient, statusField)
					}
				}

				LaunchedEffect(hasAllPermissions) {
					if (hasAllPermissions) {
						statusField.value = "Initializing Mesh Engine..."
						val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
							statusField.value = throwable.message ?: "Unknown error"
						}
						val client = MeshClient.startService(context, uuid, coroutineContext = exceptionHandler)
						statusField.value = "Started to collecting rumors"
						meshClient = client
						client.collect { message ->
							chatHistory.add(String(message))
						}
					} else {
						meshClient?.stopService()
					}
				}

				LaunchedEffect(Unit) {
					hasAllPermissions = permissionsToRequest.all { permission ->
						ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_GRANTED
					}

					if (!hasAllPermissions) {
						launcher.launch(permissionsToRequest)
					}
				}

				DisposableEffect(Unit) {
					onDispose {
						meshClient?.stopService()
						meshClient = null
					}
				}
			}
		}
	}

	@Composable
	private fun PingButton(
		meshClient: MeshClient?,
		networkStatus: MutableState<String>,
	) {
		val scope = rememberCoroutineScope()
		var sendIsInProgress by remember { mutableStateOf(false) }
		Button(
			modifier = Modifier
				.height(98.dp)
				.padding(horizontal = 16.dp),
			contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
			enabled = !sendIsInProgress && meshClient != null,
			onClick = {
				sendIsInProgress = true
				scope.launch {
					val status = networkStatus.value
					networkStatus.value = "Sending message..."
					meshClient!!.sendMessage("Ping".toByteArray())
					networkStatus.value = status
					sendIsInProgress = false
				}
			}
		) {
			Icon(
				Icons.AutoMirrored.Filled.Send,
				contentDescription = null,
				modifier = Modifier.size(ButtonDefaults.IconSize)
			)
			Spacer(Modifier.size(8.dp))
			Text("PING")
		}
	}
}