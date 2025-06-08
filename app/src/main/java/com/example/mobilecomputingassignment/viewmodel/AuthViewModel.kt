package com.example.mobilecomputingassignment.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.example.mobilecomputingassignment.model.UserModel

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth

   private val firestore = Firebase.firestore

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val db = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onResult:(Boolean, String?)-> Unit){
        auth.signInWithEmailAndPassword(email, password)
              .addOnCompleteListener{
                  if(it.isSuccessful){
                       onResult(true, null)
                  }else{
                        onResult(false, "Something went wrong")
                  }
              }
    }


    fun signup(email: String, name: String, password: String, onResult:(Boolean, String?)-> Unit){
        auth.createUserWithEmailAndPassword(email, password)
           .addOnCompleteListener{
                  if(it.isSuccessful){
                      var userId = it.result?.user?.uid

                     val userModel = UserModel(name, email, userId!!, password)
                     firestore.collection("users").document(userId)
                              .set(userModel)
                              .addOnCompleteListener { dbTask->
                                if(dbTask.isSuccessful){
                                    onResult(true, null)
                                }else{
                                    onResult(false, "Something went wrong")
                                }
                              }
                  }else{
                      onResult(false, it.exception?.localizedMessage)
                  }
            }
    }

    fun updateName(newName: String, onResult:(Boolean, String?)-> Unit) {
        uid?.let {
            db.collection("users").document(it)
                .update("name", newName)
                .addOnSuccessListener {
                    onResult(true, null)
                }
                .addOnFailureListener {
                    onResult(false, "Something went wrong")
                }
        }
    }

        fun updateEmail(newEmail: String, onResult:(Boolean, String?)-> Unit) {
            uid?.let {
                db.collection("users").document(it)
                    .update("email", newEmail)
                    .addOnSuccessListener {
                        onResult(true, null)
                    }
                    .addOnFailureListener {
                        onResult(false, "Something went wrong")
                    }
            }
    }

    fun updatePassword(newPassword: String, onResult:(Boolean, String?)-> Unit) {
        uid?.let {
            db.collection("users").document(it)
                .update("password", newPassword)
                .addOnSuccessListener {
                    onResult(true, null)
                }
                .addOnFailureListener {
                    onResult(false, "Something went wrong")
                }
        }
    }
}