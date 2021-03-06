package com.napptilians.diskdatasource.data

import com.napptilians.commons.Failure
import com.napptilians.commons.Response
import com.napptilians.commons.Success
import com.napptilians.commons.error.ErrorModel
import com.napptilians.data.datasources.DbDataSource
import com.napptilians.diskdatasource.Cache
import com.napptilians.diskdatasource.dao.DeviceDao
import com.napptilians.diskdatasource.dao.ExampleDao
import com.napptilians.diskdatasource.dao.UserDao
import com.napptilians.diskdatasource.mappers.DeviceInDbMapper
import com.napptilians.diskdatasource.mappers.DeviceOutDbMapper
import com.napptilians.diskdatasource.mappers.MovieDbMapper
import com.napptilians.diskdatasource.mappers.UserDbMapper
import com.napptilians.domain.models.device.DeviceModel
import com.napptilians.domain.models.movie.MovieModel
import com.napptilians.domain.models.user.UserModel
import javax.inject.Inject

class DbDataBaseImpl @Inject constructor(
    private val exampleDao: ExampleDao,
    private val movieDbMapper: MovieDbMapper,
    private val deviceDao: DeviceDao,
    private val userDao: UserDao,
    private val deviceInDbMapper: DeviceInDbMapper,
    private val deviceOutDbMapper: DeviceOutDbMapper,
    private val userDbMapper: UserDbMapper
) : DbDataSource {

    override suspend fun getMovie(id: Long): Response<MovieModel, ErrorModel> =
        exampleDao.getMovie(id)?.let {
            Cache.checkTimestampCache(it.timeStamp, movieDbMapper.map(it))
        } ?: run { Failure(ErrorModel("")) }

    override suspend fun saveMovie(movie: MovieModel) {
        return exampleDao.insertWithTimestamp(movieDbMapper.mapToDbModel(movie))
    }

    override suspend fun getDeviceInfo(): Response<DeviceModel, ErrorModel> =
        deviceDao.getDeviceInfo()?.let {
            Success(deviceOutDbMapper.map(it))
        } ?: run { Failure(ErrorModel("db error")) }

    override suspend fun saveDeviceInfo(device: DeviceModel): Response<Unit, ErrorModel> =
        Success(deviceDao.insertDeviceInfo(deviceInDbMapper.map(device)))

    override suspend fun getUser(uid: String): Response<UserModel, ErrorModel> =
        userDao.getUser(uid)?.let {
            Success(userDbMapper.map(it))
        } ?: run { Failure(ErrorModel("db error")) }

    override suspend fun saveUser(user: UserModel): Response<Unit, ErrorModel> =
        Success(userDao.insertUser(userDbMapper.mapToDbModel(user)))

    override suspend fun removeUser(uid: String): Response<Unit, ErrorModel> =
        Success(userDao.deleteUser(uid))
}