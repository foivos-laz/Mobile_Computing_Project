package com.example.mobilecomputingassignment.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.Routes
import com.example.mobilecomputingassignment.components.EventsAccountView
import com.example.mobilecomputingassignment.components.EventsAccountVolunteeringView
import com.example.mobilecomputingassignment.components.EventsInterestedAccountView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun AccountPage(modifier: Modifier = Modifier, navController: NavHostController, name1: String) {

    val orange = Color(0xFFF87217)

    var enabled by remember {
        mutableStateOf(true)
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column (modifier = Modifier.fillMaxWidth().padding(0.dp, 20.dp)
        ){
            Text(text = stringResource(id = R.string.accountpage_text1) + " $name1",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 30.sp,
                    //fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        //This area handles the interested for events
        Column (modifier = Modifier.fillMaxWidth()){
            Text(text = stringResource(id = R.string.accountpage_interestedevents_text),
                modifier = Modifier.fillMaxWidth(),
                //textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ))

            Spacer(modifier = Modifier.height(10.dp))

            EventsInterestedAccountView(modifier)
        }

        Spacer(modifier = Modifier.height(20.dp))

        //This area handles the registered events
        Column (modifier = Modifier.fillMaxWidth()){
            Text(text = stringResource(id = R.string.accountpage_registredevents_text),
                modifier = Modifier.fillMaxWidth(),
                //textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ))

            Spacer(modifier = Modifier.height(10.dp))

            EventsAccountView(modifier)
        }

        Spacer(modifier = Modifier.height(20.dp))

        //This area handles the volunteering events
        Column (modifier = Modifier.fillMaxWidth()){
            Text(text = stringResource(id = R.string.accountpage_volunteeringin_text),
                modifier = Modifier.fillMaxWidth(),
                //textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                ))

            Spacer(modifier = Modifier.height(10.dp))

            EventsAccountVolunteeringView(modifier)
        }

        Spacer(modifier = Modifier.height(10.dp))

        //This is for the change username area of the page
        Column (modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = stringResource(id = R.string.accountpage_text3),
                modifier = Modifier
                    .clickable(enabled = enabled){
                        enabled = false
                        navController.navigate(Routes.namechangescreen)
                    },
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 20.sp,
                    color = orange
                )
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        //This handles the logout ui
        Column (modifier = Modifier.fillMaxWidth()){
            Text(text = stringResource(id = R.string.accountpage_text2),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Thin
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column (modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center){
                OutlinedButton(onClick = {
                    Firebase.auth.signOut()
                    navController.navigate(Routes.authscreen){
                        popUpTo(Routes.homescreen){inclusive = true}
                    }
                }, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFF87217)
                    )) {
                    Text(text = stringResource(id = R.string.accountpage_logout_button)
                        , fontSize = 15.sp, fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}