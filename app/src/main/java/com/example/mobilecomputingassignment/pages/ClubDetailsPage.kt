package com.example.mobilecomputingassignment.pages

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import coil.compose.AsyncImage
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.components.ClubsHostedEvents
import com.example.mobilecomputingassignment.model.ClubModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.core.net.toUri

@Composable
fun ClubDetailsPage(modifier: Modifier = Modifier, clubID : String) {
    var club by remember{
        mutableStateOf(ClubModel())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("clubs").document(clubID).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    var result = it.result.toObject(ClubModel::class.java)
                    if(result!=null){
                        result.id = it.result.id
                        club = result
                    }
                }
            }
    }

    var context =  LocalContext.current

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
                Text(text = club.name, modifier = Modifier,
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
                Column( modifier = Modifier.padding(20.dp)
                    ,verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    //Image
                    if(club.imageURL != ""){
                        AsyncImage(
                            model = club.imageURL,
                            contentDescription = "Image of the event",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
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

                        Text(text = club.description, modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Justify,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal

                            ))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //Email for contact with the club
                    Column(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally){
                        Text(text = stringResource(id = R.string.loginscreen_textbox_email), modifier = Modifier,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 20.sp,
                            ))

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = club.email, modifier = Modifier
                            .clickable{
                                sendEmailToClub(context, club)
                            },
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFF87217),
                                textDecoration = TextDecoration.Underline
                            ))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //Campus where the club is at
                    Column(modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally){
                            Text(text = stringResource(id = R.string.clubdetailpage_campus_text), modifier = Modifier,
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                ))

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = club.campus, modifier = Modifier,
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF87217)
                                ))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //The events list hosted by the club
                    Column (modifier = Modifier.fillMaxWidth()){
                        Text(text = stringResource(id = R.string.clubdetailpage_hostedevents_text),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            ))

                        Spacer(modifier = Modifier.height(10.dp))

                        ClubsHostedEvents(modifier, clubID)
                    }

                }
            }
        }
    }
}

fun sendEmailToClub(context: Context, club: ClubModel) {
    val toastSuccess = R.string.email_openingemailapp_text
    val toastFail = R.string.email_noemailapp_text

    val uri = "mailto:${club.email}".toUri()
    val intent = Intent(Intent.ACTION_SENDTO, uri)

    if (intent.resolveActivity(context.packageManager) != null) {
        Toast.makeText(context, toastSuccess, Toast.LENGTH_SHORT).show()
        context.startActivity(Intent.createChooser(intent, "Choose an email client"))
    } else {
        Toast.makeText(context, toastFail, Toast.LENGTH_SHORT).show()
    }
}