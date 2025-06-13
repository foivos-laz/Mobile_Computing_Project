package com.example.mobilecomputingassignment.pages

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
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
import com.example.mobilecomputingassignment.Routes
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.model.DisallowedWordsModel
import com.google.firebase.Timestamp

@Composable
fun PollCreationPage(modifier: Modifier = Modifier) {
    var disallowedWordsList = remember {
        //mutableStateOf<List<DisallowedWordsModel>>(emptyList())
        mutableStateListOf<DisallowedWordsModel>()
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("forbiddenWords").get()
            .addOnCompleteListener {
                val result = it.result.documents.mapNotNull { doc ->
                    doc.toObject(DisallowedWordsModel::class.java)
                }
                disallowedWordsList.addAll(result)
            }
    }

    var context = LocalContext.current

    val toastDisallowedWord : String = stringResource(R.string.pollcreationpage_toastdisallowedword)

    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        val texte = remember { mutableStateListOf("") }
        var errorMessage = remember { mutableStateOf(false) }
        val isNumberChoose = remember { mutableStateOf(false) }
        val multipleChoice = remember { mutableStateOf(false) }
        Column(modifier = Modifier.fillMaxWidth().padding(32.dp)
            .verticalScroll(scrollState)
            .padding(0.dp,0.dp, 0.dp, 60.dp)) {
            Text(
                text = stringResource(id = R.string.pollcreationpage_newpoll_text),
                modifier = Modifier.fillMaxWidth().padding(0.dp, 20.dp),
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

            )
            Text(
                text = stringResource(id = R.string.pollcreationpage_question_text),
                modifier = Modifier.padding(0.dp, 10.dp)
            )
            TextField(
                value = texte[0],
                onValueChange = { it: String -> texte[0] = it },
                label = { Text(stringResource(id = R.string.pollcreationpage_yourquestion_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            val chosenNumber = remember { mutableStateOf(2) }
            if (isNumberChoose.value) {
                for (i in 0 until chosenNumber.value) {
                    Text(
                        text = stringResource(id = R.string.pollcreationpage_choice_text) +" "+"${i + 1}"+" :",//"Choice ${i + 1} :",
                        modifier = Modifier.padding(0.dp, 10.dp)
                    )
                    TextField(
                        value = texte[i + 1],
                        onValueChange = { it: String -> texte[i + 1] = it },
                        label = {stringResource(id = R.string.pollcreationpage_choice_text) +" "+"${i + 1}"},//Text("Choice ${i + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                val showMessage = remember { mutableStateOf(false) }
                Text(
                    text = stringResource(id = R.string.pollcreationpage_numberofchoices_text),
                    modifier = Modifier.padding(0.dp, 10.dp)
                )
                Row {
                    val numberInput = remember { mutableStateOf("") }
                    TextField(
                        value = numberInput.value,
                        onValueChange = { newValue ->
                            numberInput.value = newValue
                            val number = newValue.toIntOrNull()
                            showMessage.value = number == null || number < 2
                        },
                        label = { Text(stringResource(id = R.string.pollcreationpage_numberofchoices)) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val number = numberInput.value.toIntOrNull()
                            if (number != null && number >= 2 && !showMessage.value) {
                                chosenNumber.value = number
                                while (texte.size < number + 1) {
                                    texte.add("")
                                }
                                isNumberChoose.value = true
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF87217),
                            contentColor = Color.White)
                    ) {
                        Text(stringResource(id = R.string.pollcreationpage_ok_button))
                    }
                }
                if (showMessage.value) {
                    val toasterror1 = stringResource(id = R.string.pollcreationpage_multiplechoiceerror_toast)
                    AppUtil.showToast(context, toasterror1)
                    /*Text(
                        text = "Minimum number of choices = 2, please put 2 or more",
                        color = Color.Red
                    )*/
                }
            }
            Row(modifier = Modifier.clickable { multipleChoice.value = !multipleChoice.value }) {
                Checkbox(
                    checked = multipleChoice.value,
                    onCheckedChange = { newValue -> multipleChoice.value = newValue }
                )
                Text(
                    text = stringResource(id = R.string.pollcreationpage_multiplechoicecheckbox_text),//"Multiple Answer Question",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        if (errorMessage.value){
            val toasterror2 = stringResource(id = R.string.pollcreationpage_error2_toast)
            AppUtil.showToast(context, toasterror2)
            /*Text(text = "The question or some choices are not filled ",
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 75.dp))*/
        }
        Button(
            onClick = { if (texte.all { it.isNotBlank() } && isNumberChoose.value){
                errorMessage.value = false
                val result = sendNewPoll(texte, multipleChoice.value, disallowedWordsList, context, toastDisallowedWord)
                if(result == true){
                    GlobalNavigation.navController.navigate(Routes.homescreen)
                }
                else{
                    null
                }

            } else {
                errorMessage.value = true
            }},
            modifier = Modifier
                .fillMaxWidth()
                //.height(66.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF87217),
                contentColor = Color.White)
        ) {
            Text(text = stringResource(id = R.string.commentpage_button1),fontSize = 22.sp,
                fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
        }
    }
}

fun sendNewPoll(texte : MutableList<String>, multipleAnswers : Boolean, disallowedWordsList : MutableList<DisallowedWordsModel>, context : Context, toastDisallowedWord : String): Boolean {
    //val db = FirebaseFirestore.getInstance()

    var containsDisallowedWord  = false

    val pollID = Firebase.firestore.collection("poll").document().id
    val newPoll = hashMapOf(
        "id" to pollID,
        "question" to texte[0],
        "choices" to texte.drop(1),
        "creationDate" to Timestamp.now(),
        "multipleChoice" to multipleAnswers,
        "answers" to mapOf<String, List<String>>()
    )

    disallowedWordsList.forEach{
        for (item in texte)
        if(item.contains(it.word, ignoreCase = true)){
            containsDisallowedWord = true
        }
    }

    if(containsDisallowedWord == true){
        AppUtil.showToast(context,toastDisallowedWord)
        return false
    }else{
        Firebase.firestore.collection("poll").document(pollID).set(newPoll)
        return true
    }
}