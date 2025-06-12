package com.example.mobilecomputingassignment.model

import com.google.firebase.Timestamp

data class CommentModel(
    val comment : String = "",
    val id : String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val userID : String = "",
    val userName : String = ""
)
