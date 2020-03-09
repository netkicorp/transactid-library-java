package com.netki.util

import com.google.protobuf.ByteString
import com.netki.exceptions.InvalidOwnersException
import com.netki.model.OwnerParameters
import com.netki.util.ErrorInformation.OWNERS_VALIDATION_MULTIPLE_PRIMARY_OWNERS
import com.netki.util.ErrorInformation.OWNERS_VALIDATION_NO_PRIMARY_OWNER

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
