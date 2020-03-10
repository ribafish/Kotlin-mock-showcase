package com.example.kotlinmockshowcase.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinmockshowcase.R
import com.example.kotlinmockshowcase.databinding.MainFragmentBinding
import com.example.kotlinmockshowcase.general.NetworkResult
import timber.log.Timber

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var viewAdapter: PostsAdapter


    companion object {
        fun newInstance() = MainFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, MainViewModelFactory).get(MainViewModel::class.java)

        viewAdapter = PostsAdapter()
        binding.recyclerView.adapter = viewAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.toolbar.setTitle(R.string.app_name)

        // Observe the mockyPosts and update the list when possible and show a toast with the error otherwise
        viewModel.mockyPosts?.observe(viewLifecycleOwner, Observer { posts ->
            if (posts != null)
                viewAdapter.posts = posts
        })
        viewModel.networkResult.observe(viewLifecycleOwner, Observer { result ->
            Timber.w("Result: $result")
            when (result) {
                is NetworkResult.Error -> {
                    Timber.e("Error: ${result.error}")
                    Toast.makeText(this.context, result.error, Toast.LENGTH_SHORT).show()
                    binding.swipeRefresh.isRefreshing = false
                }
                is NetworkResult.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                is NetworkResult.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                }
            }
        })

        viewModel.refreshPosts()

        binding.filterSwitch.setOnCheckedChangeListener {
                _, enabled ->
            viewModel.filterUserId.value = if (enabled) 1 else null
            viewModel.postsOrder.value = if (enabled) MainViewModel.Order.Descending else null
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }


    }

}
