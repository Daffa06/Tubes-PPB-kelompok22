package com.bismillah.alquran.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bismillah.alquran.databinding.ListItemAyatBinding
import com.bismillah.alquran.model.main.ModelAyat

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    private val modelAyatList = ArrayList<ModelAyat>()

    fun setAdapter(items: ArrayList<ModelAyat>) {
        modelAyatList.clear()
        modelAyatList.addAll(items)
        notifyDataSetChanged()
}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemAyatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelAyatList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return modelAyatList.size
    }

    // Class Holder
    class ViewHolder(private val binding: ListItemAyatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ModelAyat) {
            binding.tvNomorAyat.text = data.nomor
            binding.tvArabic.text = data.arab
            binding.tvTerjemahan.text = data.indo
        }
    }
}