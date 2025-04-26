package com.danylom73.happyplaces.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danylom73.happyplaces.R
import com.danylom73.happyplaces.models.HappyPlaceModel
import de.hdodenhof.circleimageview.CircleImageView
import androidx.core.net.toUri
import com.danylom73.happyplaces.HappyPlaceDetailActivity

class HappyPlaceAdapter(
    private val happyPlaces: MutableList<HappyPlaceModel>
) : RecyclerView.Adapter<HappyPlaceAdapter.HappyPlaceViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HappyPlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_happy_place, parent, false)
        return HappyPlaceViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HappyPlaceViewHolder,
        position: Int
    ) {
        holder.bind(happyPlaces[position])
    }

    override fun getItemCount(): Int = happyPlaces.size

    fun getItem(position: Int): HappyPlaceModel {
        return happyPlaces[position]
    }

    fun removeItem(place: HappyPlaceModel) {
        val position = happyPlaces.indexOf(place)
        if (position != -1) {
            happyPlaces.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class HappyPlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val placeImageIv = view.findViewById<CircleImageView>(R.id.placeImageIv)
        private val titleTv = view.findViewById<TextView>(R.id.titleTv)
        private val descriptionTv = view.findViewById<TextView>(R.id.descriptionTv)

        fun bind(place: HappyPlaceModel) {
            placeImageIv.setImageURI(place.image.toUri())
            titleTv.text = place.title
            descriptionTv.text = place.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context,
                    HappyPlaceDetailActivity::class.java).apply {
                    putExtra("place_id", place.id)
                }
                itemView.context.startActivity(intent)
            }
        }
    }
}