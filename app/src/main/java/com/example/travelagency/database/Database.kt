package com.example.travelagency.database

import android.content.ContentValues
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Database {
    fun addUserToDB(userId: String, email: String, name: String, password: String) {
        val db = Firebase.firestore
        val user = hashMapOf(
            "email" to email,
            "name" to name,
            "phone" to null,
            "password" to password,
            "role" to 0
        )

        db.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "User was added")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error with adding the user", e)
            }


    }
}