package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.utils.NetworkStatus

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    private val viewModel: MainViewModel by lazy {
        val viewModelFactory = MainViewModelFactory(getString(R.string.api_key),
            AsteroidDatabase.getInstance(this.requireContext()).dao)
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        val adapter = AsteroidListAdapter(AsteroidListClickListener {
            id -> viewModel.onAsteroidClicked(id)
        })
        binding.asteroidRecycler.adapter = adapter

        viewModel.asteroids.observe(viewLifecycleOwner, { asteroids ->
            asteroids?.let {
                adapter.changeList(asteroids)
            }
        })

        viewModel.neoNWSStatus.observe(viewLifecycleOwner, {
            if (it == NetworkStatus.LOADING) {
                binding.statusLoadingWheel.visibility = View.VISIBLE

            } else {
                if (it == NetworkStatus.ERROR) {
                    Snackbar.make(requireView(), R.string.neonws_network_error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) {
                            viewModel.retryRefreshAsteroidList()
                        }
                        .show()
                }
                binding.statusLoadingWheel.visibility = View.GONE
            }
        })

        viewModel.potdStatus.observe(viewLifecycleOwner, {
            if (it == NetworkStatus.LOADING) {
                binding.statusLoadingWheel.visibility = View.VISIBLE

            } else {
                if (it == NetworkStatus.ERROR) {
                    Snackbar.make(requireView(), R.string.potd_network_error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry) {
                            viewModel.retryRefreshPictureOfTheDay()
                        }
                        .show()
                }
                binding.statusLoadingWheel.visibility = View.GONE
            }
        })

        viewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, { asteroid ->
            asteroid?.let {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.onAsteroidDetailNavigated()

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
        when (item.itemId) {
            R.id.show_week -> { viewModel.setSortType(SortType.WEEK) }
            R.id.show_saved -> { viewModel.setSortType(SortType.SAVED) }
            R.id.show_today -> { viewModel.setSortType(SortType.TODAY) }
            else -> {
                return false
            }
        }
        return true
    }
}
