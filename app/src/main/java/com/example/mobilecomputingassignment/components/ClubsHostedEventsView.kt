package com.example.mobilecomputingassignment.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
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
import com.example.mobilecomputingassignment.model.ClubModel
import com.example.mobilecomputingassignment.model.EventModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

@Composable
fun ClubsHostedEvents(modifier: Modifier = Modifier, clubID : String) {
    val eventsList = remember { mutableStateOf<List<EventModel>>(emptyList()) }
    val noHostedEvents = remember { mutableStateOf(true) }
    val eventListeners = remember { mutableStateListOf<ListenerRegistration>() }
    val tempEvents = remember { mutableStateListOf<EventModel>() } // FIXED

    DisposableEffect(clubID) {
        val firestore = Firebase.firestore

        val clubListener = firestore.collection("clubs").document(clubID)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    eventsList.value = emptyList()
                    noHostedEvents.value = true
                    return@addSnapshotListener
                }

                val club = snapshot.toObject(ClubModel::class.java)
                val clubEventIds = club?.eventList ?: emptyList()

                eventListeners.forEach { it.remove() }
                eventListeners.clear()
                tempEvents.clear()

                if (clubEventIds.isEmpty()) {
                    eventsList.value = emptyList()
                    noHostedEvents.value = true
                    return@addSnapshotListener
                }

                clubEventIds.forEach { eventId ->
                    val listener = firestore.collection("events").document(eventId)
                        .addSnapshotListener { eventSnapshot, eventError ->
                            if (eventError != null || eventSnapshot == null || !eventSnapshot.exists()) return@addSnapshotListener

                            val event = eventSnapshot.toObject(EventModel::class.java)?.apply {
                                id = eventSnapshot.id
                            }

                            event?.let {
                                val index = tempEvents.indexOfFirst { it.id == event.id }
                                if (index != -1) {
                                    tempEvents[index] = it
                                } else {
                                    tempEvents.add(it)
                                }

                                // Sort if needed
                                tempEvents.sortBy { it.date }
                                eventsList.value = tempEvents.toList()
                                noHostedEvents.value = tempEvents.isEmpty()
                            }
                        }

                    eventListeners.add(listener)
                }
            }

        onDispose {
            clubListener.remove()
            eventListeners.forEach { it.remove() }
            eventListeners.clear()
            tempEvents.clear()
        }
    }

    if(!noHostedEvents.value){
        LazyRow(modifier = Modifier// This gives height constraints
            .fillMaxWidth()
        ) {
            items(eventsList.value){item->
                EventAccountItem(event = item)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
    else{
        Text(text = stringResource(id = R.string.clubdetailspage_nohostedevents_text),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Thin
            )
        )
    }

}