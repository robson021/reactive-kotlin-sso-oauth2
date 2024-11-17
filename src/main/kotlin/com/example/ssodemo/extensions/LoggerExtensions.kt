package com.example.ssodemo.extensions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified R : Any> R.getLogger() = lazy {
    LoggerFactory.getLogger(this::class.java.name.removeSuffix("\$Companion"))
}

fun Logger.logDebug(callback: () -> String) {
    if (isDebugEnabled) debug(callback())
}