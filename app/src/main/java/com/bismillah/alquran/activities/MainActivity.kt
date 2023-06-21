package com.bismillah.alquran.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.bismillah.alquran.R
import com.bismillah.alquran.adapter.MainAdapter
import com.bismillah.alquran.databinding.ActivityMainBinding
import com.bismillah.alquran.fragment.FragmentJadwalSholat.Companion.newInstance
import com.bismillah.alquran.model.main.ModelSurah
import com.bismillah.alquran.viewmodel.SurahViewModel
import im.delight.android.location.SimpleLocation
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var surahViewModel: SurahViewModel
    private lateinit var mainAdapter: MainAdapter

    private var REQ_PERMISSION = 100
    private var strCurrentLatitude = 0.0
    private var strCurrentLongitude = 0.0
    private lateinit var strCurrentLatLong: String
    private lateinit var strDate: String
    private lateinit var strDateNow: String
    private lateinit var simpleLocation: SimpleLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitLayout()
        setPermission()
        setLocation()
        setCurrentLocation()
        setViewModel()
    }

    private fun setInitLayout() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Sedang menampilkan data...")

        val dateNow = Calendar.getInstance().time
        strDate = DateFormat.format("EEEE", dateNow) as String
        strDateNow = DateFormat.format("d MMMM yyyy", dateNow) as String

        binding.tvToday.text = "$strDate,"
        binding.tvDate.text = strDateNow

        mainAdapter = MainAdapter(this)
        binding.rvSurah.setHasFixedSize(true)
        binding.rvSurah.layoutManager = LinearLayoutManager(this)
        binding.rvSurah.adapter = mainAdapter

        val jadwalSholat = newInstance("Jadwal Sholat")
        binding.layoutTime.setOnClickListener {
            jadwalSholat.show(
                supportFragmentManager, jadwalSholat.tag
            )
        }

        binding.layoutMosque.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    MasjidActivity::class.java
                )
            )
        }
    }

    private fun setLocation() {
        simpleLocation = SimpleLocation(this)
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        // Get location
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        // Set location lat long
        strCurrentLatLong = "$strCurrentLatitude,$strCurrentLongitude"
    }

    private fun setCurrentLocation() {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
            if (addressList != null && addressList.size > 0) {
                val strCurrentLocation = addressList[0].locality
                binding.tvLocation.text = strCurrentLocation
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun setViewModel() {
        progressDialog.show()
        surahViewModel = ViewModelProvider(this, NewInstanceFactory()).get(SurahViewModel::class.java)
        surahViewModel.setSurah()
        surahViewModel.getSurah().observe(this, { modelSurah: ArrayList<ModelSurah> ->
            if (modelSurah.size != 0) {
                mainAdapter.setAdapter(modelSurah)
                progressDialog.dismiss()
            } else {
                Toast.makeText(this, "Data Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })
    }

    private fun setPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQ_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_PERMISSION && resultCode == RESULT_OK) {
            // Load data
            setViewModel()
        }
    }
}