package com.example.travelagency

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelagency.Adapter.ToursAdapter
import com.example.travelagency.Model.Tours
import com.example.travelagency.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment(), ToursAdapter.OnItemClickListener {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var toursAdapter: ToursAdapter
    private  lateinit var toursList: ArrayList<Tours>
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)

        toursList = ArrayList()

        db.collection("tours")
            .get()
            .addOnSuccessListener { toursResult ->
                for (tourDocument in toursResult) {
                    val tour = tourDocument.toObject(Tours::class.java)
                    tour.id = tourDocument.id
                    toursList.add(tour)
                    // Создаем и устанавливаем адаптер
                    toursAdapter = ToursAdapter(toursList, this)
                    recyclerView.adapter = toursAdapter
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }


        val editText = binding.searchTourEt
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                toursAdapter.filter.filter(s.toString()) // Применяем фильтр к вашему адаптеру при изменении текста в EditText
            }
        })


        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(context, TourDetailsActivity::class.java)
        val tourId = toursList[position].id!!
        intent.putExtra("id", tourId)
        startActivity(intent)
    }
}