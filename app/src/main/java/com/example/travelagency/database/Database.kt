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

    fun CallbackRequest(userId: String, userPhone: String, description: String, completion: (String?) -> Unit) {
        checkIfRequestExists(userId) { exists ->
            if (!exists) {
                // Если заявка не существует, добавляем новую
                val db = Firebase.firestore
                val callBack = hashMapOf(
                    "id_user" to userId,
                    "user_phone" to userPhone,
                    "description" to description
                )

                db.collection("callBack")
                    .add(callBack)
                    .addOnSuccessListener { documentReference ->
                        val documentId = documentReference.id
                        completion(documentId)
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding tour", e)
                        completion(null)
                    }
            } else {
                // Если заявка уже существует, выполняем другие действия или возвращаем ошибку
                completion(null)
            }
        }
    }

    fun checkIfRequestExists(userId: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val query = db.collection("callBack").whereEqualTo("id_user", userId)

        query.get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty)
            }
            .addOnFailureListener {
                // Обработка ошибки
                callback(false)
            }
    }

    fun deleteCallbackRequest(userId: String, completion: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val callBackCollection = db.collection("callBack")

        callBackCollection
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            completion(true) // Успешно удалено
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error deleting callback request", e)
                            completion(false) // Ошибка при удалении
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting documents", e)
                completion(false) // Ошибка при получении документов
            }
    }
}