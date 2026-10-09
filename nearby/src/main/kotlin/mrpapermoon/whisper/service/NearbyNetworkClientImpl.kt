package mrpapermoon.whisper.service

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import mrpapermoon.whisper.tools.awaitResult
import mrpapermoon.whisper.utils.asInputStream
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

internal class NearbyNetworkClientImpl(
	context: Context,
	exceptionHandler: CoroutineExceptionHandler,
	private val uuid: String,
) : NearbyNetworkClient {

	private val logger = KotlinLogging.logger {}

	private val isStopped = AtomicBoolean(false)
	private val collectors = CopyOnWriteArrayList<FlowCollector<ByteArray>>()
	private val connectionsClient by lazy { Nearby.getConnectionsClient(context) }

	private val history = MutableSharedFlow<ByteArray>(replay = MAX_HISTORY_SIZE, onBufferOverflow = BufferOverflow.DROP_OLDEST)

	private val scope: CoroutineScope
	private val activeEndpoints: Deferred<List<String>>

	init {
		val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
		scope = CoroutineScope(dispatcher + exceptionHandler + SupervisorJob())

		activeEndpoints = scope.async {
			@OptIn(ObsoleteCoroutinesApi::class)
			val actor = actor<NetworkConnectionsWrapper.Event> {
				for (event in channel) {
					logger.info { "On event ${event.javaClass}" }
					when (event) {
						is NetworkConnectionsWrapper.Event.DropConnection -> {}
						is NetworkConnectionsWrapper.Event.NewConnection -> {
							broadcastStream(history, event.endpointId)
						}

						is NetworkConnectionsWrapper.Event.OnStream -> {
							launch {
								event.byteStream.collect {
									for (collector in collectors) {
										collector.emit(it)
									}
								}
							}
						}

						is NetworkConnectionsWrapper.Event.OnMessage -> {
							launch {
								for (collector in collectors) {
									collector.emit(event.payload)
								}
							}
						}
					}
				}
			}

			NetworkConnectionsWrapper(
				actor,
				context,
				SERVICE_ID,
				uuid
			)
				.activeEndpoints
		}
	}

	override suspend fun broadcast(marshalledMessage: ByteArray) {
		if (isStopped.get()) return
		if (marshalledMessage.size > ConnectionsClient.MAX_BYTES_DATA_SIZE)
			throw IllegalArgumentException("Message size cannot be more than ${ConnectionsClient.MAX_BYTES_DATA_SIZE} bytes")

		history.emit(marshalledMessage)

		val activeEndpoints = activeEndpoints.await()
		connectionsClient.sendPayload(activeEndpoints, Payload.fromBytes(marshalledMessage))
			.awaitResult("sendPayload", marshalledMessage.contentHashCode())
	}

	override suspend fun broadcast(source: Flow<ByteArray>) {
		if (isStopped.get()) return

		history.emitAll(source)
		broadcastStream(source, null)
	}

	private suspend fun broadcastStream(source: Flow<ByteArray>, endpoint: String?) {
		val payload = Payload.fromStream(source.asInputStream(bufferSize = ConnectionsClient.MAX_BYTES_DATA_SIZE))
		if (endpoint == null) {
			val activeEndpoints = activeEndpoints.await()
			if (activeEndpoints.isEmpty()) return
			connectionsClient.sendPayload(activeEndpoints, payload)
		} else {
			connectionsClient.sendPayload(endpoint, payload)
		}
			.awaitResult("sendPayload", payload)
	}

	override suspend fun stopService() {
		isStopped.set(true)

		scope.cancel()
		collectors.clear()

		@OptIn(ExperimentalCoroutinesApi::class)
		history.resetReplayCache()
	}

	override suspend fun collect(collector: FlowCollector<ByteArray>) {
		collectors += collector

		try {
			awaitCancellation()
		} finally {
			collectors -= collector
		}
	}

	private companion object {
		const val SERVICE_ID = "mrpapermoon.whisper.MESH_SERVICE"
		const val MAX_HISTORY_SIZE = Int.MAX_VALUE
	}
}