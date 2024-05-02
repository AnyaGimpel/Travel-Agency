package com.example.travelagency

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.travelagency.Model.Tours
import com.example.travelagency.databinding.ActivitySignInBinding
import com.example.travelagency.databinding.ActivityTourDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TourDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTourDetailsBinding
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tourId = intent.getStringExtra("id")

        if (tourId != null) {
            db.collection("tours")
                .document(tourId)
                .get()
                .addOnSuccessListener { tourDocument ->
                    if (tourDocument.exists()) {
                        val tour = tourDocument.toObject(Tours::class.java)
                        tour?.id = tourDocument.id

                        binding.name.text = tour?.name
                        binding.keyWords.text = tour?.key_words
                        binding.cities.text = tour?.cities
                        binding.type.text = tour?.type
                        binding.date.text = formatDate(tour?.dateTimeTo)  + " - " + formatDate(tour?.dateTimeFrom)
                        binding.description.text = tour?.description

                        val imageUrl = tour?.img
                        if (!imageUrl.isNullOrEmpty()) {
                            Picasso.get().load(imageUrl).fit().centerCrop().into(binding.image)
                        }

                        val hotelId = tour?.id_hotel
                        Log.d("TourDetailsActivity", "Document $hotelId")
                        if (hotelId != null) {
                            db.collection("hotels")
                                .document(hotelId)
                                .get()
                                .addOnSuccessListener { Document ->
                                    if (Document.exists()) {

                                        val name = Document.getString("name")
                                        if (!name.isNullOrEmpty()) {
                                            binding.hotelName.text = "Название отеля: " + name
                                        }

                                        val rate = Document.getDouble("rate")
                                        if (rate != null) {
                                            binding.hotelRate.text = "Рейтинг: " + rate
                                        }

                                        val city = Document.getString("city")
                                        if (!city.isNullOrEmpty()) {
                                            binding.hotelAddress.text = "Адрес: " + city
                                        }

                                    } else {

                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                                }
                        }

                    } else {

                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }


        binding.imageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToOpen", "HomeFragment")
            startActivity(intent)
        }

    }
}

private fun formatDate(date: Date?): String {
    return date?.let {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) // замените формат на нужный вам
        format.format(it)
    } ?: ""
}