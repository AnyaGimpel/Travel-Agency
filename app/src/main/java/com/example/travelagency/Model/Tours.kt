package com.example.travelagency.Model

import java.util.Date

data class Tours(
    var id: String? = null,

    val name: String? = null,
    val cities: String? = null,
    val description: String? = null,
    val type: String? = null,
    val key_words: String? = null,
    val transport: String? = null,
    val id_hotel: String? = null,
    val img: String? = null,

    val cost: Double? = null,

    var dateTimeTo: Date? = null,
    var dateTimeFrom: Date? = null,

    )