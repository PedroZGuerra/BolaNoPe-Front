package com.uri.bolanope.model

data class MostReservedTimesModel (
    val id_field: String,
    val most_reserved_times: List<MostReservedTimesItemModel>,
)

data class MostReservedTimesItemModel (
    val count: Int,
    val time: String,
)
