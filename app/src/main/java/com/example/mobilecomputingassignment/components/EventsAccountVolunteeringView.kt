package com.example.mobilecomputingassignment.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.model.EventModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun EventsAccountVolunteeringView(modifier: Modifier = Modifier) {
    val eventsList = remember {
        mutableStateOf<List<EventModel>>(emptyList())
    }

    val noVolunteeringEvents = remember { mutableStateOf(true) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("events")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val volunteeringEvents = task.result.documents.mapNotNull { doc ->
                        val volunteeringUsers = doc.get("volunteers") as? List<String> ?: emptyList()
                        if (currentUserId in volunteeringUsers) {
                            val event = doc.toObject(EventModel::class.java)
                            event?.id = doc.id
                            event
                        } else {
                            null
                        }
                    }

                    eventsList.value = volunteeringEvents
                    noVolunteeringEvents.value = volunteeringEvents.isEmpty()
                } else {
                    // Handle error if needed
                    noVolunteeringEvents.value = true
                }
            }
    }

    if(!noVolunteeringEvents.value){
        LazyRow {
            items(eventsList.value){item->
                EventAccountItem(event = item)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
    else{
        Text(text = stringResource(id = R.string.accountpage_notvolunteering_text),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Thin
            )
        )
    }

}