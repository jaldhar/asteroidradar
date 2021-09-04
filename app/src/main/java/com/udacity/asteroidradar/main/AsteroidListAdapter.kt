package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.databinding.CardAsteroidBinding
import com.udacity.asteroidradar.domain.Asteroid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AsteroidListAdapter(val clickListener: AsteroidListClickListener) :
    ListAdapter<Asteroid, AsteroidCardHolder>(AsteroidDiffCallback()) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(holder: AsteroidCardHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AsteroidCardHolder.from(parent)

    fun changeList(asteroids: List<Asteroid>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(asteroids)
            }
        }
    }

}

class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }
}

class AsteroidCardHolder private constructor(private val binding: CardAsteroidBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(asteroid: Asteroid, clickListener: AsteroidListClickListener) {
        binding.asteroid = asteroid
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup) : AsteroidCardHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = CardAsteroidBinding.inflate(inflater, parent, false)

            return AsteroidCardHolder(binding)
        }
    }
}

class AsteroidListClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}
