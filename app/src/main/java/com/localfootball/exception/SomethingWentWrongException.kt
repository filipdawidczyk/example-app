package com.localfootball.exception

import java.lang.RuntimeException
//temporary
class SomethingWentWrongException(capturedErrorMessage: String): RuntimeException() {

    private val capturedErrorMessage = capturedErrorMessage

    fun getCapturedErrorMessage() = capturedErrorMessage
}