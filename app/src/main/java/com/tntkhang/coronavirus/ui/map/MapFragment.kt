package com.tntkhang.coronavirus.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.tntkhang.coronavirus.R
import com.tntkhang.coronavirus.models.MapStats
import com.tntkhang.coronavirus.network.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MapFragment : Fragment(), OnMapReadyCallback {

    private val appService by lazy { NetworkService.create(context!!) }
    private var disposable: Disposable? = null
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getStatsData() {
        disposable = appService.getMapStats()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {stats -> onGetDataSuccess(stats)},
                {error ->
                    Log.e("QWEASD", "getData Err: ${error}")
                },
                { Log.i("QWEASD", "getData Login Completed")}
            )
    }

    private fun onGetDataSuccess(stats: MapStats) {
        stats.features!!.forEach { stat ->
//            val snippetValue = context?.getString(R.string.confirm_case,
//                stat.attributes?.Confirmed,
//                stat.attributes?.Recovered,
//                stat.attributes?.Deaths)
//
//            val bitmapId = when {
//                stat.attributes?.Confirmed!! < 10 -> R.drawable.zone_less_than_10
//                stat.attributes?.Confirmed!! in 10..99 -> R.drawable.zone_10_100
//                else -> R.drawable.zone_over_100
//            }
            val radius = when {
                stat.attributes?.Confirmed!! < 10 -> 30000
                stat.attributes?.Confirmed!! in 10..99 -> 60000
                else -> 100000
            }
            val color = when {
                stat.attributes?.Confirmed!! < 10 -> ContextCompat.getColor(context!!, R.color.red_less_than_10)
                stat.attributes?.Confirmed!! in 10..99 -> ContextCompat.getColor(context!!, R.color.red_10_100)
                else -> ContextCompat.getColor(context!!, R.color.red_over_100)
            }

            var title = stat.attributes?.Country_Region!!
            stat.attributes?.Province_State?.let {
                title += it
            }


            val customInfoWindow = CustomInfoWindowGoogleMap(context!!)
            mMap.setInfoWindowAdapter(customInfoWindow)

            val marker = mMap.addCircle(
                CircleOptions()
                    .center(LatLng(stat.attributes?.Lat!!.toDouble(), stat.attributes?.Long_!!.toDouble()))
                    .radius(radius.toDouble())
                    .fillColor(color)
                    .strokeColor(color)
            )

            marker.tag = stat
//            marker.showInfoWindow()

//            mMap.addMarker(
//                MarkerOptions()
//                    .position(LatLng(stat.attributes?.Lat!!.toDouble(), stat.attributes?.Long_!!.toDouble()))
//                    .title(title)
//                    .snippet(snippetValue)
//                    .icon(getBitmap(bitmapId)))

//            Log.i("QWEASD", "$title - $snippetValue - ${stat.attributes?.Lat!!.toDouble()} - ${stat.attributes?.Long_!!.toDouble()}")
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        getStatsData()

        mMap = googleMap

        val tphcm = LatLng(10.0, 106.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tphcm))

//        mMap.addCircle(
//                CircleOptions()
//                    .center(tphcm)
//                    .radius(10000.0)
//                    .fillColor(Color.RED)
//                    .strokeColor(Color.RED)
//                )
    }

    private fun getBitmap(drawableId: Int): BitmapDescriptor? {
        val drawable = ContextCompat.getDrawable(activity!!, drawableId)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}