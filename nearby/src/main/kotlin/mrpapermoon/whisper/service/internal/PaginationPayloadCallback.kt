package mrpapermoon.whisper.service.internal

import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import kotlinx.coroutines.flow.flowOf

internal class PaginationPayloadCallback : StreamPayloadCallback() {

	@Volatile
	private var bytesPayload = -1L

	override fun onPayloadReceived(
		endpointId: String,
		payload: Payload,
	) {
		if (payload.type == Payload.Type.BYTES) {
			bytesPayload = payload.id
			logger.debug { "onPayloadReceived for bytesPayload($bytesPayload)" }
			emitEvent(Event.OnStreamPayloadReceived(flowOf(payload.asBytes()!!)))
			return
		}
		super.onPayloadReceived(endpointId, payload)
	}

	override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
		if (update.payloadId == bytesPayload) {
			logger.debug { "onPayloadTransferUpdate for bytesPayload($bytesPayload)" }
			return
		}
		super.onPayloadTransferUpdate(endpointId, update)
	}
}