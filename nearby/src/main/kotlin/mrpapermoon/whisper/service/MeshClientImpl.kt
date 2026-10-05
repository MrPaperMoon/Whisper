package mrpapermoon.whisper.service

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of [MeshClient] managing Nearby Connections peer-to-peer advertising, discovery,
 * payload transfer, and flow collection.
 *
 * @param context The Android [Context] used to access [Nearby.getConnectionsClient].
 * @param scope The [CoroutineScope] managing background coroutine tasks and network operations.
 * @param uuid The unique node identifier for advertising and discovery.
 */
internal class MeshClientImpl(
	context: Context,
	private val scope: CoroutineScope,
	private val uuid: String,
) : PayloadCallback(), MeshClient {

	private val logger = KotlinLogging.logger {}

	private val isStarted = AtomicBoolean(false)
	private val collectors = CopyOnWriteArrayList<FlowCollector<ByteArray>>()

	private val connectionsClient: ConnectionsClient by lazy { Nearby.getConnectionsClient(context) }
	private val connectionLifecycleCallbackWrapper by lazy { ConnectionLifecycleCallbackWrapper(scope, connectionsClient, this) }

	/**
	 * Broadcasts [marshalledMessage] to all connected endpoints in [ConnectionLifecycleCallbackWrapper.activeConnections].
	 *
	 * @param marshalledMessage The byte array payload to send.
	 */
	override suspend fun sendMessage(marshalledMessage: ByteArray) {
		checkIsActive()
		val activeConnections = connectionLifecycleCallbackWrapper.activeConnections
		if (activeConnections.isEmpty()) return
		withContext(Dispatchers.IO) {
			connectionsClient.sendPayload(
				activeConnections.toList(),
				Payload.fromBytes(marshalledMessage),
			)
				.addOnSuccessListener { logger.info { "message(${marshalledMessage.size}) successfully broadcasted" } }
				.addOnFailureListener { logger.error(it) { "message(${marshalledMessage.size}) failed to broadcast" } }
				.await()
		}
	}

	/**
	 * Registers a [collector] to receive incoming byte payloads and maintains network activity while collectors are attached.
	 *
	 * Automatically starts network advertising and discovery when the first collector arrives,
	 * and stops network activity when all collectors detach.
	 *
	 * @param collector The [FlowCollector] receiving incoming binary messages.
	 */
	override suspend fun collect(collector: FlowCollector<ByteArray>) {
		collectors += collector
		checkIsActive()

		try {
			awaitCancellation()
		} finally {
			collectors -= collector
			scope.launch {
				checkIsActive()
			}
		}
	}

	/**
	 * Callback triggered when a payload is received from a connected endpoint.
	 *
	 * For [Payload.Type.BYTES], dispatches received bytes to all registered collectors on [Dispatchers.IO].
	 *
	 * @param endpointId The unique identifier of the sending endpoint.
	 * @param payload The received payload container.
	 */
	override fun onPayloadReceived(
		endpointId: String,
		payload: Payload,
	) {
		if (payload.type != Payload.Type.BYTES) return
		val asBytes = payload.asBytes()!!
		scope.launch {
			collectors.asFlow()
				.collect { collector ->
					withContext(Dispatchers.IO) {
						collector.emit(asBytes)
					}
				}
		}
	}

	/**
	 * Callback triggered when payload transfer progress updates occur.
	 *
	 * @param endpointId The unique identifier of the endpoint involved in the payload transfer.
	 * @param update Transfer progress and status information.
	 */
	override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
		logger.info { "onPayloadTransferUpdate endpointId >$endpointId update >${update.dumpString()}" }
	}

	/**
	 * Stops advertising, discovery, closes endpoint connections, and cancels the underlying [scope].
	 */
	override fun stopService() {
		stopNetwork()
		scope.cancel()
	}

	/**
	 * Starts advertising and discovery using [Strategy.P2P_CLUSTER].
	 */
	@Throws(Exception::class)
	private suspend fun startNetwork() {
		if (isStarted.getAndSet(true)) {
			logger.info { "Client already started" }
			return
		}
		logger.info { "start receiving messages" }

		val strategy = Strategy.P2P_CLUSTER
		startAdvertisingSync(strategy)
		startDiscoverySync(strategy)
	}

	/**
	 * Initiates Nearby Connections advertising with [Strategy.P2P_CLUSTER].
	 */
	private suspend fun startAdvertisingSync(strategy: Strategy) {
		logger.info { "startAdvertising" }
		val advertisingOptions = AdvertisingOptions.Builder()
			.setStrategy(strategy)
			.build()

		connectionsClient.startAdvertising(
			uuid,
			SERVICE_ID,
			connectionLifecycleCallbackWrapper,
			advertisingOptions,
		)
			.addOnSuccessListener {
				logger.info { "onSuccess" }
			}
			.addOnFailureListener {
				logger.error(it) { "onFailure" }
			}
			.await()
	}

	/**
	 * Initiates Nearby Connections discovery with [Strategy.P2P_CLUSTER].
	 */
	private suspend fun startDiscoverySync(strategy: Strategy) {
		logger.info { "startDiscovery" }
		val discoveryOptions = DiscoveryOptions.Builder()
			.setStrategy(strategy)
			.build()

		val endpointDiscoveryCallbackWrapper =
			EndpointDiscoveryCallbackWrapper(scope, uuid, connectionsClient, connectionLifecycleCallbackWrapper)
		connectionsClient.startDiscovery(
			SERVICE_ID,
			endpointDiscoveryCallbackWrapper,
			discoveryOptions,
		)
			.addOnSuccessListener {
				logger.info { "onSuccess" }
			}
			.addOnFailureListener {
				logger.error(it) { "onFailure" }
			}
			.await()
	}

	/**
	 * Stops discovery, advertising, and disconnects all endpoints.
	 */
	private fun stopNetwork() {
		if (!isStarted.getAndSet(false)) {
			logger.info { "Client already stopped" }
			return
		}
		logger.info { "stopDiscovery" }
		connectionsClient.stopDiscovery()
		logger.info { "stopAdvertising" }
		connectionsClient.stopAdvertising()
		logger.info { "stop receiving messages" }
		connectionsClient.stopAllEndpoints()
	}

	/**
	 * Checks active collectors count and starts or stops the network accordingly.
	 */
	private suspend fun checkIsActive() {
		if (collectors.isEmpty()) stopNetwork() else startNetwork()
	}

	/**
	 * Formats [PayloadTransferUpdate] details into a string representation for logging purposes.
	 */
	private fun PayloadTransferUpdate.dumpString() =
		"PayloadTransferUpdate(payloadId=$payloadId, status=$status, bytesTransferred=$bytesTransferred, totalBytes=$totalBytes)"

	private companion object {
		const val SERVICE_ID = "mrpapermoon.whisper.MESH_SERVICE"
	}
}