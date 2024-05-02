package com.example.travelagency.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Filterable
import com.example.travelagency.Model.Tours
import com.example.travelagency.R
import com.google.android.material.internal.ViewUtils.dpToPx
import com.squareup.picasso.Picasso

class ToursAdapter(private val toursList: ArrayList<Tours>, private val listener: OnItemClickListener): RecyclerView.Adapter<ToursAdapter.ToursViewHolder>(), Filterable {

    private var toursListFull: ArrayList<Tours> = ArrayList(toursList)

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ToursViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val tvName: TextView = itemView.findViewById(R.id.name)
        val tvCities: TextView = itemView.findViewById(R.id.city)
        val tvKeyWords: TextView = itemView.findViewById(R.id.keyWords)

        val tvDateTimeTo: TextView = itemView.findViewById(R.id.dateTimeTo)
        val tvDateTimeFrom: TextView = itemView.findViewById(R.id.dateTimeFrom)
        val tvCost: TextView = itemView.findViewById(R.id.cost)
        val imageView: ImageView = itemView.findViewById(R.id.image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToursViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_view,parent,false)
        return ToursViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return toursList.size
    }

    override fun onBindViewHolder(holder: ToursViewHolder, position: Int) {
        val tours = toursList[position]
        val imageURL = tours.img

        val name = tours.name
        val cities = tours.cities
        val keyWords = tours.key_words
        val cost = "${tours.cost} рублей"
        val dateTimeTo = formatDate(tours.dateTimeTo)
        val dateTimeFrom = formatDate(tours.dateTimeFrom)


        holder.tvName.text = name
        holder.tvCities.text = cities
        holder.tvKeyWords.text = keyWords
        holder.tvCost.text = cost
        holder.tvDateTimeTo.text = dateTimeTo
        holder.tvDateTimeFrom.text = " - " + dateTimeFrom

        Picasso.get().load(imageURL).fit().centerCrop().into(holder.imageView)

    }


    private fun formatDate(date: Date?): String {
        return date?.let {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) // замените формат на нужный вам
            format.format(it)
        } ?: ""
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList: ArrayList<Tours> = ArrayList()
                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(toursListFull)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    for (item in toursListFull) {
                        if (item.name?.toLowerCase()?.contains(filterPattern) == true) {
                            filteredList.add(item)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                toursList.clear()
                toursList.addAll(results?.values as ArrayList<Tours>)
                notifyDataSetChanged()
            }
        }
    }

}