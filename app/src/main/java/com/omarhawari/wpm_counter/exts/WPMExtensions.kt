package com.omarhawari.wpm_counter.exts

import java.util.Locale

inline fun Float.toTwoDecimalPlaces(): String = String.format(Locale.ROOT, "%.2f", this)
inline fun Double.toTwoDecimalPlaces(): String = String.format(Locale.ROOT, "%.2f", this)
