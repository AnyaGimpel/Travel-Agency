package com.example.travelagency

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelagency.Adapter.ToursAdapter
import com.example.travelagency.Model.Tours
import com.example.travelagency.databinding.FragmentCartBinding
import com.example.travelagency.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CartFragment : Fragment(), ToursAdapter.OnItemClickListener  {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCartBinding
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
        binding = FragmentCartBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)

        toursList = ArrayList()

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid.toString()

        db.collection("bookings")
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener { bookingsResult ->
                for (bookingDocument in bookingsResult) {
                    val tourId = bookingDocument.getString("id_tour").toString()
                    db.collection("tours").document(tourId)
                        .get()
                        .addOnSuccessListener { tourDocument ->
                            val tour = tourDocument.toObject(Tours::class.java)
                            if (tour != null) {
                                tour.id = tourDocument.id
                                toursList.add(tour)
                            }
                            // Создаем и устанавливаем адаптер
                            toursAdapter = ToursAdapter(toursList, this)
                            recyclerView.adapter = toursAdapter
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }


        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {
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
        intent.putExtra("marker", "cart")
        startActivity(intent)
    }

}

