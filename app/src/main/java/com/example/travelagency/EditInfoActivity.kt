package com.example.travelagency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.travelagency.database.Database
import com.example.travelagency.databinding.ActivityEditInfoBinding
import com.example.travelagency.databinding.ActivitySignInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class EditInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditInfoBinding
    private var pass: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")

        binding.nameEt.setText(name)
        binding.phoneEt.setText(phone)

        binding.imageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToOpen", "ProfileFragment")
            startActivity(intent)
            finish()
        }

        binding.button.setOnClickListener {
            val newName = binding.nameEt.text.toString()
            val newPhone = binding.phoneEt.text.toString()

            val database = Database()

            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid.toString()

            val db = Firebase.firestore

            val userDocRef = db.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        pass = document.getString("password")
                    }
                }

            val phonePattern = "\\+375\\d{9}"
            if(newName.isNotEmpty()){
                if(phone!=""){
                    if(phone!!.matches(phonePattern.toRegex())){
                        database.updateUserData(userId, newName, newPhone)
                        Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this, "Введите номер телефона в формате +375", Toast.LENGTH_SHORT).show()
                    }

                }
                else {
                    database.updateUserData(userId, newName, newPhone)
                    Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fragmentToOpen", "ProfileFragment")
                    startActivity(intent)
                    finish()
                }
            }
            else {
                Toast.makeText(this, "Имя пользователя должно быть заполнено", Toast.LENGTH_SHORT).show()
            }

        }

    }
}