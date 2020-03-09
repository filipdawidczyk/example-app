package com.localfootball.page.events_map

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import android.content.SharedPreferences
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL

private const val LONGITUDE = "longitude"
private const val LATITUDE = "latitude"
private const val ZOOM = "zoom"
private const val BEARING = "bearing"
private const val TILT = "tilt"
private const val MAPTYPE = "MAPTYPE"
private const val PREFS_NAME = "mapCameraState"

class MapStateManager(context: Context) {

    private val mapStatePrefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    val savedMapType: Int get() = mapStatePrefs.getInt(MAPTYPE, MAP_TYPE_NORMAL)

    val savedCameraPosition: CameraPosition?
        get() {
            val latitude = mapStatePrefs.getFloat(LATITUDE, 0f).toDouble()
            if (latitude == 0.0) {
                return null
            }
            val longitude = mapStatePrefs.getFloat(LONGITUDE, 0f).toDouble()
            val target = LatLng(latitude, longitude)

            val zoom = mapStatePrefs.getFloat(ZOOM, 0f)
            val bearing = mapStatePrefs.getFloat(BEARING, 0f)
            val tilt = mapStatePrefs.getFloat(TILT, 0f)

            return CameraPosition(target, zoom, tilt, bearing)
        }

    fun saveMapState(mapMie: GoogleMap) {
        val editor = mapStatePrefs.edit()
        val position = mapMie.cameraPosition

        editor.putFloat(LATITUDE, position.target.latitude.toFloat())
        editor.putFloat(LONGITUDE, position.target.longitude.toFloat())
        editor.putFloat(ZOOM, position.zoom)
        editor.putFloat(TILT, position.tilt)
        editor.putFloat(BEARING, position.bearing)
        editor.putInt(MAPTYPE, mapMie.mapType)
        editor.apply()
    }

}