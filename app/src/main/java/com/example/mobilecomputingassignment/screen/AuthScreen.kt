package com.example.mobilecomputingassignment.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mobilecomputingassignment.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun AuthScreen(modifier: Modifier = Modifier){
    Column(modifier = modifier
        .fillMaxSize()
        .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Image(
            painter = painterResource(id = R.drawable.logo_transparent),
            contentDescription = "Banner",
            modifier = Modifier
               // .fillMaxSize()
        )

        Text(
            text = stringResource(id = R.string.authscreen_text1),
            style = TextStyle(
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {}, modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)){
            Text(text = stringResource(id = R.string.authscreen_button1))
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(onClick = {}, modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)) {
            Text(text = stringResource(id = R.string.authscreen_button2))
        }
    }
}