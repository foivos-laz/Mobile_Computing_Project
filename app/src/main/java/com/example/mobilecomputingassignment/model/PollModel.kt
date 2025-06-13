package com.example.mobilecomputingassignment.model

import com.google.firebase.Timestamp

data class PollModel (
    val id : String ="",
    var question : String = "",
    var creationDate : Timestamp = Timestamp.now(),
    var choices : List<String> = emptyList(),
    var answers : Map<String, List<String>>? = null,
    var multipleChoice : Boolean = true
)