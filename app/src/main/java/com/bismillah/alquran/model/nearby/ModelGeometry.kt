package com.bismillah.alquran.model.nearby

import com.google.gson.annotations.SerializedName
import com.bismillah.alquran.model.nearby.ModelLocation

class ModelGeometry {
    @SerializedName("location")
    lateinit var modelLocation: ModelLocation
}