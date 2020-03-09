package com.localfootball.service

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.makeText
import com.localfootball.MyApplication
import com.localfootball.page.login.LoginActivity
import com.localfootball.database.AppRepository
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io

class LogoutService {
    private companion object {
        val appContext = MyApplication.appContext
        val userDao = AppRepository.database.userDao()
    }

    @SuppressLint("CheckResult")
    fun logout() {
        removeLocalUserData()
            .unsubscribeOn(io())
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe(
                {
                    restartAppAfterLogout()
                    makeText(appContext, "LOGGED OUT", LENGTH_LONG).show()
                },
                {
                    makeText(appContext, it.message, LENGTH_LONG).show()
                }
            )
    }

    private fun removeLocalUserData() = userDao.removeAll()

    private fun restartAppAfterLogout() {
        Intent(appContext, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.let {
            appContext.startActivity(it)
        }
    }
}