package com.example.mobilecomputingassignment.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import com.example.mobilecomputingassignment.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobilecomputingassignment.AppUtil
import com.example.mobilecomputingassignment.Routes
import com.example.mobilecomputingassignment.viewmodel.AuthViewModel

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }
    var enabled by remember {
        mutableStateOf(true)
    }

    val orange = Color(0xFFF87217)

    var context = LocalContext.current

    val toast = stringResource(id = R.string.toast_genericerror)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = stringResource(id = R.string.loginscreen_text1),
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontSize = 30.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ))

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = stringResource(id = R.string.loginscreen_textbox_email))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = stringResource(id = R.string.loginscreen_textbox_password))
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            authViewModel.login(email, password){success, errorMessage->
            if(success){
            navController.navigate(Routes.homescreen){
                popUpTo(Routes.authscreen) {inclusive = true}
            }
            }
              else{
             AppUtil.showToast(context, errorMessage?:toast)

        }}}, modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = orange,
                contentColor = Color.White
            )){
            Text(text = stringResource(id = R.string.authscreen_button1)
                , fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.loginscreen_text2),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

           Text(
                text = stringResource(id = R.string.loginscreen_text3),
                modifier = Modifier
                    .clickable(enabled = enabled){
                    enabled = false
                    navController.navigate(Routes.signupscreen)
                },
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 15.sp,
                    color = orange
                )
            )
        }

    }
}