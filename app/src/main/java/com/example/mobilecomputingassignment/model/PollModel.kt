package com.example.mobilecomputingassignment.model

import com.google.firebase.Timestamp

data class PollModel (
    var id : String ="",
    var question : String = "",
    var creationDate : Timestamp = Timestamp.now(),
    val creatorID : String = "",
    var choices : List<String> = emptyList(),
    var answers : Map<String, List<String>>? = null,
    var multipleChoice : Boolean = true
)