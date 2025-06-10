package com.example.mobilecomputingassignment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mobilecomputingassignment.GlobalNavigation
import com.example.mobilecomputingassignment.Routes
import com.example.mobilecomputingassignment.model.ClubModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
@Composable
fun ClubsView(modifier: Modifier = Modifier) {
    val clubsList = remember {
        mutableStateOf<List<ClubModel>>(emptyList())
    }


    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("clubs")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.documents.mapNotNull { doc ->
                        doc.toObject(ClubModel::class.java)
                    }
                    clubsList.value = result
                }
            }
    }

    LazyColumn {
        items(clubsList.value){ item->
            ClubItem(club = item)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ClubItem(club : ClubModel){

    Card(
        modifier = Modifier.fillMaxWidth().size(100.dp)
            .clickable{
                GlobalNavigation.navController.navigate(Routes.clubdetailspage+club.id)
            },
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color(0xFFF87217)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ){
        Column (modifier = Modifier.fillMaxSize().padding(3.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = club.name, modifier = Modifier,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ))
        }

    }
}