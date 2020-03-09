package com.localfootball.page.registration

import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.localfootball.R
import com.localfootball.page.common.CustomDatePickerDialogFragment
import com.localfootball.exception.UiException
import com.localfootball.model.RegistrationRequest
import com.localfootball.model_parcelable.GenderParcelable
import com.localfootball.model_parcelable.LocalDateParcelable
import com.localfootball.service.AnimationService
import com.localfootball.service.PlayerService
import com.localfootball.util.*
import com.localfootball.util.DatePickerMode.BIRTH_DATE
import com.localfootball.util.state_updater.ActivityStateUpdater
import com.localfootball.util.state_updater.state_key.REGISTRATION_BIRTH_DATE_STATE_KEY
import com.localfootball.util.state_updater.state_key.REGISTRATION_GENDER_STATE_KEY
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity(),
    ActivityStateUpdater {

    companion object {
        val registrationCurrentValuesState = Bundle()
    }

    private val animationService = AnimationService()
    private val playerService = PlayerService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(registrationCurrentValuesState)
        setContentView(R.layout.activity_registration)

        configureBackTextView()
        configureRegistrationButton()
        configureSlideAnimationForAllComponents()
        configureAnimationOfBallImageView()
        configureChooseGenderDialogButton()
        configureBirthDateButton()
    }

    private fun configureBirthDateButton() {
        registerPageBirthDateButton.setOnClickListener {
            CustomDatePickerDialogFragment.newInstance(BIRTH_DATE, this)
                .show(supportFragmentManager, null)
        }
    }

    private fun configureChooseGenderDialogButton() {
        registerPageGenderButton.setSafeOnClickListener {
            GenderPickerDialogFragment.newInstance(this).show(supportFragmentManager, null)
        }
    }

    private fun configureAnimationOfBallImageView() =
        animationService.startInfiniteMediumVelocityRotating(
            applicationContext,
            registerPageSecondaryBallImageView
        )

    private fun configureRegistrationButton() {
        registerPageRegistrationButton.setSafeOnClickListener {
            getValidatedEditTexts().let { validatedEditTexts ->
                if (validatedEditTexts.all { it.valid }) {
                    register()
                } else validatedEditTexts.filterNot { it.valid }.forEach {
                    it.editText.error = it.errorMessage
                }
            }
        }
    }

    private fun register() =
        playerService.register(
            createRegistrationRequestBody()
        )
            .subscribeOn(io())
            .unsubscribeOn(io())
            .observeOn(mainThread())
            .subscribe(
                {
                    makeText(this, getString(R.string.registration_is_successful), LENGTH_LONG).show()
                    finish()
                },
                {
                    if (it is UiException) {
                        makeText(
                            this,
                            getString(it.getErrorMessageResourceId()),
                            LENGTH_LONG
                        ).show()
                    }
                }
            )

    private fun createRegistrationRequestBody() =
        RegistrationRequest(
            nickname = registerPageNickEditText.text.toString().toLowerCase(),
            email = registerPageEmailEditText.text.toString(),
            password = registerPagePasswordEditText.text.toString().toLowerCase(),
            birthDate = (registrationCurrentValuesState.get(REGISTRATION_BIRTH_DATE_STATE_KEY) as LocalDateParcelable).localDate,
            gender = (registrationCurrentValuesState.get(REGISTRATION_GENDER_STATE_KEY) as GenderParcelable?)!!.gender
        )

    private fun configureBackTextView() =
        registerPageBackButtonTextView.setOnClickListener {
            finish()
        }

    private fun configureSlideAnimationForAllComponents() {
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            500,
            registerPageCreateAccountTextView,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            560,
            registerPageNickComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            590,
            registerPageEmailComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            620,
            registerPagePasswordComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            650,
            registerPageRePasswordComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            680,
            registerPageBirthDateComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            710,
            registerPageGenderComponentConstraintLayout,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            740,
            registerPageFootballerImageView,
            0.0f,
            1.0f
        )
        animationService.rightToLeftSideSlideWithFadeInAnimation(
            760,
            registerPageRegistrationButton,
            0.0f,
            1.0f
        )
    }

    private fun getValidatedEditTexts() = setOf(
        registerPageNickEditText.validate(
            isValid = registerPageNickEditText.text.toString().isValidNickFormat(),
            errorMessage = getString(R.string.wrong_nick_format_text)
        ),
        registerPageEmailEditText.validate(
            isValid = registerPageEmailEditText.text.toString().isValidEmailFormat(),
            errorMessage = getString(R.string.wrong_email_format_text)
        ),
        registerPagePasswordEditText.validate(
            isValid = registerPagePasswordEditText.text.toString().isValidPasswordFormat(),
            errorMessage = getString(R.string.wrong_password_format_text)
        ),
        registerPageRePasswordEditText.validate(
            isValid = registerPageRePasswordEditText.text.toString().isPasswordConfirmed(
                registerPagePasswordEditText.text.toString()
            ),
            errorMessage = getString(R.string.wrong_re_password_does_not_match_text)
        )
    )

    override fun updateActivityCustomState(key: String, parcelable: Parcelable) {
        registrationCurrentValuesState.putParcelable(key, parcelable)
    }
}