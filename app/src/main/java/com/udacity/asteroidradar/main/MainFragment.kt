package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import java.io.ObjectStreamException

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)

        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        /** RecyclerView Setup **/
        val asteroidAdapter = AsteroidAdapter(AsteroidClickListener { asteroid ->
            viewModel.navigateToAsteroidDetails(asteroid)
        })
        binding.asteroidRecycler.adapter = asteroidAdapter

        viewModel.asteroids.observe(viewLifecycleOwner, Observer { asteroids ->
            asteroidAdapter.submitList(asteroids)
        })

        viewModel.pictureOfTheDay.observe(viewLifecycleOwner, Observer { pictureOfTheDay ->
            pictureOfTheDay?.let {
                if(pictureOfTheDay.isImage) {
                    Picasso.with(context).load(pictureOfTheDay.url).into(binding.activityMainImageOfTheDay)
                    binding.activityMainImageOfTheDay.contentDescription = pictureOfTheDay.title
                }
            }
        })

        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer { asteroid ->
            asteroid?.let {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.onNavigationToAsteroidDetailsComplete()
            }
        })

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
