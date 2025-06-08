package com.example.mobilecomputingassignment.model

import com.google.firebase.Timestamp

data class CommentModel(
    val userID : String,
    val name : String,
    val comment : String,
    val timestamp: Timestamp,
)
