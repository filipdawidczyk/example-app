package com.localfootball.page.your_events

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.localfootball.R
import com.localfootball.model.YourEventResponse
import com.localfootball.service.AnimationService
import com.localfootball.service.EventService
import com.localfootball.util.setSafeOnClickListener
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_your_events.*

class YourEventsActivity : AppCompatActivity() {

    companion object {
        var yourEventsList = listOf<YourEventResponse>()
    }

    private val animationService = AnimationService()
    private val eventService = EventService()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_events)

        configureAnimationOfBallImageView()
        configureBackTextView()

        loadParticipationsOfLoggedPlayer()

        configureYourEventsPageEnterAnimations()
    }

    @SuppressLint("CheckResult")
    private fun loadParticipationsOfLoggedPlayer() {
        eventService.getParticipationsOfLoggedPlayer()
            .unsubscribeOn(io())
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe(
                {
                    yourEventsList = it
                    configureYourEventsRecyclerViewPageAdapter()
                },
                {
                    makeText(applicationContext, it.message, LENGTH_LONG).show()
                }
            )
    }

    private fun configureYourEventsRecyclerViewPageAdapter() {
        yourEventsPageRecyclerView.layoutManager =
            LinearLayoutManager(this, VERTICAL, false)
        yourEventsPageRecyclerView.adapter =
            YourEventsRecyclerViewAdapter(
                this,
                yourEventsList
            )
        yourEventsPageRecyclerView.addItemDecoration(YourEventsRecyclerViewDecorator(20, 20))
    }

    private fun configureAnimationOfBallImageView() =
        animationService.startInfiniteMediumVelocityRotating(
            applicationContext,
            yourEventsPageSecondaryBallImageView
        )

    private fun configureBackTextView() =
        yourEventsPageBackTextView.setSafeOnClickListener {
            finish()
        }

    private fun configureYourEventsPageEnterAnimations() {
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            700,
            yourEventsPageTitleTextView,
            0.1f,
            1.0f
        )
    }
}