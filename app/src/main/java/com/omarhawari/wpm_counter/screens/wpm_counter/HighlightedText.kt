package com.omarhawari.wpm_counter.screens.wpm_counter

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *  HighlightedText: Displays the WPM Text with a highlight on the character were the cursor is
 *
 *  @param modifier: Modifier for styling
 *  @param text: The text to be displayed
 *  @param cursor: The index of the character were the cursor is
 *
 * */

@Composable
fun HighlightedText(modifier: Modifier = Modifier, text: String, cursor: Int) {
    Text(
        text = buildAnnotatedString {

            val beforeCursor = text.substring(startIndex = 0, endIndex = cursor)
            val cursorChar = text[cursor]
            val afterChar = text.substring(
                startIndex = cursor + 1,
                endIndex = text.length
            )

            append(beforeCursor)

            withStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            ) {
                append(cursorChar)
            }
            append(afterChar)
        },
        modifier
            .fillMaxWidth()
            .padding(16.dp),
    )

}