package com.example.financekeeper.category.data

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: Int,
    @SerializedName("userId") val userId: Long = 0
)