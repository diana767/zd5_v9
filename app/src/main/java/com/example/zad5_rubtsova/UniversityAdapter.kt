package com.example.zad5_rubtsova

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class UniversityAdapter(private var universities: List<UniversityResponse>) :
    RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder>() {

    class UniversityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val universityName: TextView = itemView.findViewById(R.id.tvUniversityName)
        val universityPhoto: ImageView = itemView.findViewById(R.id.ivUniversityPhoto)
        val specialtiesCount: TextView = itemView.findViewById(R.id.tvSpecialtiesCount) // TextView for specialties count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_university, parent, false)
        return UniversityViewHolder(view)
    }

    override fun onBindViewHolder(holder: UniversityViewHolder, position: Int) {
        val university = universities[position]
        holder.universityName.text = university.name

        // Load the university photo using an image loading library like Glide or Picasso
        Glide.with(holder.itemView.context)
            .load(university.web_pages.firstOrNull()) // Assuming the first web page is used as a photo URL
            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
            .error(R.drawable.error_image) // Error image if loading fails
            .into(holder.universityPhoto)

        // Display the count of specialties
        holder.specialtiesCount.text = "Specialties: ${university.specialties.size}"
    }

    override fun getItemCount(): Int {
        return universities.size
    }

    fun updateData(newUniversities: List<UniversityResponse>) {
        universities = newUniversities
        notifyDataSetChanged()
    }
}