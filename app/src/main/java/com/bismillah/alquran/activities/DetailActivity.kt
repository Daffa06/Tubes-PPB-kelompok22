package com.bismillah.alquran.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.bismillah.alquran.R
import com.bismillah.alquran.adapter.DetailAdapter
import com.bismillah.alquran.databinding.ActivityDetailBinding
import com.bismillah.alquran.model.main.ModelAyat
import com.bismillah.alquran.model.main.ModelSurah
import com.bismillah.alquran.viewmodel.SurahViewModel
import java.io.IOException
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var strNomor: String
    private lateinit var strNama: String
    private lateinit var strArti: String
    private lateinit var strType: String
    private lateinit var strAyat: String
    private lateinit var strKeterangan: String
    private lateinit var strAudio: String
    private lateinit var modelSurah: ModelSurah
    private lateinit var detailAdapter: DetailAdapter
    private lateinit var progressDialog: ProgressDialog
    private lateinit var surahViewModel: SurahViewModel
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setInitLayout()
        setViewModel()
    }

    @SuppressLint("RestrictedApi")
    private fun setInitLayout() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handler = Handler()

        modelSurah = intent.getSerializableExtra(DETAIL_SURAH) as ModelSurah
        if (modelSurah != null) {
            strNomor = modelSurah.nomor
            strNama = modelSurah.nama
            strArti = modelSurah.arti
            strType = modelSurah.type
            strAyat = modelSurah.ayat
            strAudio = modelSurah.audio
            strKeterangan = modelSurah.keterangan

            binding.fabStop.visibility = View.GONE
            binding.fabPlay.visibility = View.VISIBLE

            // Set text
            binding.tvHeader.text = strNama
            binding.tvTitle.text = strNama
            binding.tvSubTitle.text = strArti
            binding.tvInfo.text = "$strType - $strAyat Ayat"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvKet.text = Html.fromHtml(strKeterangan, Html.FROM_HTML_MODE_COMPACT)
            } else {
                binding.tvKet.text = Html.fromHtml(strKeterangan)
            }

            val mediaPlayer = MediaPlayer()

            binding.fabPlay.setOnClickListener {
                try {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mediaPlayer.setDataSource(strAudio)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.fabPlay.visibility = View.GONE
                binding.fabStop.visibility = View.VISIBLE
            }

            binding.fabStop.setOnClickListener {
                mediaPlayer.stop()
                mediaPlayer.reset()
                binding.fabPlay.visibility = View.VISIBLE
                binding.fabStop.visibility = View.GONE
            }
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Sedang menampilkan data...")

        detailAdapter = DetailAdapter()
        binding.rvAyat.setHasFixedSize(true)
        binding.rvAyat.layoutManager = LinearLayoutManager(this)
        binding.rvAyat.adapter = detailAdapter
    }

    private fun setViewModel() {
        progressDialog.show()
        surahViewModel = ViewModelProvider(this, NewInstanceFactory()).get(SurahViewModel::class.java)
        surahViewModel.setDetailSurah(strNomor)
        surahViewModel.getDetailSurah()
            .observe(this, { modelAyat: ArrayList<ModelAyat> ->
                if (modelAyat.size != 0) {
                    detailAdapter.setAdapter(modelAyat)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(this, "Data Tidak Ditemukan!", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                progressDialog.dismiss()
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DETAIL_SURAH = "DETAIL_SURAH"
    }
}