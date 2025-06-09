package com.example.mobilecomputingassignment.model

data class ClubModel(
    val description : String = "",
    var eventList : List<String> = emptyList(),
    var id : String = "",
    val name : String = ""
)
