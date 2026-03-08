package com.pharmadelivery.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharmadelivery.data.models.User
import com.pharmadelivery.data.models.UserRole
import com.pharmadelivery.data.repository.MockData
import com.pharmadelivery.data.repository.OrderRepository
import com.pharmadelivery.data.repository.SessionRepository
import com.pharmadelivery.utils.NetworkMonitor
import com.pharmadelivery.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AppState {
    object Loading : AppState()
    object NoInternet : AppState()
    data class Authenticated(val user: User) : AppState()
    object Unauthenticated : AppState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val orderRepository: OrderRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val networkState: StateFlow<NetworkState> = networkMonitor.networkState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NetworkState.Available)

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState: StateFlow<AppState> = _appState

    init {
        checkSessionAndInit()
    }

    private fun checkSessionAndInit() {
        viewModelScope.launch {
            // Seed demo data so there's something to look at
            orderRepository.seedSampleOrders()

            val user = sessionRepository.getActiveSession()
            _appState.value = if (user != null) {
                AppState.Authenticated(user)
            } else {
                AppState.Unauthenticated
            }
        }
    }

    fun loginAs(role: UserRole) {
        viewModelScope.launch {
            val user = when (role) {
                UserRole.CUSTOMER -> MockData.currentCustomer
                UserRole.PHARMACY -> MockData.currentPharmacyOwner
                UserRole.RIDER -> MockData.currentRider
                UserRole.ADMIN -> MockData.currentAdmin
            }
            sessionRepository.saveSession(user)
            _appState.value = AppState.Authenticated(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionRepository.clearSession()
            _appState.value = AppState.Unauthenticated
        }
    }
}
