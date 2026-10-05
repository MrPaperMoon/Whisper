package mrpapermoon.whisper.service

import android.content.Context
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * Interface representing a mesh network client for peer-to-peer messaging via Nearby Connections.
 *
 * Extends [Flow] of [ByteArray] to allow callers to collect incoming messages broadcasted across the mesh network.
 */
interface MeshClient : Flow<ByteArray> {

	/**
	 * Broadcasts a binary payload ([marshalledMessage]) to all currently active connected endpoints in the mesh.
	 *
	 * @param marshalledMessage The byte array payload to send.
	 */
	suspend fun sendMessage(marshalledMessage: ByteArray)

	/**
	 * Stops advertising, discovery, and closes all active endpoint connections, shutting down the mesh service.
	 */
	fun stopService()

	/**
	 * Factory companion object for instantiating [MeshClient] instances.
	 */
	companion object Factory {
		/**
		 * Creates and starts a new [MeshClient] instance.
		 *
		 * @param context The Android [Context] used to initialize Nearby Connections client.
		 * @param uuid The unique identifier representing this node in the mesh network.
		 * @param coroutineContext Optional custom [CoroutineContext] merged into the client's internal [CoroutineScope].
		 * @return A newly created [MeshClient] instance.
		 */
		@WorkerThread
		fun startService(
			context: Context,
			uuid: String,
			coroutineContext: CoroutineContext,
		): MeshClient {
			val dispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
			val scope = CoroutineScope(dispatcher + coroutineContext + SupervisorJob())
			return MeshClientImpl(context, scope, uuid)
		}
	}
}