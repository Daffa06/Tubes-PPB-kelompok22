package com.bismillah.alquran.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.bismillah.alquran.R
import com.bismillah.alquran.databinding.ActivityMasjidBinding
import com.bismillah.alquran.model.nearby.ModelResults
import com.bismillah.alquran.viewmodel.MasjidViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import im.delight.android.location.SimpleLocation
import java.util.*

class MasjidActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMasjidBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var masjidViewModel: MasjidViewModel

    private var strCurrentLatitude = 0.0
    private var strCurrentLongitude = 0.0
    private lateinit var strCurrentLocation: String
    private lateinit var mapsView: GoogleMap
    private lateinit var simpleLocation: SimpleLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasjidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("sedang menampilkan lokasi")

        setInitLayout()
    }

    private fun setInitLayout() {
        binding.toolbar.setTitle(null)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        simpleLocation = SimpleLocation(this)
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        // Get location
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        // Set location lat long
        strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"

        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapsView = googleMap

        // Viewmodel
        setViewModel()
    }

    private fun setViewModel() {
        progressDialog.show()
        masjidViewModel =
            ViewModelProvider(this, NewInstanceFactory()).get(MasjidViewModel::class.java)
        masjidViewModel.setMarkerLocation(strCurrentLocation)
        masjidViewModel.getMarkerLocation()
            .observe(this, { modelResults: ArrayList<ModelResults> ->
                if (modelResults.size != 0) {

                    // Get multiple markers
                    getMarker(modelResults)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(
                        this,
                        "Oops, tidak bisa mendapatkan lokasi kamu!",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }
            })
    }

    private fun getMarker(modelResultsArrayList: ArrayList<ModelResults>) {
        for (i in modelResultsArrayList.indices) {

            // Set LatLng from API
            val latLngMarker = LatLng(
                modelResultsArrayList[i].modelGeometry.modelLocation.lat,
                modelResultsArrayList[i].modelGeometry.modelLocation.lng
            )

            // Add marker
            mapsView.addMarker(
                MarkerOptions()
                    .position(latLngMarker)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(modelResultsArrayList[i].name)
            )

            // Show marker
            val latLngResult = LatLng(
                modelResultsArrayList[0].modelGeometry.modelLocation.lat,
                modelResultsArrayList[0].modelGeometry.modelLocation.lng
            )

            // Set position marker
            mapsView.moveCamera(CameraUpdateFactory.newLatLng(latLngResult))
            mapsView.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latLngResult.latitude, latLngResult.longitude),
                    14f
                )
            )
            mapsView.uiSettings.setAllGesturesEnabled(true)
            mapsView.uiSettings.isZoomGesturesEnabled = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}