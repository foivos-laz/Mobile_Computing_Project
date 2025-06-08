package com.example.mobilecomputingassignment.model

import com.google.firebase.Timestamp

data class ClubModel(
    val description : String = "",
    var eventList : List<String> = emptyList(),
    var id : String = "",
    val name : String = ""
)
