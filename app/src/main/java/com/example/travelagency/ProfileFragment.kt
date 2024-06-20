package com.example.travelagency

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.travelagency.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var name: String? = null
    private var email: String? = null
    private var phone: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.progressBar.visibility = View.VISIBLE

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid.toString()
        val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userDocRef.get().addOnSuccessListener { document ->

            binding.progressBar.visibility = View.GONE

            binding.profileTitle.visibility = View.VISIBLE
            binding.myInfoTitle.visibility = View.VISIBLE
            binding.editInfoBtn.visibility = View.VISIBLE
            binding.logOut.visibility = View.VISIBLE
            binding.deleteProfile.visibility = View.VISIBLE
            binding.infoEmail.visibility = View.VISIBLE
            binding.infoName.visibility = View.VISIBLE
            binding.infoPhone.visibility = View.VISIBLE

            if (document != null) {
                name = document.getString("name")
                email = document.getString("email")
                phone = document.getString("phone")

                binding.infoEmail.setText("E-mail: $email")
                binding.infoName.setText("Имя: $name")

                if (phone == ""){
                    binding.infoPhone.setText("Телефон: Не указан")
                } else{
                    binding.infoPhone.setText("Телефон: $phone")
                }


            } else {
                Log.d("TAG", "Документ не существует")
            }
        }

        binding.editInfoBtn.setOnClickListener{
            val intent = Intent(context, EditInfoActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("phone", phone)
            startActivity(intent)
        }

        binding.logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)

        }

        binding.deleteProfile.setOnClickListener {
            val intent = Intent(context, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.help.setOnClickListener {
            val intent = Intent(context, HelpActivity::class.java)
            startActivity(intent)
        }


        return view
    }

}