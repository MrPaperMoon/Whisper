@file:Suppress("NOTHING_TO_INLINE")

package mrpapermoon.whisper.utils

import java.lang.ref.Reference
import kotlin.reflect.KProperty

inline operator fun <T> Reference<T>.getValue(thisRef: Any?, property: KProperty<*>): T? = get()