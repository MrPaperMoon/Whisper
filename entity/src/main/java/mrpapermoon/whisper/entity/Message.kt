package mrpapermoon.whisper.entity

sealed interface Message {
	val userId: String

	data class BranchLog(val hashList: List<Long>, override val userId: String): Message
	data class Whisper(val encryptedMessage: String, override val userId: String): Message
	data class Cipher(val cipher: String, override val userId: String): Message
}