package com.example.rocketreserver

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rocketreserver.databinding.LaunchListFragmentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LaunchListFragment : Fragment() {
    private lateinit var binding: LaunchListFragmentBinding
    private lateinit var disposable: CompositeDisposable
    private lateinit var launches: MutableList<LaunchListQuery.Launch>
    private lateinit var adapter: LaunchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        disposable = CompositeDisposable()
        binding = LaunchListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

    var cursor: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launches = mutableListOf()
        adapter = LaunchListAdapter(launches)
        binding.launches.layoutManager = LinearLayoutManager(requireContext())
        binding.launches.adapter = adapter


        getMoreData()
        adapter.onEndOfListReached = {
            disposable.dispose()
            getMoreData()
        }

        adapter.onItemClicked = { launch ->
            findNavController().navigate(
                LaunchListFragmentDirections.openLaunchDetails(launchId = launch.id)
            )
        }
    }

    private fun getMoreData() {
        val subscription = apolloClient(requireContext()).query(
            LaunchListQuery(
                cursor = cursor
            )
        )
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
            { response ->
                val newLaunches = response.data?.launches?.launches?.filterNotNull()

                if (newLaunches != null) {
                    launches.addAll(newLaunches)
                    adapter.notifyDataSetChanged()
                }

                cursor = response.data?.launches?.cursor
                if (response.data?.launches?.hasMore != true) {
                    adapter.onEndOfListReached = null
                }

            },
            {
                Log.d("LaunchList", "Failure", it)
            }
        )

        disposable.add(subscription)

    }
}