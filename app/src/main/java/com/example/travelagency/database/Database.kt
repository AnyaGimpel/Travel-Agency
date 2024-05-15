package com.example.travelagency.database

import android.content.ContentValues
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class Database {
    fun addUserToDB(userId: String, email: String, name: String, password: String) {
        val db = Firebase.firestore
        val user = hashMapOf(
            "email" to email,
            "name" to name,
            "phone" to "",
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
    fun updateUserData(userId: String, name: String, phone: String) {

        val db = Firebase.firestore
        db.collection("users").document(userId)
            .update(
                mapOf(
                    "name" to name,
                    "phone" to phone,
                ),
            )

    }

    fun isTourBooked(userId: String, tourId: String, completion: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val docRef = db.collection("bookings")
            .whereEqualTo("id_user", userId)
            .whereEqualTo("id_tour", tourId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    completion(false) // Тур не забронирован
                } else {
                    completion(true) // Тур забронирован
                }
            }
            .addOnFailureListener { exception ->
                completion(false) // В случае ошибки также возвращаем false
            }
    }
    fun bookTour(userId: String, tourId: String, completion: (String?) -> Unit) {
        val db = Firebase.firestore
        val booking = hashMapOf(
            "id_user" to userId,
            "id_tour" to tourId,
        )

        db.collection("bookings")
            .add(booking)
            .addOnSuccessListener { documentReference ->
                val documentId = documentReference.id
                completion(documentId)
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding tour", e)
                completion(null)
            }
    }

    fun cancelBooking(userId: String, tourId: String?){

        val db = Firebase.firestore
        val collectionRef = db.collection("bookings")
        collectionRef.whereEqualTo("id_user", userId)
            .whereEqualTo("id_tour", tourId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("bookings").document(document.id)
                        .delete()
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Ошибка при удалении документа", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Ошибка при получении документов", exception)
            }
    }

}