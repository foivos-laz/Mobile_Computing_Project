package com.example.mobilecomputingassignment.model

import com.google.firebase.Timestamp

data class EventModel(
    val askVolunteer : Boolean = false,
    var availableSeats : Int = 0,
    val date : Timestamp = Timestamp.now(),
    val description : String = "",
    var hostedBy : List<String> = emptyList(),
    var id : String = "",
    var imageURL : String = "",
    val location : String = "",
    val name : String = "",
    val needRegistration : Boolean = false,
    val price : Double = 0.0,
    var registeredUsers : List<String> = emptyList(),
    var volunteers : List<String> = emptyList()
)
