package mrpapermoon.whisper.service

import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mrpapermoon.utils.getValue
import java.lang.ref.WeakReference

/**
 * Wrapper for [EndpointDiscoveryCallback] that handles events when nearby endpoints are found or lost.
 *
 * When a new endpoint is discovered via [onEndpointFound], this wrapper attempts to automatically
 * request a connection to it using the provided [ConnectionsClient], unless the endpoint is already
 * connected or references have been garbage collected.
 *
 * @property scope The [CoroutineScope] used to launch asynchronous connection tasks.
 * @property uuid The local user or node identifier sent when requesting a connection.
 * @param connectionsClient The [ConnectionsClient] instance used to initiate or reject connections. Stored via [WeakReference].
 * @param connectionLifecycleCallback The lifecycle callback wrapper handling connection state changes. Stored via [WeakReference].
 */
internal class EndpointDiscoveryCallbackWrapper(
	private val scope: CoroutineScope,
	private val uuid: String,
	connectionsClient: ConnectionsClient,
	connectionLifecycleCallback: ConnectionLifecycleCallbackWrapper,
) : EndpointDiscoveryCallback() {

	private val logger = KotlinLogging.logger {}

	private val connectionsClient by WeakReference(connectionsClient)
	private val connectionLifecycleCallback by WeakReference(connectionLifecycleCallback)

	/**
	 * Called when a nearby endpoint is discovered.
	 *
	 * Launches a coroutine in [scope] to verify dependencies and active connection state, then
	 * requests a connection to the discovered endpoint via [ConnectionsClient.requestConnection].
	 * If dependencies are no longer available, the connection is rejected.
	 *
	 * @param endpointId The unique identifier of the discovered endpoint.
	 * @param info Metadata associated with the discovered endpoint, such as its service ID and name.
	 */
	override fun onEndpointFound(
		endpointId: String,
		info: DiscoveredEndpointInfo,
	) {
		logger.info { "onEndpointFound(endpointId: $endpointId, info: ${info.dumpString()})" }
		val connectionsClient = connectionsClient
		val connectionLifecycleCallback = connectionLifecycleCallback

		scope.launch {
			if (connectionLifecycleCallback == null || connectionsClient == null) {
				logger.warn { "onEndpointFound connectionLifecycleCallback or connectionsClient is null" }
				connectionsClient?.rejectConnection(endpointId)
					?.addOnSuccessListener { logger.info { "rejectConnection onSuccess" } }
					?.await()
				return@launch
			}
			if (connectionLifecycleCallback.activeConnections.contains(endpointId)) {
				logger.info { "Endpoint $endpointId is already connected" }
				return@launch
			}

			connectionsClient.requestConnection(uuid, endpointId, connectionLifecycleCallback)
				.addOnSuccessListener { logger.info { "requestConnection onSuccess" } }
				.addOnFailureListener { logger.error(it) { "requestConnection onFailure" } }
				.await()
		}
	}

	/**
	 * Called when a previously discovered endpoint is lost or no longer available for discovery.
	 *
	 * @param endpointId The unique identifier of the lost endpoint.
	 */
	override fun onEndpointLost(endpointId: String) {
		logger.info { "onEndpointLost(endpointId: $endpointId)" }
	}

	/**
	 * Formats [DiscoveredEndpointInfo] details into a human-readable string representation for logging.
	 */
	private fun DiscoveredEndpointInfo.dumpString(): String =
		"DiscoveredEndpointInfo(serviceId: $serviceId, endpointName: $endpointName)"
}