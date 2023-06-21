package com.bismillah.alquran.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bismillah.alquran.activities.DetailActivity
import com.bismillah.alquran.databinding.ListItemSurahBinding
import com.bismillah.alquran.model.main.ModelSurah

class MainAdapter (private val mContext: Context) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    private val modelSurahList = ArrayList<ModelSurah>()

    fun setAdapter(items: ArrayList<ModelSurah>) {
        modelSurahList.clear()
        modelSurahList.addAll(items)
        notifyDataSetChanged()
}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemSurahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelSurahList[position]
        holder.bind(data)

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, DetailActivity::class.java)
            intent.putExtra(DetailActivity.DETAIL_SURAH, modelSurahList[position])
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return modelSurahList.size
    }

    // Class Holder
    class ViewHolder(private val binding: ListItemSurahBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ModelSurah) {
            binding.tvNumber.text = data.nomor
            binding.tvAyat.text = data.nama
            binding.tvInfo.text = "${data.type} - ${data.ayat} Ayat"
            binding.tvName.text = data.asma
        }
    }
}