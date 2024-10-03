package com.omarhawari.wpm_counter.screens.username

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarhawari.wpm_counter.database.daos.User
import com.omarhawari.wpm_counter.domain.WPMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing users and fetching users with their high scores.
 * It interacts with the repository to fetch data and create new sessions.
 */
@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val repository: WPMRepository,
) : ViewModel() {

    // A flow that holds the current sessionId. Initially null and will be updated when a session is created
    val sessionIdFlow = MutableStateFlow<String?>(null)

    // A flow that holds a list of users with high scores. Initially empty and will be updated by collecting data from the repository
    var usersWithScore = MutableStateFlow<List<User>>(listOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUsersWithHighScore().collect {
                usersWithScore.emit(it)
            }
        }
    }

    /**
     * Creates a new user if it doesn't exist and starts a new session for the user.
     * Emits the created session's ID to the sessionIdFlow.
     *
     * @param username The username of the user for whom the session is created.
     */
    fun startSession(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.insertUserIfNotExists(username)
            val session = repository.insertSession(user.uuid)
            sessionIdFlow.emit(session.uuid)
        }
    }
}