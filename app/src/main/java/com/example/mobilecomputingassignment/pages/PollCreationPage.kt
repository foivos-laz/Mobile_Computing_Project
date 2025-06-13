package com.example.mobilecomputingassignment.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.mobilecomputingassignment.GlobalNavigation
import com.example.mobilecomputingassignment.Routes
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun PollCreationPage(modifier: Modifier = Modifier) {
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
                text = "New Poll",
                modifier = Modifier.fillMaxWidth().padding(0.dp, 20.dp),
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

            )
            Text(
                text = "Question :",
                modifier = Modifier.padding(0.dp, 10.dp)
            )
            TextField(
                value = texte[0],
                onValueChange = { it: String -> texte[0] = it },
                label = { Text("Your Question") },
                modifier = Modifier.fillMaxWidth()
            )
            val chosenNumber = remember { mutableStateOf(2) }
            if (isNumberChoose.value) {
                for (i in 0 until chosenNumber.value) {
                    Text(
                        text = "Choice ${i + 1} :",
                        modifier = Modifier.padding(0.dp, 10.dp)
                    )
                    TextField(
                        value = texte[i + 1],
                        onValueChange = { it: String -> texte[i + 1] = it },
                        label = { Text("Choice ${i + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                val showMessage = remember { mutableStateOf(false) }
                Text(
                    text = "Number of choices :",
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
                        label = { Text("number of choices") },
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
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text("ok")
                    }
                }
                if (showMessage.value) {
                    Text(
                        text = "Minimum number of choices = 2, please put 2 or more",
                        color = Color.Red
                    )
                }
            }
            Row(modifier = Modifier.clickable { multipleChoice.value = !multipleChoice.value }) {
                Checkbox(
                    checked = multipleChoice.value,
                    onCheckedChange = { newValue -> multipleChoice.value = newValue }
                )
                Text(
                    text = "Multiple Answer Question",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        if (errorMessage.value){
            Text(text = "The question or some choices are not filled ",
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 75.dp))
        }
        Button(
            onClick = { if (texte.all { it.isNotBlank() } && isNumberChoose.value){
                errorMessage.value = false
                sendNewPoll(texte, multipleChoice.value)
                GlobalNavigation.navController.navigate(Routes.homescreen)
            } else {
                errorMessage.value = true
            }},
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 30.dp)
        ) {
            Text("Submit", color = Color.White)
        }
    }
}

fun sendNewPoll(texte : MutableList<String> , multipleAnswers : Boolean){
    //val db = FirebaseFirestore.getInstance()
    val pollID = Firebase.firestore.collection("poll").document().id
    val newPoll = hashMapOf(
        "id" to pollID,
        "question" to texte[0],
        "choices" to texte.drop(1),
        "creationDate" to com.google.firebase.Timestamp.now(),
        "multipleChoice" to multipleAnswers,
        "answers" to mapOf<String, List<String>>()
    )
    Firebase.firestore.collection("poll").document(pollID).set(newPoll)
}