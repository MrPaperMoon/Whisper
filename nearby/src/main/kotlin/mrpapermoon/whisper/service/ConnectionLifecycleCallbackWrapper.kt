package mrpapermoon.whisper.service

import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.PayloadCallback
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mrpapermoon.utils.getValue
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Wrapper for [ConnectionLifecycleCallback] that handles connection lifecycle events
 * between nearby devices (connection initiation, result status, and disconnection).
 *
 * Maintains a thread-safe set of currently [activeConnections] and automatically accepts
 * incoming or outgoing connection requests using [ConnectionsClient.acceptConnection].
 *
 * @property scope The [CoroutineScope] used to launch asynchronous connection acceptance/rejection tasks.
 * @param connectionsClient The [ConnectionsClient] instance used to accept or reject connections. Stored via [WeakReference].
 * @param payloadCallback The [PayloadCallback] instance passed when accepting connections to receive payload data. Stored via [WeakReference].
 */
internal class ConnectionLifecycleCallbackWrapper(
	private val scope: CoroutineScope,
	connectionsClient: ConnectionsClient,
	payloadCallback: PayloadCallback,
) : ConnectionLifecycleCallback() {

	private val logger = KotlinLogging.logger {}

	private val connectionsClient by WeakReference(connectionsClient)
	private val payloadCallback by WeakReference(payloadCallback)

	/**
	 * Thread-safe set containing the endpoint IDs of all currently connected endpoints.
	 */
	val activeConnections: Collection<String>
		field = CopyOnWriteArraySet<String>()

	/**
	 * Called when a connection is initiated from or to a nearby endpoint.
	 *
	 * Launches a coroutine in [scope] to accept the connection with [payloadCallback]
	 * if references are available, or reject it if required dependencies have been garbage collected.
	 *
	 * @param endpointId The unique identifier of the endpoint initiating or receiving the connection.
	 * @param connectionInfo Metadata about the connection (e.g. endpoint name, authentication token).
	 */
	override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
		logger.info { "onConnectionInitiated endpointId >$endpointId connectionInfo >${connectionInfo.dumpString()}" }
		val payloadCallback = payloadCallback
		val connectionsClient = connectionsClient
		scope.launch {
			if (payloadCallback == null || connectionsClient == null) {
				logger.warn { "onConnectionInitiated payloadCallback or connectionsClient is null" }
				connectionsClient?.rejectConnection(endpointId)
					?.await()
				return@launch
			}

			connectionsClient.acceptConnection(endpointId, payloadCallback)
				.addOnSuccessListener { logger.info { "acceptConnection onSuccess" } }
				.addOnFailureListener { logger.error(it) { "acceptConnection onFailure" } }
				.await()
		}
	}

	/**
	 * Called when a connection response is received.
	 *
	 * If the connection result is successful, [endpointId] is added to [activeConnections].
	 *
	 * @param endpointId The unique identifier of the endpoint.
	 * @param result The [ConnectionResolution] containing status code and result information.
	 */
	override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
		val status = result.status
		logger.info { "onConnectionResult status >$status" }
		if (!status.isSuccess) {
			if (!status.isCanceled && !status.isInterrupted) {
				logger.warn { "OnConnectionResult failed: statusCode >${status.statusCode} statusMessage >${status.statusMessage}" }
			}
			return
		}
		activeConnections += endpointId
		logger.info { "OnConnectionResult Success >$endpointId" }
	}

	/**
	 * Called when an active connection with a nearby endpoint is disconnected.
	 *
	 * Removes [endpointId] from [activeConnections].
	 *
	 * @param endpointId The unique identifier of the disconnected endpoint.
	 */
	override fun onDisconnected(endpointId: String) {
		logger.info { "OnDisconnect >$endpointId" }
		activeConnections -= endpointId
	}

	/**
	 * Formats [ConnectionInfo] properties into a string representation for logging purposes.
	 */
	@Suppress("DEPRECATION")
	private fun ConnectionInfo.dumpString(): String = "ConnectionInfo(endpointName=$endpointName, isIncomingConnection=$isIncomingConnection, authenticationToken=$authenticationToken)"
}