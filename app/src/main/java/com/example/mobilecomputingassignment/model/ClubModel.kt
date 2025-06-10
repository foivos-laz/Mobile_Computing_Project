package com.example.mobilecomputingassignment.model

data class ClubModel(
    val campus : String = "",
    val description : String = "",
    var eventList : List<String> = emptyList(),
    var id : String = "",
    var imageURL : String = "",
    val name : String = ""
)
