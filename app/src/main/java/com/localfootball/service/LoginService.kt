package com.localfootball.service

import com.localfootball.database.AppRepository
import com.localfootball.database.user.User
import io.reactivex.Completable

class LoginService {
    private companion object {
        val userDao = AppRepository.database.userDao()
        val authServerService = AuthServerService()
    }

    fun login(email: String, password: String): Completable =
        authServerService.getToken(email, password)
            .map {
                User(
                    id = it.userId,
                    email = email,
                    token = it.token
                )
            }.flatMapCompletable(userDao::insert)
}