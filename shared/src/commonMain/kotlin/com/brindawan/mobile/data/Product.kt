package com.brindawan.mobile.data

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val variations: List<ProductVariation>,
)

data class ProductVariation(
    val label: String,
    val price: String,
    val imageUrl: String,
)

