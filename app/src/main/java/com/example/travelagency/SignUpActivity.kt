package com.example.travelagency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.travelagency.database.Database
import com.example.travelagency.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }



        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.button.setOnClickListener {
            binding.button.isClickable = false

            val email = binding.emailEt.text.toString()
            val name = binding.nameEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && name.isNotEmpty()) {
                if (pass == confirmPass) {
                    if (pass.length < 6) {
                        Toast.makeText(this, "Пароль должен состоять минимум из 6 символов", Toast.LENGTH_SHORT).show()
                        binding.button.isClickable = true
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Некорректный адрес электронной почты", Toast.LENGTH_SHORT).show()
                        binding.button.isClickable = true
                    } else {
                        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { createUserTask ->
                            if (createUserTask.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                val userId = user?.uid.toString()
                                val database = Database()
                                database.addUserToDB(userId, email, name, pass)

                                Toast.makeText(this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener { e ->
                            if (e is FirebaseAuthUserCollisionException) {
                                Toast.makeText(this, "Пользователь с таким электронным адресом уже зарегистрирован", Toast.LENGTH_SHORT).show()
                                binding.button.isClickable = true
                            } else {
                                Toast.makeText(this, "Ошибка при регистрации: " + e.message, Toast.LENGTH_SHORT).show()
                                binding.button.isClickable = true
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                    binding.button.isClickable = true
                }
            } else {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
                binding.button.isClickable = true
            }
        }
    }
}