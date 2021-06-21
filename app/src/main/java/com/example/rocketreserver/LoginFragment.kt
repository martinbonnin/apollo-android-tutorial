package com.example.rocketreserver

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rocketreserver.databinding.LoginFragmentBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private lateinit var disposable: CompositeDisposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposable = CompositeDisposable()

        binding.submitProgressBar.visibility = View.GONE
        binding.submit.setOnClickListener {
            val email = binding.email.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.error = getString(R.string.invalid_email)
                return@setOnClickListener
            }

            binding.submitProgressBar.visibility = View.VISIBLE
            binding.submit.visibility = View.GONE
            val subscription = apolloClient(requireContext()).mutate(LoginMutation(email = email)).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                { response ->
                    val login = response?.data?.login
                    if (login == null || response.hasErrors()) {
                        binding.submitProgressBar.visibility = View.GONE
                        binding.submit.visibility = View.VISIBLE
                        return@subscribe
                    }

                    User.setToken(requireContext(), login)
                    findNavController().popBackStack()
                },
                {

                }
            )
            disposable.add(subscription)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        disposable.dispose()
    }
}