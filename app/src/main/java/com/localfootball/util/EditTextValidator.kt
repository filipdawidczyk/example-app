package com.localfootball.util

import android.util.Patterns
import android.widget.EditText

private const val NICK_REGEX = "[a-z]|[a-z0-9]{5,30}"
private const val PASSWORD_REGEX = "^(?![0-9])[0-9a-z]|[a-z0-9]{8,30}$"

data class ValidatedEditText(
    val editText: EditText,
    val valid: Boolean,
    val errorMessage: String? = "invalid value"
)

fun EditText.validate(isValid: Boolean, errorMessage: String) = ValidatedEditText(
    editText = this,
    valid = isValid,
    errorMessage = errorMessage.takeIf { !isValid }
)

fun String.isValidEmailFormat(): Boolean =
    this.isNotEmpty() &&
            Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidNickFormat(): Boolean =
    this.isNotEmpty() &&
            NICK_REGEX.toPattern().matcher(this).matches()

fun String.isValidPasswordFormat(): Boolean =
    this.isNotEmpty() &&
            PASSWORD_REGEX.toPattern().matcher(this).matches()

fun String.isPasswordConfirmed(confirmPassword: String): Boolean =
    this.isNotEmpty() && confirmPassword == this
