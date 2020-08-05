package com.example.chap.view.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.chap.R
import com.google.gson.JsonObject
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT
import ir.map.sdk_map.MapirStyle
import ir.map.servicesdk.MapService
import ir.map.servicesdk.ResponseListener
import ir.map.servicesdk.model.base.MapirError
import ir.map.servicesdk.response.FastReverseGeoCodeResponse
import ir.map.servicesdk.response.ReverseGeoCodeResponse
import kotlinx.android.synthetic.main.fragment_map.*


class MapFragment : Fragment() {

    lateinit var map_view: MapView

    private var map: MapboxMap? = null
    private var mapStyle: Style? = null
    private var symbol: Symbol? = null
    private var symbolManager: SymbolManager? = null
    private val mapService = MapService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_view = view.findViewById(R.id.map_view)

        map_view.onCreate(savedInstanceState)

        map_view.getMapAsync {
            map = it
            it.setStyle(
                Style.Builder().fromUri(MapirStyle.MAIN_MOBILE_VECTOR_STYLE),
                Style.OnStyleLoaded { style ->
                    mapStyle = style
                    map!!.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                36.309632,
                                59.529750
                            ),
                            15.0
                        )
                    )

                    enableLocationComponent()
                    symbolManager = SymbolManager(map_view, map!!, mapStyle!!)
                    symbolManager!!.iconAllowOverlap = true
                    symbolManager!!.iconRotationAlignment = ICON_ROTATION_ALIGNMENT_VIEWPORT
                })


            map!!.addOnMapClickListener { point ->
                addSymbolToMap(point)

                false
            }

        }

        val navController = Navigation.findNavController(view)
        btn_continue.setOnClickListener {
            if (symbol != null) {
                mapService.reverseGeoCode(
                    symbol!!.latLng.latitude,
                    symbol!!.latLng.longitude,
                    object : ResponseListener<ReverseGeoCodeResponse> {
                        override fun onSuccess(response: ReverseGeoCodeResponse) {
                            Log.i("fuck", response.addressCompact)
                            val bundle = bundleOf(
                                "address" to response.addressCompact,
                                "lat" to symbol!!.latLng.latitude.toFloat(),
                                "lng" to symbol!!.latLng.longitude.toFloat()
                            )
                            navController.navigate(
                                R.id.action_mapFragment_to_editAddressFragment,
                                bundle
                            )
                        }

                        override fun onError(error: MapirError) {
                            Toast.makeText(
                                context,
                                "مشکلی در آدرس یابی پیش آمده است",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })

            } else
                Toast.makeText(context, "آدرسی را انتخاب کنید", Toast.LENGTH_LONG).show()

        }
    }

    private fun enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            val customLocationComponentOptions =
                LocationComponentOptions.builder(requireContext()).elevation(5f).accuracyAlpha(0.6f)
                    .accuracyColor(Color.TRANSPARENT).build()

            val locationComponent = map!!.locationComponent
            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(requireContext(), mapStyle!!)
                    .locationComponentOptions(customLocationComponentOptions).build()

            locationComponent.activateLocationComponent(locationComponentActivationOptions)

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            locationComponent.isLocationComponentEnabled = true

            locationComponent.cameraMode = CameraMode.TRACKING

            locationComponent.renderMode = RenderMode.COMPASS

            if (locationComponent.lastKnownLocation != null)
                map!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            locationComponent.lastKnownLocation!!.latitude,
                            locationComponent.lastKnownLocation!!.longitude
                        ),
                        24.0
                    )
                )
            else
                map!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            36.309632,
                            59.529750
                        ),
                        15.0
                    )
                )

        } else {
            val permissionsManager =
                PermissionsManager(object : PermissionsListener {
                    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {}
                    override fun onPermissionResult(granted: Boolean) {
                        if (granted) enableLocationComponent() else Toast.makeText(
                            context,
                            "Permission Denied",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })

            permissionsManager.requestLocationPermissions(activity);
        }
    }


    private fun addSymbolToMap(point: LatLng) {
        mapStyle!!.addImage(
            "image_id",
            resources.getDrawable(R.drawable.mapbox_marker_icon_default)
        )
        val symbolOptions = SymbolOptions()
        symbolOptions.withLatLng(point)
        symbolOptions.withGeometry(Point.fromLngLat(point.longitude, point.latitude))
        symbolOptions.withIconImage("image_id")
        symbolOptions.withIconSize(1.0f)
        if (symbol != null)
            symbolManager!!.delete(symbol)
        symbol = symbolManager!!.create(symbolOptions)
    }

    override fun onStart() {
        super.onStart()
        map_view.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onStop() {
        super.onStop()
        map_view.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map_view.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }
}