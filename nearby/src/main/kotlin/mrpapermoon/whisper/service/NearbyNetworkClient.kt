package mrpapermoon.whisper.service

import android.content.Context
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow

interface NearbyNetworkClient : Flow<ByteArray> {

	suspend fun broadcast(marshalledMessage: ByteArray)
	suspend fun broadcast(source: Flow<ByteArray>)
	suspend fun stopService()

	companion object Factory {
		fun initClient(
			context: Context,
			coroutineContext: CoroutineExceptionHandler,
			uuid: String,
		): NearbyNetworkClient = NearbyNetworkClientImpl(context, coroutineContext, uuid)
	}
}