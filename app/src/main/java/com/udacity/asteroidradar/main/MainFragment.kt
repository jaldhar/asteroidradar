package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(getString(R.string.api_key),
            AsteroidDatabase.getInstance(this.requireContext()).dao)
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.feed.observe(viewLifecycleOwner, { asteroids ->
            asteroids?.forEach {
                Log.i("Asteroid Radar","${it.codename} ${it.closeApproachDate}\n")
            }
        })

        viewModel.status.observe(viewLifecycleOwner, {
            if (it == NeoWSAPIStatus.LOADING) {
                binding.statusLoadingWheel.visibility = View.VISIBLE

            } else {
                if (it == NeoWSAPIStatus.ERROR) {
                    Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                }
                binding.statusLoadingWheel.visibility = View.GONE
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
