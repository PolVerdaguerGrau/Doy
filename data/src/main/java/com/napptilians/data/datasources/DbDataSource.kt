package com.napptilians.data.datasources

import com.napptilians.commons.Response
import com.napptilians.commons.error.ErrorModel
import com.napptilians.domain.models.device.DeviceModel
import com.napptilians.domain.models.movie.MovieModel
import com.napptilians.domain.models.user.UserModel

interface DbDataSource {

    suspend fun getMovie(id: Long): Response<MovieModel, ErrorModel>

    suspend fun saveMovie(movie: MovieModel)

    suspend fun getDeviceInfo(): Response<DeviceModel, ErrorModel>

    suspend fun saveDeviceInfo(device: DeviceModel): Response<Unit, ErrorModel>

    suspend fun getUser(uid: String): Response<UserModel, ErrorModel>

    suspend fun saveUser(user: UserModel): Response<Unit, ErrorModel>

    suspend fun removeUser(uid: String): Response<Unit, ErrorModel>
}
