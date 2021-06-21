package com.example.rocketreserver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.example.rocketreserver.databinding.LaunchDetailsFragmentBinding
import io.reactivex.disposables.CompositeDisposable

class LaunchDetailsFragment : Fragment() {

    private lateinit var binding: LaunchDetailsFragmentBinding
    val args: LaunchDetailsFragmentArgs by navArgs()
    private lateinit var disposable: CompositeDisposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LaunchDetailsFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposable = CompositeDisposable()
        binding.bookButton.visibility = View.GONE
        binding.bookProgressBar.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.error.visibility = View.GONE

        val subscription = apolloClient(requireContext()).query(LaunchDetailsQuery(id = args.launchId)).subscribe(
                { response ->
                    val launch = response.data?.launch
                    if (launch == null || response.hasErrors()) {
                        binding.progressBar.visibility = View.GONE
                        binding.error.text = response.errors?.get(0)?.message
                        binding.error.visibility = View.VISIBLE
                        return@subscribe
                    }

                    binding.progressBar.visibility = View.GONE

                    binding.missionPatch.load(launch.mission?.missionPatch) {
                        placeholder(R.drawable.ic_placeholder)
                    }
                    binding.site.text = launch.site
                    binding.missionName.text = launch.mission?.name
                    val rocket = launch.rocket
                    binding.rocketName.text = "🚀 ${rocket?.name} ${rocket?.type}"

                    configureButton(launch.isBooked)
                },
                {
                    binding.progressBar.visibility = View.GONE
                    binding.error.text = "Oh no... A protocol error happened"
                    binding.error.visibility = View.VISIBLE

                }
            )
        disposable.add(subscription)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

    private fun configureButton(isBooked: Boolean) {
        binding.bookButton.visibility = View.VISIBLE
        binding.bookProgressBar.visibility = View.GONE

        binding.bookButton.text = if (isBooked) {
            getString(R.string.cancel)
        } else {
            getString(R.string.book_now)
        }

        binding.bookButton.setOnClickListener {
            val context = context
            if (context != null && User.getToken(context) == null) {
                findNavController().navigate(
                    R.id.open_login
                )
                return@setOnClickListener
            }

            binding.bookButton.visibility = View.INVISIBLE
            binding.bookProgressBar.visibility = View.VISIBLE

                val mutation = if (isBooked) {
                    CancelTripMutation(id = args.launchId)
                } else {
                    BookTripMutation(id = args.launchId)
                }

                val subscription = apolloClient(requireContext()).mutate(mutation).subscribe(
                    { response ->
                        if (response.hasErrors()) {
                            configureButton(isBooked)
                        } else {
                            configureButton(!isBooked)
                        }
                    },
                    {
                        configureButton(isBooked)
                    }
                )
        }
    }
}