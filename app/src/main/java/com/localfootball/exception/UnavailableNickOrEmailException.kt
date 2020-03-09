package com.localfootball.exception

import com.localfootball.R
import java.lang.RuntimeException

data class UnavailableNickOrEmailException(
    private val nicknameAvailable: Boolean,
    private val emailAvailable: Boolean
) : RuntimeException(), UiException {
    override fun getErrorMessageResourceId(): Int =
        if (!nicknameAvailable && !emailAvailable) R.string.email_and_nick_is_already_taken
        else if (!nicknameAvailable) R.string.nick_is_already_taken
        else R.string.email_is_already_taken
}