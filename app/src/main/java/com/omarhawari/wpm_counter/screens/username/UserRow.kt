package com.omarhawari.wpm_counter.screens.username

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omarhawari.wpm_counter.database.daos.User
import com.omarhawari.wpm_counter.exts.toTwoDecimalPlaces


@Composable
fun UserRow(
    modifier: Modifier = Modifier,
    user: User,
    onClick: (String) -> Unit,
) {

    Row(
        modifier
            .clickable(
                onClick = { onClick(user.uuid) },
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple()
            )
            .padding(16.dp)
    ) {

        Text(text = user.username, modifier = Modifier.weight(1f))

        user.highScore?.let { highScore ->
            Text(text = "High score: ${highScore.toTwoDecimalPlaces()}")
        } ?: run {
            Text(text = "Start new session")
        }

    }

}