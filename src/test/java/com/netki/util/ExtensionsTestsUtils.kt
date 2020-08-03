package com.netki.util

import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.http.hostWithPort

class ExtensionsTestsUtils

val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort

val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"
