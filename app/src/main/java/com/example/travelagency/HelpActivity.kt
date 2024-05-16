package com.example.travelagency

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.travelagency.database.Database
import com.example.travelagency.databinding.ActivityHelpBinding
import com.example.travelagency.databinding.ActivityTourDetailsBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid.toString()


        val database = Database()
        database.checkIfRequestExists(userId) { exists ->
            if (exists) {

                val documentsRef = FirebaseFirestore.getInstance().collection("callBack")
                val query = documentsRef.whereEqualTo("id_user", userId)

                query.get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val phone = "Телефон: "+ document.getString("user_phone")
                        val description = "Описание проблемы: "+document.getString("description")
                        binding.callbackPhone.setText(phone)
                        binding.callbackDescription.setText(description)
                    }
                }

                binding.helpMessage.visibility = View.GONE
                binding.phoneLayout.visibility = View.GONE
                binding.descriptionLayout.visibility = View.GONE
                binding.button.visibility = View.GONE

                binding.progressBar.visibility = View.GONE

                binding.callbackTitle.visibility = View.VISIBLE
                binding.callbackPhone.visibility = View.VISIBLE
                binding.callbackDescription.visibility = View.VISIBLE
                binding.button1.visibility = View.VISIBLE
            }
            else{
                binding.progressBar.visibility = View.GONE

                binding.helpMessage.visibility = View.VISIBLE
                binding.phoneLayout.visibility = View.VISIBLE
                binding.descriptionLayout.visibility = View.VISIBLE
                binding.button.visibility = View.VISIBLE
            }
        }

        val db = Firebase.firestore

        val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val phone = document.getString("phone")
                binding.phoneEt.setText(phone)
            }
        }


        binding.imageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToOpen", "ProfileFragment")
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val database = Database()
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)

            val phonePattern = "\\+375\\d{9}"
            if(binding.phoneEt.text.toString()!="" && binding.descriptionEt.toString()!="" ){
                if(binding.phoneEt.text.toString()!!.matches(phonePattern.toRegex())){
                    database.CallbackRequest(userId, binding.phoneEt.text.toString(), binding.descriptionEt.text.toString()) { documentId ->
                        if (documentId != null) {
                            Toast.makeText(this, "Заявка оставлена", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("fragmentToOpen", "ProfileFragment")
                            startActivity(intent)

                        }
                        else{
                            Toast.makeText(this, "Дождитесь обработки активной заявки", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Введите номер телефона в формате +375", Toast.LENGTH_SHORT).show()
                }

            }
            else {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
            }

        }

        binding.button1.setOnClickListener {
            val database = Database()
            database.deleteCallbackRequest(userId) { success ->
                if (success) {
                    Toast.makeText(this, "Заявка на обратный звонок успешно удалена", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fragmentToOpen", "ProfileFragment")
                    startActivity(intent)
                } else {
                    println("При удалении заявки на обратный звонок произошла ошибка")
                }
            }
        }

    }
}