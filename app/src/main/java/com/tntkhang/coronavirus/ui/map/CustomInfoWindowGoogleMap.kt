package com.tntkhang.coronavirus.ui.map

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import com.tntkhang.coronavirus.R
import com.tntkhang.coronavirus.models.MapAttribute
import kotlinx.android.synthetic.main.map_custom_infowindow.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomInfoWindowGoogleMap(val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker?): View {
        var mInfoView = (context as Activity).layoutInflater.inflate(R.layout.map_custom_infowindow, null)
        var stat: MapAttribute? = p0?.tag as MapAttribute?

        val cases = context.getString(R.string.confirm_case,
            stat?.Confirmed,
            stat?.Recovered,
            stat?.Deaths)

        var title = stat?.Country_Region!!
        stat.Province_State?.let {
            title += it
        }

        mInfoView.tv_region.text = title
        mInfoView.tv_cases.text = cases

        return mInfoView
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }
}