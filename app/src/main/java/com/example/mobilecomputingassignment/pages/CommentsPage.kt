package com.example.mobilecomputingassignment.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mobilecomputingassignment.AppUtil
import com.example.mobilecomputingassignment.R
import com.example.mobilecomputingassignment.components.CommentsView
import com.example.mobilecomputingassignment.model.CommentModel
import com.example.mobilecomputingassignment.model.DisallowedWordsModel
import com.example.mobilecomputingassignment.model.EventModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@Composable
fun CommentsPage(modifier: Modifier = Modifier, eventID : String) {
    var event by remember {
        mutableStateOf(EventModel())
    }

    var showDialog by remember { mutableStateOf(false) }

    var inputText by remember { mutableStateOf("") }

    var name by remember {
        mutableStateOf("")
    }

    var disallowedWordsList = remember {
        mutableStateOf<List<DisallowedWordsModel>>(emptyList())
    }

    val context = LocalContext.current

    val toastDisallowedWord : String = stringResource(R.string.commentspage_disallowedword_toast)

    val toastSuccess = stringResource(id = R.string.commentpage_toast1)
    val toastFail = stringResource(id = R.string.commentpage_toast2)

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnCompleteListener {
                name = it.result.get("name").toString()
            }
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("events").document(eventID).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var result = it.result.toObject(EventModel::class.java)
                    if (result != null) {
                        result.id = it.result.id
                        event = result
                    }
                }
            }
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("forbiddenWords").get()
            .addOnCompleteListener {
                val result = it.result.documents.mapNotNull { doc ->
                    doc.toObject(DisallowedWordsModel::class.java)
                }
                disallowedWordsList.value = result
            }
    }

    Box{
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
        ) {
            //Event Name Area and "Comments" text, doesn't scroll with the rest
            Column(
                modifier = Modifier.fillMaxSize().padding(3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(3.dp)
                        .background(Color(0xFFE3A370), shape = RoundedCornerShape(5.dp)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = event.name, modifier = Modifier,
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(id = R.string.eventdetailspage_commentsbutton),
                        modifier = Modifier,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                //The comments
                CommentsView(modifier, eventID)
            }
        }

        FloatingActionButton(
            onClick = {showDialog = true },
            modifier = Modifier.padding(25.dp)
                .align(Alignment.BottomEnd),
            containerColor = Color(0xFFE5BA97)
        ) {
            Icon(Icons.Filled.Add, "Floating action button.")
        }

        // Popup Dialog
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,//MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.commentspage_text1))
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.fillMaxWidth(),
                            //singleLine = true,
                            label = {
                                Text(stringResource(id = R.string.commentpage_text2))
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val currentUser = Firebase.auth.currentUser
                                val currentUserId = currentUser?.uid!!

                                val commentID = Firebase.firestore.collection("events")
                                    .document(eventID).collection("comments").document().id

                                var commentModel = CommentModel(inputText, commentID, Timestamp.now(), currentUserId, name)

                                val db = FirebaseFirestore.getInstance()

                                var containsDisallowedWord : Boolean = false

                                //Checks if all words in disallowed word list are contained in
                                //the comment
                                for(item in disallowedWordsList.value){
                                    if(inputText.contains(item.word, ignoreCase = true)){
                                        containsDisallowedWord = true
                                    }
                                }

                                if(containsDisallowedWord == false){
                                    db.collection("events").document(eventID)
                                        .collection("comments").document(commentID)
                                        .set(commentModel)
                                        .addOnSuccessListener {
                                            AppUtil.showToast(context, toastSuccess)
                                        }
                                        .addOnFailureListener {
                                            AppUtil.showToast(context, toastFail)
                                        }

                                    showDialog = false
                                }
                                else{
                                    AppUtil.showToast(context,toastDisallowedWord)
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF87217),
                                contentColor = Color.White
                            )
                        ) {
                            Text(stringResource(id = R.string.commentpage_button1))
                        }
                    }
                }
            }
        }
    }
}