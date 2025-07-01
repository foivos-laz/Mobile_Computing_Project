package com.example.mobilecomputingassignment.pages

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mobilecomputingassignment.AppUtil
import com.example.mobilecomputingassignment.GlobalNavigation
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.Routes
import com.example.mobilecomputingassignment.model.PollModel
import com.example.mobilecomputingassignment.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.math.round

@Composable
fun PollDetailsPage(modifier: Modifier = Modifier, pollID :String) {
    var poll by remember{
        mutableStateOf(PollModel())
    }

    //Log.e("Error", "The Poll id is:"+pollID)

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userID = currentUser?.uid!!

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("poll").document(pollID).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    var result = it.result.toObject(PollModel::class.java)
                    if(result!=null){
                        result.id = it.result.id
                        poll = result
                    }
                }
            }
    }

    Card(
        modifier = Modifier.fillMaxSize()
            .padding(32.dp),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color(0xFFF87217)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ){

            ShowPoll(poll,userID)
    }
}

@Composable
fun ShowPoll(poll: PollModel, userID: String) {
    val choiceListe = poll.choices.associate { it to 0 }.toMutableMap()
    val userChoice: MutableList<String> = remember { mutableListOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var choiceVar by remember { mutableStateOf("") }

    var context = LocalContext.current

    val toastDelete = stringResource(id = R.string.polldetailspage_delete_toast)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(3.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = poll.question,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(15.dp))

        poll.answers?.forEach { (choice, userList) ->
            choiceListe[choice] = userList.size
            if (userID in userList) {
                userChoice.add(choice)
            }
        }

        userChoice.drop(1)
        val total: Int = choiceListe.values.sum()

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
                            .fillMaxWidth(percentage.coerceAtLeast(0.01f))
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

                Spacer(modifier = Modifier.height(5.dp))

                Button(onClick = {
                    choiceVar = choice
                    showDialog = true

                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF87217),
                    contentColor = Color.White)) {
                    Text(text = stringResource(id = R.string.polldetailspage_showresponders_button))
                }
            }
        }

        if (showDialog) {
            RespondersDialog(onDismiss = { showDialog = false }, poll, choiceVar)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(text = stringResource(id = R.string.polldetailspage_textdelete), modifier = Modifier,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            ))

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                Firebase.firestore.collection("poll").document(poll.id)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("PollDelete", "Poll successfully deleted.")
                        AppUtil.showToast(context, toastDelete)
                        GlobalNavigation.navController.navigate(Routes.homescreen){
                            popUpTo(Routes.polldetailspage) {inclusive = true}
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("PollDelete", "Error deleting poll", e)
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.polldetailspage_delete_button),
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
            )
        }

    }
}

@Composable
fun RespondersDialog(onDismiss: () -> Unit, poll: PollModel, choice: String) {
    val scrollState = rememberScrollState()

    val usersList = remember {
        mutableStateOf<List<UserModel>>(emptyList())
    }

    val respondersList = remember {
        mutableStateOf<List<UserModel>>(emptyList())
    }

    // Fetch all users
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val allUsers = result.documents.mapNotNull { it.toObject(UserModel::class.java) }
                usersList.value = allUsers

                // Get the list of user IDs who chose this choice
                val selectedUserIDs = poll.answers?.get(choice) ?: emptyList()

                // Filter the usersList to get responders
                val filtered = allUsers.filter { it.uid in selectedUserIDs }
                respondersList.value = filtered
            }
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.polldetailspage_dialog_title) + " \"$choice\":",
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(200.dp) // adjust size here
                        .verticalScroll(scrollState)
                ) {
                    if (respondersList.value.isEmpty()) {
                        Text(text = stringResource(id = R.string.polldetailspage_dialog_noanswers))
                    } else {
                        respondersList.value.forEach { user ->
                            Text("• ${user.name}", fontSize = 16.sp) // Or user.id
                        }
                    }
                }
            }
        }
    }
}
