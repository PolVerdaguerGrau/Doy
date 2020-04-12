package com.napptilians.features.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.napptilians.commons.error.ErrorModel
import com.napptilians.commons.valueOrNull
import com.napptilians.domain.models.user.UserModel
import com.napptilians.domain.usecases.GetDeviceInfoUseCase
import com.napptilians.domain.usecases.GetUserUseCase
import com.napptilians.domain.usecases.LogoutUseCase
import com.napptilians.domain.usecases.UpdateUserUseCase
import com.napptilians.features.UiStatus
import com.napptilians.features.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val getDeviceInfoUseCase: GetDeviceInfoUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<UserModel>() {

    private val userUid: String get() = firebaseAuth.currentUser?.uid ?: ""

    private var email: String = ""
    private var name: String = ""
    private var description: String = ""
    private var image: String = ""

    private val _userDataStream = MutableLiveData<UiStatus<UserModel, ErrorModel>>()
    val userDataStream: LiveData<UiStatus<UserModel, ErrorModel>> get() = _userDataStream

    private val _updateUserDataStream = MutableLiveData<UiStatus<UserModel, ErrorModel>>()
    val updateUserDataStream: LiveData<UiStatus<UserModel, ErrorModel>> get() = _updateUserDataStream

    private val _logoutDataStream = MutableLiveData<UiStatus<Unit, ErrorModel>>()
    val logoutDataStream: LiveData<UiStatus<Unit, ErrorModel>> get() = _logoutDataStream

    init {
        // Get user info.
        getUser(userUid)
    }

    fun updateUser(name: String? = null, token: String? = null, description: String? = null, image: String? = null) {
        viewModelScope.launch {
            _updateUserDataStream.value = emitLoadingState()
            val request = updateUserUseCase(userUid, name, token, description, image)
            _updateUserDataStream.value = processModel(request)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutDataStream.value = emitLoadingState()
            val request = logoutUseCase(userUid)
            firebaseAuth.signOut()
            _logoutDataStream.value = processModel(request)
        }
    }

    fun getUserEmail(): String = email

    fun getUserName(): String = name

    fun getUserDescription(): String = description

    fun getUserImage(): String = image

    private fun getUser(userUid: String) {
        viewModelScope.launch {
            _userDataStream.value = emitLoadingState()

            // Get token from device.
            val deviceTokenRequest = getDeviceInfoUseCase.execute()
            val deviceToken = deviceTokenRequest.valueOrNull()?.firebaseToken ?: ""

            val getUserRequest = getUserUseCase(userUid)

            // Check if push token is the same.
            getUserRequest.valueOrNull()?.let { user ->
                email = user.email
                name = user.name
                description = user.description
                image = user.image
                if (user.pushToken != deviceToken) {
                    refreshPushToken(userUid, deviceToken)
                }
            }
            _userDataStream.value = processModel(getUserRequest)
        }
    }

    private suspend fun refreshPushToken(userUid: String, token: String) {
        viewModelScope.launch {
            updateUserUseCase(userUid = userUid, token = token)
        }
    }
}