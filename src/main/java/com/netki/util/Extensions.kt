package com.netki.util

import com.google.protobuf.ByteString

/**
 * Transform String to ByteString.
 *
 * @return ByteString.
 */
internal fun String.toByteString(): ByteString = ByteString.copyFrom(this.toByteArray())

/**
 * Transform ByteString to String.
 *
 * @return String.
 */
internal fun ByteString.toStringLocal(): String = String(this.toByteArray())

/**
 * Transform ByteArray to ByteString.
 *
 * @return ByteString.
 */
internal fun ByteArray.toByteString(): ByteString = ByteString.copyFrom(this)
