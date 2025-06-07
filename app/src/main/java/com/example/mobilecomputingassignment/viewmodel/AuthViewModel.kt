package com.example.mobilecomputingassignment.viewmodel

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    //private val auth = Firebase.auth

   // private val firestore = Firebase.firestore

    fun login(email: String, password: String, onResult:(Boolean, String?)-> Unit){
        /*auth.signInWithEmailAndPassword(email, password)
              .addOnCompleteListener{
                  if(it.isSucceful){
                       onResult(true, null)
                  }else{
                        onResult(false, stringResource(id = R.string.signupscreen_error)
                  }
              }
        */

    }

    fun signup(email: String, name: String, password: String, onResult:(Boolean, String?)-> Unit){
        /*auth.createUserWithEmailAndPassword(email, password)
           .addOnCompleteListener{
                  if(it.isSucceful){
                      var userId = it.result?.user?.uid

                     val userModel = UserModel(name, email, userId!!)
                     firestore.collection("users").document(userId)
                              .set(userModel)
                              .addOnCompleteListener { dbTask->
                                if(dbTask.isSuccessful){
                                    onResult(true, null)
                                }else{
                                    onResult(false, stringResource(id = R.string.signupscreen_error)
                                }
                              }

                  }else{
                      onResult(false, it.exception?.localizedMessage)
                  }
            }*/
    }
}