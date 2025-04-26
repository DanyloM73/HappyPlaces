package com.danylom73.happyplaces

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danylom73.happyplaces.adapters.HappyPlaceAdapter
import com.danylom73.happyplaces.database.AppDatabase
import com.danylom73.happyplaces.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var adapter: HappyPlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = AppDatabase.getInstance(this)
        adapter = HappyPlaceAdapter(mutableListOf())

        binding.listRv.layoutManager = LinearLayoutManager(this)
        binding.listRv.adapter = adapter

        loadHappyPlaces()

        binding.addFab.setOnClickListener {
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }

        val itemTouchHelper = ItemTouchHelper(itemDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.listRv)
    }

    private fun loadHappyPlaces() {
        lifecycleScope.launch {
            val happyPlaces = database.happyPlaceDao().getAllHappyPlaces()
            adapter = HappyPlaceAdapter(happyPlaces.toMutableList())
            binding.listRv.adapter = adapter
            setItemsVisibility()
        }
    }

    val itemDeleteCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val happyPlace = adapter.getItem(position)
                lifecycleScope.launch {
                    database.happyPlaceDao().deleteHappyPlaceById(happyPlace.id)
                    adapter.removeItem(happyPlace)
                    setItemsVisibility()
                }
            }
        }
    }

    private fun setItemsVisibility() {
        lifecycleScope.launch {
            val happyPlaces = database.happyPlaceDao().getAllHappyPlaces()
            if (happyPlaces.isEmpty()) {
                binding.listRv.visibility = View.GONE
                binding.noItemsTv.visibility = View.VISIBLE
            } else {
                binding.listRv.visibility = View.VISIBLE
                binding.noItemsTv.visibility = View.GONE
            }
        }
    }
}