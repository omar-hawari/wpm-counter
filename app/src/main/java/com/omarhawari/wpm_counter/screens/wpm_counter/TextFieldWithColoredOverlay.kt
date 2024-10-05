package com.omarhawari.wpm_counter.screens.wpm_counter

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.omarhawari.wpm_counter.ui.theme.CorrectTextGreen
import com.omarhawari.wpm_counter.ui.theme.IncorrectTextRed

/**
 *  TextFieldWithColoredOverlay contains:
 *  - TextField: To accept user input and handle key events
 *  - Text: To show the user input with colored overlay (Correct/Incorrect)
 *
 *  @param modifier: Modifier for styling
 *  @param onValueChange: Callback to handle user input in the viewModel
 *  @param letterMatchList: List to show the user input with colored overlay (Correct/Incorrect)
 *  @param isFinished: Disables/Enables the TextField based on if the user has finished the text
 * */
@Composable
fun TextFieldWithColoredOverlay(
    modifier: Modifier = Modifier,
    onValueChange: (Char, /*Key Down Time*/ Long, /*Key Up Time*/ Long) -> Unit,
    letterMatchList: List<Pair<Char, Boolean>>,
    isFinished: Boolean
) {

    val rememberKeyReleaseTime = remember { mutableLongStateOf(0L) }

    var userInput by remember {
        mutableStateOf(TextFieldValue(""))
    }

    Box(
        modifier = modifier
            .onPreviewKeyEvent {
                Log.e("Key event!", it.toString())
                false
            }
    ) {
        // TextField to accept user input and handle key events.
        TextField(
            value = userInput,
            onValueChange = {
                val (keyPressTime, keyReleaseTime) = generateKeyStrokeAnalytics(rememberKeyReleaseTime, System.currentTimeMillis())

                if (it.text.length > userInput.text.length) { // This disables backspace and any deletion
                    userInput =
                        userInput.copy(
                            text = it.text,
                            selection = TextRange(it.text.length) // Keep the cursor at the end of the text
                        )

                    onValueChange(it.text.last(), keyPressTime, keyReleaseTime) // Pass the last entered character to the viewModel with keyPressTime and keyReleaseTime
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.None,
                keyboardType = KeyboardType.Text,
            ),
            modifier = Modifier
                .alpha(0f) // The text field is hidden as the user will be able to see the input in the overlay with the appropriate colors
                .fillMaxSize()
                .testTag(TEST_TAG_TEXT_FIELD),
            enabled = !isFinished,
        )

        // Overlay to show the user input with colored overlay (Correct/Incorrect)
        Text(
            text = buildAnnotatedString {
                letterMatchList.forEach { char ->
                    if (char.second) {
                        withStyle(style = SpanStyle(color = CorrectTextGreen)) {
                            append(char.first)
                        }
                    } else {
                        withStyle(style = SpanStyle(color = IncorrectTextRed)) {
                            append(char.first)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onPreviewKeyEvent {
                    
                    false
                }
                .padding(16.dp), // Match padding with the TextField
        )
    }
}

/**
 * Generates keyPressTime and keyReleaseTime for a key stroke
 *
 * @param keyReleaseTime: Last keyReleaseTime generated. Used to calculate keyPressTime
 * @param currentTime: Current time in milliseconds
 *
 * @return KeyStrokeAnalytics: Contains keyPressTime and keyReleaseTime
 * */
fun generateKeyStrokeAnalytics(keyReleaseTime: MutableState<Long>, currentTime: Long): KeyStrokeAnalytics {
    if (keyReleaseTime.value == 0L) {
        keyReleaseTime.value = currentTime
    }

    // Current keyPressTime equals previous keyReleaseTime
    val keyPressTime = keyReleaseTime.value

    // keyReleaseTime equals current time
    keyReleaseTime.value = currentTime

    return KeyStrokeAnalytics(keyPressTime, keyReleaseTime.value)
}

data class KeyStrokeAnalytics(val keyPressTime: Long, val keyReleaseTime: Long)