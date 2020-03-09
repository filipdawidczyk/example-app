package com.localfootball.page.events_map

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.localfootball.R

class MapEventClusterManagerRenderer(
    private val context: Context,
    private val map: GoogleMap,
    private val mapEventClusterManager: ClusterManager<MapEventClusterMarker>,
    private val iconGenerator: IconGenerator,
    private val imageView: ImageView,
    private val markerWidth: Int,
    private val markerHeight: Int
) : DefaultClusterRenderer<MapEventClusterMarker>(context, map, mapEventClusterManager) {

    constructor(context: Context, map: GoogleMap, mapEventClusterManager: ClusterManager<MapEventClusterMarker>) : this(
        context = context,
        map = map,
        mapEventClusterManager = mapEventClusterManager,
        iconGenerator = IconGenerator(context.applicationContext),
        imageView = ImageView(context.applicationContext),
        markerWidth = context.resources.getDimension(R.dimen.custom_marker_image).toInt(),
        markerHeight = context.resources.getDimension(R.dimen.custom_marker_image).toInt()
    ) {
        val padding = context.resources.getDimension(R.dimen.custom_marker_padding).toInt()
        imageView.layoutParams = ViewGroup.LayoutParams(markerWidth, markerHeight)
        imageView.setPadding(padding, padding, padding, padding)
        iconGenerator.setContentView(imageView)
    }

    override fun onBeforeClusterItemRendered(mapEventClusterMarker: MapEventClusterMarker, markerOptions: MarkerOptions?) {
        imageView.setImageResource(mapEventClusterMarker.getIconPicture())
        val icon = iconGenerator.makeIcon()
        markerOptions
            ?.icon(BitmapDescriptorFactory.fromBitmap(icon))
            ?.title(mapEventClusterMarker.title)

        val marker = map.addMarker(markerOptions)
        eventInfoHashMap[marker] = mapEventClusterMarker
    }

    override fun shouldRenderAsCluster(mapEventCluster: Cluster<MapEventClusterMarker>): Boolean = false
}