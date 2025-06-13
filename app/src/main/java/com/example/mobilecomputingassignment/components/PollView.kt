package com.example.mobilecomputingassignment.components

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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilecomputingassignment.AppUtil
import com.example.mobilecomputingassignment.GlobalNavigation
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.Routes
import com.example.mobilecomputingassignment.model.EventModel
import com.example.mobilecomputingassignment.model.PollModel
import com.example.mobilecomputingassignment.pages.PollsPage
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Hashtable
import java.util.Locale
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
                PollItem(Modifier.padding(8.dp), poll, userID)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Button(
            onClick = {GlobalNavigation.navController.navigate(Routes.pollcreationpage) },
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 100.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF87217),
                contentColor = Color.White)
        ) {
            Text(text = stringResource(id = R.string.pollpage_createpoll_button), fontSize = 18.sp,
                fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
        }
    }
}
@Composable
fun PollItem(modifier: Modifier,poll : MutableState<PollModel>, userID: String){
    var context = LocalContext.current

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
            showAnswer(poll.value,userID)
        }else{
            Column (modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = poll.value.question, modifier = Modifier,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                var nbOfAnswer = remember { mutableStateOf(0) }
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
                            onCheckedChange = {newValue -> checkedStates[option]?.value = newValue;
                                if (newValue){
                                    nbOfAnswer.value++;
                                } else {
                                    nbOfAnswer.value--;
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
                                submitVote(poll.value.id, userID, choice)
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
                        showProblem.value = false;
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF87217),
                    contentColor = Color.White)){
                    Text(text = stringResource(id = R.string.commentpage_button1),
                        fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
                }

                if (showProblem.value){
                    val toast1 = stringResource(id = R.string.pollpage_error_text)
                    AppUtil.showToast(context, toast1)
                }
            }
        }
    }
}

@Composable
fun showAnswer(poll : PollModel, userID: String){
    val choiceListe = poll.choices.associate { it to 0 }.toMutableMap()
        //mutableMapOf<String, Int>()
    val userChoice : MutableList<String> = remember { mutableListOf("") }
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(3.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = poll.question)
        poll.answers?.forEach{(choice, userList) ->
            choiceListe[choice]=userList.size
            if (userID in userList){
                userChoice.add(choice)
            }
        }
        userChoice.drop(1)
        val total : Int = choiceListe.values.sum()
        choiceListe.forEach{(choice, num) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ){
                if (choice in userChoice){
                    Text(text = choice+ " : ",
                        fontWeight = FontWeight.Bold)
                } else {
                    Text(text = choice + " : ")
                }

                Box(
                    modifier = Modifier
                        .weight(3f)
                        .height(20.dp)
                        .background(Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(num.toFloat()/total)
                            .background(Color(0xFF4CAF50))
                    ){Text(text=round(num.toDouble()/total*100).toString()+"%",
                        modifier = Modifier.align(Alignment.Center)
                    )}
                }
            }
        }
    }
}

fun submitVote(pollID: String, userID : String, choice : String){
    val db = FirebaseFirestore.getInstance()
    db.collection("poll")
        .document(pollID)
        .update("answers."+choice, FieldValue.arrayUnion(userID))
}
