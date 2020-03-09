package com.localfootball.page.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.localfootball.R
import com.localfootball.page.home.HomeActivity
import com.localfootball.page.registration.RegistrationActivity
import com.localfootball.service.AnimationService
import com.localfootball.service.LoginService
import com.localfootball.service.PlayerService
import com.localfootball.util.setSafeOnClickListener
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val animationService = AnimationService()
    private val playerService = PlayerService()
    private val loginService = LoginService()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerService.getLoggedUser()
            .unsubscribeOn(io())
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe(
                {
                    onLoggedPlayer()
                },
                {
                    onNotLoggedPlayer()
                }
            )
    }

    private fun login() =
        loginService.login(
            email = loginPageEmailEditText.text.toString(),
            password = loginPagePasswordEditText.text.toString()
        )
            .unsubscribeOn(io())
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe(
                {
                    onLoggedPlayer()
                },
                {
                    makeText(
                        applicationContext,
                        getString(R.string.wrong_login_or_password),
                        LENGTH_LONG
                    ).show()
                }
            )

    private fun onLoggedPlayer() {
        Intent(applicationContext, HomeActivity::class.java).let(::startActivity)
        finish()
    }

    private fun onNotLoggedPlayer() {
        setContentView(R.layout.activity_login)
        configureBallImageView()
        configureRegistrationTextViewButton()
        configureLoginButton()
    }

    private fun configureLoginButton() =
        loginPageLoginButton.setSafeOnClickListener {
            login()
        }

    private fun configureRegistrationTextViewButton() =
        loginPageRegistrationTextView.setSafeOnClickListener {
            Intent(applicationContext, RegistrationActivity::class.java).let(::startActivity)
        }

    private fun configureBallImageView() =
        animationService.startInfiniteSlowVelocityRotating(
            applicationContext,
            loginPageBallImageView
        )
}


