package com.example.mobilecomputingassignment.pages

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.mobilecomputingassignment.AppUtil
import com.example.mobilecomputingassignment.GlobalNavigation
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.Routes
import com.example.mobilecomputingassignment.model.ClubModel
import com.example.mobilecomputingassignment.model.EventModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun EventDetailsPage(modifier: Modifier = Modifier, eventID : String) {
    var event by remember{
        mutableStateOf(EventModel())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("events").document(eventID).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    var result = it.result.toObject(EventModel::class.java)
                    if(result!=null){
                        result.id = it.result.id
                        event = result
                    }
                }
            }
    }

    var context = LocalContext.current

    val toastCalPerDenied = stringResource(id = R.string.toastcalendar_permission_denied)
    val toastAlreadyRegistered = stringResource(id = R.string.eventdetailspage_alreadyregistred_toast)
    val toastNoAvailableSeats = stringResource(id = R.string.eventsdetailspage_nomoreavailableseats)
    val toastSuccessfullyRegistered = stringResource(id = R.string.eventdetailspage_successfullyregistred_toast)
    val toastVolunteerRegistrationSuccess = stringResource(id = R.string.eventsdetailspage_volunteersuccessfulregistration_toast)
    val toastAlreadyRegisteredVolunteer = stringResource(id = R.string.eventdetailspage_alreadyvolunteer_toast)

    //To get permission to write to calendar
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            // Permissions granted, now try to add the event
            addEventToCalendar(context, event)
        } else {
            // Permissions denied
            Toast.makeText(context, toastCalPerDenied, Toast.LENGTH_SHORT).show()
        }
    }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var isRegistered by remember { mutableStateOf(false) }

    val currentUser = auth.currentUser
    val currentUserId = currentUser?.uid!!

    val hostingClubsState = remember { mutableStateOf<List<ClubModel>>(emptyList()) }

    LaunchedEffect(event.id) {
        if (!event.id.isNullOrEmpty()) {
            try {
                val eventSnapshot = db.collection("events").document(event.id!!).get().await()
                val hostedByIds = eventSnapshot.get("hostedBy") as? List<String> ?: emptyList()

                Log.d("EventDetails", "Host IDs: $hostedByIds")

                val loadedClubs = mutableListOf<ClubModel>()
                for (id in hostedByIds) {
                    val doc = db.collection("clubs").document(id).get().await()
                    if (doc.exists()) {
                        val club = doc.toObject(ClubModel::class.java)
                        if (club != null) {
                            club.id = doc.id
                            loadedClubs.add(club)
                            Log.d("EventDetails", "Loaded club: ${club.name}")
                        }
                    } else {
                        Log.w("EventDetails", "Club doc $id does not exist")
                    }
                }

                hostingClubsState.value = loadedClubs

            } catch (e: Exception) {
                Log.e("EventDetails", "Failed to load hosting clubs", e)
            }
        }
        if (event.id?.isNotEmpty() == true) {
            val eventRef = db.collection("events").document(event.id!!)

            db.runTransaction { transaction ->
                val eventSnapshot = transaction.get(eventRef)
                val hostedBy = eventSnapshot.get("hostedBy") as? List<String> ?: emptyList()

                // First, read all club documents
                val clubSnapshots = hostedBy.map { clubId ->
                    val clubRef = db.collection("clubs").document(clubId)
                    clubId to transaction.get(clubRef)
                }

                // Then, do all writes
                for ((clubId, clubSnapshot) in clubSnapshots) {
                    val clubRef = db.collection("clubs").document(clubId)
                    if (clubSnapshot.exists()) {
                        transaction.update(clubRef, "eventList", FieldValue.arrayUnion(event.id))
                    }
                }
            }.addOnSuccessListener {
                Log.d("EventDetails", "Successfully updated clubs with event ID.")
            }.addOnFailureListener { e ->
                Log.e("EventDetails", "Transaction failed: ${e.message}")
            }
        }
    }

    val date = event.date.toDate()
    val formatter = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(date)

    Card(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color(0xFFF87217)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ){
        val scrollState = rememberScrollState()

        //Event Name Area, doesn't scroll with the rest
        Column(modifier = Modifier.fillMaxSize().padding(3.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally){
            Column(modifier = Modifier.fillMaxWidth().padding(3.dp)
                .background(Color(0xFFE3A370), shape = RoundedCornerShape(5.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = event.name, modifier = Modifier,
                    style = TextStyle(
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold
                    ))
            }

            //For the rest of the events content
        Column (
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
        ){
                //The are of the rest of the information
                Column( modifier = Modifier.padding(10.dp)
                    ,verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    //Image
                    if(event.imageURL != ""){
                        AsyncImage(
                            model = event.imageURL,
                            contentDescription = "Image of the event",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    //Description
                    Column(modifier = Modifier.fillMaxWidth()
                        .background(Color(0xFFE3A370), shape = RoundedCornerShape(10.dp)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text = stringResource(id = R.string.eventdetailspage_description), modifier = Modifier
                            .background(Color(0xFFF87217), shape = RoundedCornerShape(5.dp)).fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                            ))

                        //Spacer(modifier = Modifier.height(10.dp))

                        Text(text = event.description, modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Justify,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal

                            ))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Location Information
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = stringResource(id = R.string.eventdetailspage_wheretext), modifier = Modifier,
                            style = TextStyle(
                                fontSize = 20.sp,
                            ))

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(text = event.location, modifier = Modifier,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFF87217)
                            ))


                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Date Information
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically){
                        Text(text = stringResource(id = R.string.eventdetailspage_whentext), modifier = Modifier,
                            style = TextStyle(
                                fontSize = 20.sp,
                            ))

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(text = formattedDate, modifier = Modifier,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFF87217)
                            ))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //Price Information
                    Column(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically){
                            Text(text = stringResource(id = R.string.eventdetailspage_pricetext), modifier = Modifier,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                ))

                            Spacer(modifier = Modifier.width(10.dp))
                            if(event.price > 0.0 ){
                                Text(text = event.price.toString(), modifier = Modifier,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF87217)
                                    ))
                            }
                            else {
                                Text(text = stringResource(id = R.string.eventdetailspage_pricefree_text), modifier = Modifier,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF87217)
                                    ))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //Seats Information
                    if(event.needRegistration == true){
                        Column(modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically){
                                Text(text = stringResource(id = R.string.eventdetailspage_availableseatstwext), modifier = Modifier,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                    ))

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(text = event.availableSeats.toString(), modifier = Modifier,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFF87217)
                                    ))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Host Information
                    val hostingClubs = hostingClubsState.value
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.eventdetailspage_hostebytext),
                            style = TextStyle(fontSize = 20.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (hostingClubs.isEmpty()) {
                            Text("No hosts available")
                        }

                        for (club in hostingClubs) {
                            Text(
                                text = club.name,
                                modifier = Modifier
                                    .clickable{
                                        GlobalNavigation.navController.navigate(Routes.clubdetailspage+club.id)
                                    },
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF87217),
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Interested Button
                    Column(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text = stringResource(id = R.string.eventdetailspage_interestedtext), modifier = Modifier,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ))

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(onClick = {
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(currentUser.uid)
                                .update("eventsInterestedFor", FieldValue.arrayUnion(event.id))

                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.WRITE_CALENDAR
                                ) == PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_CALENDAR
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                // Permissions already granted, proceed directly
                                addEventToCalendar(context, event)
                            } else {
                                // Permissions not granted, launch the request
                                // This call is now safe because requestPermissionLauncher is initialized
                                requestPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.WRITE_CALENDAR,
                                        Manifest.permission.READ_CALENDAR
                                    )
                                )
                            }
                        },modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF87217),
                                contentColor = Color.White)){
                            Text(text = stringResource(id = R.string.eventdetailspage_interested_button), fontSize = 22.sp,
                                fontWeight = FontWeight.Normal)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Comments Button
                    Column(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text = stringResource(id = R.string.eventdetailspage_commentstext), modifier = Modifier,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ))

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(onClick = {
                            GlobalNavigation.navController.navigate(Routes.commentspage+event.id)
                        },modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF87217),
                                contentColor = Color.White)){
                            Text(text = stringResource(id = R.string.eventdetailspage_commentsbutton), fontSize = 22.sp,
                                fontWeight = FontWeight.Normal)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Registration Information
                    if(event.needRegistration == true){
                        Column(modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = stringResource(id = R.string.eventdetailspage_registrationneeded), modifier = Modifier,
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal
                                ))

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (event.availableSeats > 0 && event.needRegistration) {
                                        val ref = db.collection("events").document(event.id!!)
                                        db.runTransaction { transaction ->
                                            val snapshot = transaction.get(ref)
                                            val seats = snapshot.getLong("availableSeats") ?: 0L
                                            val registeredUsers = snapshot.get("registeredUsers") as? List<String> ?: emptyList()

                                            // Fail if already registered
                                            if (currentUserId in registeredUsers) {
                                                throw Exception("Already registered")
                                            }

                                            // Fail if full
                                            if (seats <= 0) {
                                                throw Exception("No seats left")
                                            }

                                            // Update data
                                            transaction.update(ref, mapOf(
                                                "availableSeats" to seats - 1,
                                                "registeredUsers" to FieldValue.arrayUnion(currentUserId)
                                            ))

                                        }.addOnSuccessListener {
                                            isRegistered = true
                                            AppUtil.showToast(context, toastSuccessfullyRegistered)
                                        }.addOnFailureListener { e ->
                                            val message = when (e.message) {
                                                "Already registered" -> toastAlreadyRegistered
                                                "No seats left" -> toastNoAvailableSeats
                                                else -> "Failed to register: ${e.message}"
                                            }
                                            AppUtil.showToast(context, message)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF87217),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.eventdetailspage_register),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Normal,
                                )
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //Volunteer Information
                    if (event.askVolunteer == true) {
                        Column(modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally){
                            val context = LocalContext.current

                            Text(text = stringResource(id = R.string.eventdetailspage_volunteertext), modifier = Modifier,
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal
                                ))

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
                                    val eventRef = db.collection("events").document(event.id!!)

                                    db.runTransaction { transaction ->
                                        val snapshot = transaction.get(eventRef)

                                        val currentVolunteers = snapshot.get("volunteers") as? List<String> ?: emptyList()

                                        if (currentUserId in currentVolunteers) {
                                            throw Exception("Already volunteering")
                                        }

                                        // Add user ID to volunteers
                                        transaction.update(eventRef, "volunteers", FieldValue.arrayUnion(currentUserId))
                                    }.addOnSuccessListener {
                                        AppUtil.showToast(context, toastVolunteerRegistrationSuccess)
                                    }.addOnFailureListener { e ->
                                        val message = when (e.message) {
                                            "Already volunteering" -> toastAlreadyRegisteredVolunteer
                                            else -> "Something went wrong: ${e.message}"
                                        }
                                        AppUtil.showToast(context, message)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color(0xFFF87217)
                                )
                            ) {
                                Text(
                                    text = stringResource(id = R.string.eventdetailspage_volunteerbutton),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun addEventToCalendar(context: Context, event: EventModel) {
    val toastOpeningCalendar = R.string.eventdetailspage_openingcalendartoast
    val toastNoCalendarFound = R.string.eventdetailspage_nocalendarfound_toast

    // Convert Firebase Timestamp to milliseconds for calendar event
    val startMillis = event.date.toDate().time

    // Optionally, define event duration (e.g., 1 hour)
    val durationMillis = 60 * 60 * 1000  // 1 hour in milliseconds
    val endMillis = startMillis + durationMillis

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, event.name)
        putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        putExtra(CalendarContract.Events.DESCRIPTION, event.description)
    }

    Log.d("CalendarEvent", "Attempting to add event: ${event.name}")
    Log.d("CalendarEvent", "Start Time: $startMillis, End Time: $endMillis")

    // Verify there's an app to handle this intent before launching
    if (intent.resolveActivity(context.packageManager) != null) {
        Log.d("CalendarEvent", "Calendar app found, launching intent.")
        context.startActivity(intent)
        Toast.makeText(context, toastOpeningCalendar, Toast.LENGTH_SHORT).show()

    } else {
        // Handle the case where no calendar app is installed
        Toast.makeText(context, toastNoCalendarFound, Toast.LENGTH_SHORT).show()
    }
}