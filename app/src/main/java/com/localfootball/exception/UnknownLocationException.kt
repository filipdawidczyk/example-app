package com.localfootball.exception

import com.localfootball.R
import java.lang.RuntimeException

class UnknownLocationException : RuntimeException(), UiException {
    override fun getErrorMessageResourceId() = R.string.create_new_event_unknown_address
}