package com.example.mobilecomputingassignment.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import com.example.mobilecomputingassignment.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobilecomputingassignment.model.CommentModel
import com.example.mobilecomputingassignment.model.ReportModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommentsView(modifier: Modifier = Modifier, eventID : String) {
    var commentsList = remember {
        mutableStateOf<List<CommentModel>>(emptyList())
    }

    LaunchedEffect(eventID) {
        Firebase.firestore.collection("events")
            .document(eventID)
            .collection("comments")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("CommentsView", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val result = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CommentModel::class.java)
                    }
                    commentsList.value = result
                    Log.e("Error", "A comment were added")
                } else {
                    commentsList.value = emptyList()
                    Log.e("Error", "No Comments were added")
                }
            }
    }

    LazyColumn {
        items(commentsList.value){item->
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
            if((item.comment == "" && item.userName == "" && item.userID == "")){
                null
            }
            else if(tooManyReports == true){
                null
            }
            else{
                CommentItem(comment = item)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

}

@Composable
fun CommentItem(comment : CommentModel){
    val date = comment.timestamp.toDate()
    val formatter = SimpleDateFormat("yyyy-MM-dd, HH:mm", Locale.getDefault())
    val formattedDate = formatter.format(date)

    var enabled by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color(0xFFE3A370), shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Commenters Name
        Text(
            text = comment.userName,
            modifier = Modifier
                .background(Color(0xFFF87217), shape = RoundedCornerShape(5.dp))
                .fillMaxWidth().padding(10.dp, 0.dp),
            textAlign = TextAlign.Start,
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
        )

        Text(
            text = formattedDate,
            modifier = Modifier
                .background(Color(0xFFF87217), shape = RoundedCornerShape(5.dp))
                .fillMaxWidth().padding(10.dp, 0.dp),
            textAlign = TextAlign.Start,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                //color = Color.White
            )
        )

        //Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = comment.comment, modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Justify,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal

            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.commentsview_report_text), modifier = Modifier.padding(10.dp, 0.dp)
                .clickable(enabled = enabled){
                    val db = FirebaseFirestore.getInstance()
                    val reportsRef = db.collection("reports")

                    reportsRef
                        .whereEqualTo("idOfReportedItem", comment.id)
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
                                    idOfReportedItem = comment.id,
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
                color = Color.White,
                textDecoration = TextDecoration.Underline

            )
        )
    }
}