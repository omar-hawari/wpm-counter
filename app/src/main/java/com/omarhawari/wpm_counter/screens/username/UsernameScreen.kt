package com.omarhawari.wpm_counter.screens.username

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun UsernameScreen(
    modifier: Modifier = Modifier,
    navController: NavController?,
    viewModel: UsernameViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.sessionIdFlow.collect {
            if (it != null) {
                navController?.navigate("wpm_counter/${it}")
                viewModel.sessionIdFlow.value = null // This is to mark the session as consumed
            }
        }
    }

    val usersWithScore by viewModel.usersWithScore.collectAsState(listOf())

    var username by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(TEST_TAG_USER_SCREEN),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Enter your username or select your username from the list below",
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            placeholder = { Text(text = "Username") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TEST_TAG_USER_TEXT_FIELD)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Button(
            onClick = {
                viewModel.startSession(username)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TEST_TAG_USER_BUTTON)
                .padding(horizontal = 16.dp),
            enabled = username.isNotBlank()
        ) {
            Text(text = "Continue")
        }


        usersWithScore.forEach { user ->
            UserRow(
                user = user,
                onClick = {
                    viewModel.startSession(user.username)
                }
            )
        }

    }
}

const val TEST_TAG_USER_SCREEN = "user_screen_test_tag"
const val TEST_TAG_USER_TEXT_FIELD = "username_text_field"
const val TEST_TAG_USER_BUTTON = "username_button"