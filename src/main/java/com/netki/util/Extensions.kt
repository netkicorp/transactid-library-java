package com.netki.util

import com.google.protobuf.ByteString

/**
 * Transform String to ByteString.
 *
 * @return ByteString.
 */
fun String.toByteString(): ByteString = ByteString.copyFrom(this.toByteArray())

/**
 * Transform ByteString to String.
 *
 * @return String.
 */
fun ByteString.toStringLocal(): String = String(this.toByteArray())

/**
 * Transform ByteArray to ByteString.
 *
 * @return ByteString.
 */
fun ByteArray.toByteString(): ByteString = ByteString.copyFrom(this)
