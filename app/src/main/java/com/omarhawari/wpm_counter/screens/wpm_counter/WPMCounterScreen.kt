package com.omarhawari.wpm_counter.screens.wpm_counter

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omarhawari.wpm_counter.database.daos.ScreenOrientation
import com.omarhawari.wpm_counter.exts.toTwoDecimalPlaces
import com.omarhawari.wpm_counter.ui.theme.CorrectTextGreen
import com.omarhawari.wpm_counter.ui.theme.IncorrectTextRed


@Composable
fun WPMCounterScreen(
    sessionId: String,
    modifier: Modifier = Modifier,
    viewModel: WPMCounterViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.init(sessionId)
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val accuracy by viewModel.wpmCounter.accuracy.collectAsState()
    val wpmCount by viewModel.wpmCounter.wpmCount.collectAsState()

    val cursor by viewModel.cursor.collectAsState()
    val isFinished by viewModel.wpmCounter.isFinished.collectAsState()

    val configuration = LocalConfiguration.current

    Column(modifier.testTag(TEST_TAG_WPM_SCREEN), verticalArrangement = Arrangement.Center) {

        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {

            currentUser?.let {
                Text(
                    "User: ${viewModel.currentUser.value?.username}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isFinished) "Final " else ""}WPM is ${
                        wpmCount.toTwoDecimalPlaces()
                    }",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFinished) CorrectTextGreen else Color.Unspecified,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .testTag(TEST_TAG_WPM),
                )

                Text(
                    text = "Accuracy ${accuracy.toTwoDecimalPlaces()}%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CorrectTextGreen.copy(alpha = accuracy / 100f)
                        .compositeOver(IncorrectTextRed.copy(alpha = 1 - accuracy / 100f)),
                    modifier = Modifier.testTag(TEST_TAG_ACCURACY)
                )
            }
        }

        HighlightedText(text = viewModel.text.value, cursor = cursor)

        TextFieldWithColoredOverlay(
            modifier = Modifier.weight(1f),
            onValueChange = { key, keyDownTime, keyUpTime ->
                viewModel.onCharReceived(
                    key,
                    keyDownTime,
                    keyUpTime,
                    detectOrientation(configuration)
                )
            },
            letterMatchList = viewModel.letterMatchList,
            isFinished = isFinished,
        )
    }
}

// Detect Portrait vs Landscape orientation. To be called on each keystroke
fun detectOrientation(configuration: Configuration): ScreenOrientation {
    return when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> ScreenOrientation.LANDSCAPE
        else -> ScreenOrientation.PORTRAIT
    }
}

const val ARG_SESSION_ID = "sessionId"

const val TEST_TAG_WPM_SCREEN = "wpm_screen"
const val TEST_TAG_TEXT_FIELD = "text_field"
const val TEST_TAG_ACCURACY = "accuracy"
const val TEST_TAG_WPM = "wpm"