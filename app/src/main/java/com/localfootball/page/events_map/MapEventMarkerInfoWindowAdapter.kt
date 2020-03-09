package com.localfootball.page.events_map

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat.getFont
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.localfootball.R

class MapEventMarkerInfoWindowAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val window =
            LayoutInflater
                .from(context)
                .inflate(
                    R.layout.map_event_marker_info_window,
                    null
                )
        return getConfiguredCustomInfoWindow(window, marker)
    }

    @SuppressLint("SetTextI18n")
    private fun getConfiguredCustomInfoWindow(window: View, marker: Marker): View {
        window.findViewById<TextView>(R.id.mapEventInfoWindowEventNameTextView).typeface =
            getFont(context, R.font.bungee)
        window.findViewById<TextView>(R.id.mapEventInfoWindowEventNameTextView).text =
            eventInfoHashMap[marker]?.getEventInfoWindowContent()?.name

        window.findViewById<TextView>(R.id.mapEventInfoWindowStartDateTextView).typeface =
            getFont(context, R.font.montserrat)
        window.findViewById<TextView>(R.id.mapEventInfoWindowStartDateTextView).text =
            eventInfoHashMap[marker]?.getEventInfoWindowContent()?.startDate.toString()

        window.findViewById<TextView>(R.id.mapEventInfoWindowStartTimeTextView).typeface =
            getFont(context, R.font.montserrat)
        window.findViewById<TextView>(R.id.mapEventInfoWindowStartTimeTextView).text =
            eventInfoHashMap[marker]?.getEventInfoWindowContent()?.startTime.toString()

        window.findViewById<TextView>(R.id.mapEventInfoWindowPlayersCountTextView).typeface =
            getFont(context, R.font.montserrat)
        window.findViewById<TextView>(R.id.mapEventInfoWindowPlayersCountTextView).text =
            eventInfoHashMap[marker]?.getEventInfoWindowContent()?.participantsNumber.toString() + "/" +
                    eventInfoHashMap[marker]?.getEventInfoWindowContent()?.maxPlayers.toString()

        window.findViewById<TextView>(R.id.mapEventInfoWindowOrganizerTextView).typeface =
            getFont(context, R.font.montserrat)
        window.findViewById<TextView>(R.id.mapEventInfoWindowOrganizerTextView).text =
            eventInfoHashMap[marker]?.getEventInfoWindowContent()?.organizerName

        return window
    }
}