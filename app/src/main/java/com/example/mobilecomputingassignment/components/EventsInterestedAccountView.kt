package com.example.mobilecomputingassignment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilecomputingassignment.GlobalNavigation
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.Routes
import com.example.mobilecomputingassignment.model.EventModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EventsInterestedAccountView(modifier: Modifier = Modifier) {
    val eventsInterestedList = remember { mutableStateOf<List<EventModel>>(emptyList()) }
    val eventsList = remember { mutableStateOf<List<EventModel>>(emptyList()) }
    val noInterestedEvents = remember { mutableStateOf(true) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

// Step 1: Fetch all events first
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("events")
            .orderBy("date", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val allEvents = querySnapshot.documents.mapNotNull { doc ->
                    val event = doc.toObject(EventModel::class.java)
                    event?.id = doc.id  // assign the id here
                    event
                }
                eventsList.value = allEvents

                // Step 2: Fetch current user after events fetched
                Firebase.firestore.collection("users").document(currentUserId)
                    .get()
                    .addOnSuccessListener { userDoc ->
                        val eventsInterestedFor = userDoc.get("eventsInterestedFor") as? List<String> ?: emptyList()

                        // Step 3: Filter events that match the interested event ids
                        val interestedEvents = allEvents.filter { it.id in eventsInterestedFor }

                        eventsInterestedList.value = interestedEvents
                        noInterestedEvents.value = interestedEvents.isEmpty()
                    }
                    .addOnFailureListener {
                        // If user fetch fails, no interested events
                        noInterestedEvents.value = true
                    }
            }
            .addOnFailureListener {
                // If events fetch fails, no interested events
                noInterestedEvents.value = true
            }
    }

    if(!noInterestedEvents.value){
        LazyRow {
            items(eventsInterestedList.value){ item->
                EventInterestedAccountItem(event = item)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
    else{
        Text(text = stringResource(id = R.string.accountpage_nointerestedevents_text),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Thin
            )
        )
    }

}

@Composable
fun EventInterestedAccountItem(event : EventModel){
    val date = event.date.toDate()
    val formatter = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(date)

    Card(
        modifier = Modifier.fillMaxWidth()
            .height(100.dp)
            .padding(10.dp)
            .clickable{
                GlobalNavigation.navController.navigate(Routes.eventdetailspage+event.id)
            },
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color(0xFFF87217)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ){
        Column (modifier = Modifier.fillMaxSize().padding(3.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = event.name, modifier = Modifier,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ))
            Text(text = event.location, modifier = Modifier,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ))
            Text(text = formattedDate.toString(), modifier = Modifier,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ))
        }
    }
}