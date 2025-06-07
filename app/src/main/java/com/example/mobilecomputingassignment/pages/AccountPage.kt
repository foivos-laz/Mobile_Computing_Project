package com.example.mobilecomputingassignment.pages

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.Routes

@Composable
fun AccountPage(modifier: Modifier = Modifier, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()
        .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //This is for the Welcome message
        Column (modifier = Modifier.fillMaxWidth().padding(0.dp, 20.dp)){
            Text(text = stringResource(id = R.string.accountpage_text1),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
            //The name here is for testing reasons...
            //must be replaced later with firebase call
            Text(text = "Emma",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(520.dp))

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
                    //Firebase.auth.signOut()
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