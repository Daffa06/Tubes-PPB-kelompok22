package com.bismillah.alquran.model.response

import com.google.gson.annotations.SerializedName
import com.bismillah.alquran.model.nearby.ModelResults

class ModelResultNearby {
    @SerializedName("results")
    lateinit var modelResults: List<ModelResults>
}