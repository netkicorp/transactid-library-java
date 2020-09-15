package com.netki.util

import com.google.protobuf.ByteString
import java.util.regex.Pattern

private const val REGEX = "^[a-zA-Z0-9_. -]+$"
private val PATTERN: Pattern = Pattern.compile(REGEX)

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

/**
 * Validate if an string contains only alphanumeric and white spaces characters
 */
internal fun String.isAlphaNumeric() = PATTERN.matcher(this).matches()
