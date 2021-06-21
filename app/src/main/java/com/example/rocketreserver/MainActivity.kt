package com.example.rocketreserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    lateinit var disposable: CompositeDisposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val subscription = apolloClient(this@MainActivity).subscribe(TripsBookedSubscription()).retry().subscribe {
            val trips = it.data?.tripsBooked
            val text = when {
                trips == null -> getString(R.string.subscriptionError)
                trips == -1 -> getString(R.string.tripCancelled)
                else -> getString(R.string.tripBooked, trips)
            }
            Snackbar.make(
                findViewById(R.id.main_frame_layout),
                text,
                Snackbar.LENGTH_LONG
            ).show()
        }
        disposable.add(subscription)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
