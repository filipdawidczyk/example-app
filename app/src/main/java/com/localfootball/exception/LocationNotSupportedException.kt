package com.localfootball.exception

import com.localfootball.R

class LocationNotSupportedException : RuntimeException(), UiException {
    override fun getErrorMessageResourceId() = R.string.location_not_supported
}