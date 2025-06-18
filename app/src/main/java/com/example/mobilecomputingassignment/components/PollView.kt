package com.example.mobilecomputingassignment.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilecomputingassignment.AppUtil
import com.example.mobilecomputingassignment.GlobalNavigation
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.Routes
import com.example.mobilecomputingassignment.model.PollModel
import com.example.mobilecomputingassignment.model.ReportModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.math.round

@Composable
fun PollView(modifier: Modifier = Modifier, userID : String) {
    val pollList = remember {
        mutableStateOf<List<PollModel>>(emptyList())
    }
    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("poll")
            .orderBy(
                "creationDate",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            ) // Sort by newest date
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.documents.mapNotNull { doc ->
                        doc.toObject(PollModel::class.java)
                    }
                    pollList.value = result
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .navigationBarsPadding()
        ) {
            items(pollList.value) { item ->
                val poll = remember { mutableStateOf(item) }
                val db = FirebaseFirestore.getInstance()
                val reportsRef = db.collection("reports")

                var tooManyReports by remember {
                    mutableStateOf(false)
                }

                reportsRef
                    .whereEqualTo("idOfReportedItem", item.id)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // Found an existing report
                            val document = querySnapshot.documents[0]
                            val currentAmount = document.getLong("reportAmount") ?: 0
                            Log.e("Error", "Amount of reports for comment:"+item.id+" ,is: "+currentAmount)

                            if(currentAmount >= 15){
                                tooManyReports = true
                            }
                        }
                    }

                if(tooManyReports == true){
                    null
                }
                else{
                    PollItem(Modifier.padding(8.dp), poll, userID)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = {
                GlobalNavigation.navController.navigate(Routes.pollcreationpage)
            },
            modifier = Modifier.padding(25.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 60.dp, end = 0.dp),
            containerColor = Color(0xFFE5BA97)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
    }
}
@Composable
fun PollItem(modifier: Modifier,poll : MutableState<PollModel>, userID: String){
    var context = LocalContext.current

    var enabled by remember {
        mutableStateOf(true)
    }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val currentUserID = currentUser?.uid!!

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color(0xFFF87217)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ){
        val show_answer = remember { mutableStateOf(false) }
        poll.value.answers?.forEach { (_,liste)->
            for (i in liste){
                if (i==userID){
                    show_answer.value=true
                }
            }
        }
        if (show_answer.value){
            ShowAnswer(poll.value,userID)
        }else{
            Column (modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = poll.value.question, modifier = Modifier,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                var nbOfAnswer = remember { mutableIntStateOf(0) }
                val checkedStates = remember {
                    poll.value.choices.associateWith { mutableStateOf(false) }
                }
                for (option in poll.value.choices){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Checkbox(
                            checked = checkedStates[option]?.value ?:false,
                            onCheckedChange = {newValue -> checkedStates[option]?.value = newValue
                                if (newValue){
                                    nbOfAnswer.value++
                                } else {
                                    nbOfAnswer.value--
                                }
                            }
                        )
                        Text(text = option,
                            modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
                val showProblem = remember{mutableStateOf(false)}
                Button(onClick = {
                    if (!poll.value.multipleChoice && nbOfAnswer.value>1){
                        showProblem.value = true
                    } else if (nbOfAnswer.value>0){
                        showProblem.value = false
                        checkedStates.forEach { (choice, state) ->
                            if (state.value) {
                                SubmitVote(poll.value.id, userID, choice)
                                val updatedAnswers = poll.value.answers?.toMutableMap().apply {
                                    val updatedList = this?.get(choice)?.toMutableList() ?: mutableListOf()
                                    updatedList.add(userID)
                                    this?.set(choice, updatedList)
                                }
                                poll.value = poll.value.copy(answers = updatedAnswers)
                            }
                            state.value = false
                        }
                    } else {
                        showProblem.value = false
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF87217),
                    contentColor = Color.White)){
                    Text(text = stringResource(id = R.string.commentpage_button1),
                        fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
                }

                if(currentUserID == poll.value.creatorID){
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedButton (onClick = {
                        GlobalNavigation.navController.navigate(Routes.polldetailspage+poll.value.id)
                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFFF87217))) {
                        Text(text = stringResource(id = R.string.pollsplage_details_text))
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = stringResource(id = R.string.commentsview_report_text), modifier = Modifier.padding(10.dp, 0.dp)
                        .clickable(enabled = enabled){
                            val db = FirebaseFirestore.getInstance()
                            val reportsRef = db.collection("reports")

                            reportsRef
                                .whereEqualTo("idOfReportedItem", poll.value.id)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        // Found an existing report
                                        val document = querySnapshot.documents[0]
                                        val currentAmount = document.getLong("reportAmount") ?: 0
                                        val updatedAmount = currentAmount + 1

                                        document.reference.update("reportAmount", updatedAmount)
                                            .addOnSuccessListener {
                                                Log.d("Report", "Report updated successfully.")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Report", "Failed to update report: ${e.message}")
                                            }
                                    } else {
                                        // No existing report found, create a new one
                                        val newReport = ReportModel(
                                            idOfReportedItem = poll.value.id,
                                            reportAmount = 1
                                        )

                                        reportsRef.add(newReport)
                                            .addOnSuccessListener {
                                                Log.d("Report", "New report created successfully.")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("Report", "Failed to create new report: ${e.message}")
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Report", "Failed to fetch reports: ${e.message}")
                                }
                        },
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFF87217),
                        textDecoration = TextDecoration.Underline

                    )
                )

                if (showProblem.value){
                    val toast1 = stringResource(id = R.string.pollpage_error_text)
                    AppUtil.showToast(context, toast1)
                }
            }
        }
    }
}

@Composable
fun ShowAnswer(poll : PollModel, userID: String){
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val currentUserID = currentUser?.uid!!

    val choiceListe = poll.choices.associate { it to 0 }.toMutableMap()
        //mutableMapOf<String, Int>()
    val userChoice : MutableList<String> = remember { mutableListOf("") }
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(3.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = poll.question, fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(15.dp))

        poll.answers?.forEach{(choice, userList) ->
            choiceListe[choice]=userList.size
            if (userID in userList){
                userChoice.add(choice)
            }
        }
        userChoice.drop(1)
        val total : Int = choiceListe.values.sum()
        choiceListe.forEach { (choice, num) ->
            val percentage = if (total > 0) num.toFloat() / total else 0f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "$choice :",
                    fontWeight = if (choice in userChoice) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .background(Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(percentage.coerceAtLeast(0.01f)) // ensure visibility
                            .background(Color(0xFF4CAF50))
                    ) {
                        if (percentage > 0f) {
                            Text(
                                text = "${round(percentage * 100)}%",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        if(currentUserID == poll.creatorID){
            Spacer(modifier = Modifier.height(5.dp))

            OutlinedButton (onClick = {
                GlobalNavigation.navController.navigate(Routes.polldetailspage+poll.id)
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFF87217))) {
                Text(text = stringResource(id = R.string.pollsplage_details_text))
            }
        }
    }
}

fun SubmitVote(pollID: String, userID : String, choice : String){
    val db = FirebaseFirestore.getInstance()
    db.collection("poll")
        .document(pollID)
        .update("answers."+choice, FieldValue.arrayUnion(userID))
}
