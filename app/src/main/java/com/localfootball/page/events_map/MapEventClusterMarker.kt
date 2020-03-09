package com.localfootball.page.events_map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.localfootball.model.EventInfoWindowContent
import org.jetbrains.annotations.NotNull

data class MapEventClusterMarker(
    private val position: LatLng,
    private val eventInfoWindowContent: EventInfoWindowContent,
    private val iconPicture: Int
) : ClusterItem {
    override fun getSnippet(): String? {
        return null
    }
    override fun getTitle(): String? {
        return null
    }
    override fun getPosition() = position
    fun getEventInfoWindowContent() = eventInfoWindowContent
    fun getIconPicture() = iconPicture
}

