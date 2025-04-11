package com.example.horizon.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import kotlinx.coroutines.delay

/**
 * A [Text] composable that displays the given [text] with a typewriter-style animation.
 *
 * Characters are revealed one-by-one with a delay defined by [delayBetweenEachChar].
 *
 * @param text The complete text to display.
 * @param modifier Modifier to apply to the text.
 * @param delayBetweenEachChar Lambda returning the delay (in milliseconds) between each character.
 * @param color Color of the text.
 * @param fontSize Font size of the text.
 * @param fontStyle Style of the font (e.g., Italic).
 * @param fontWeight Weight of the font (e.g., Bold).
 * @param fontFamily Font family to use.
 * @param letterSpacing Additional letter spacing.
 * @param textDecoration Optional text decorations (underline, strikethrough).
 * @param textAlign Text alignment.
 * @param lineHeight Line height for the text.
 * @param overflow Overflow behavior (e.g., ellipsis).
 * @param softWrap Whether text should break at soft line breaks.
 * @param maxLines Maximum number of lines to display.
 * @param minLines Minimum number of lines to display.
 * @param onTextLayout Callback with layout result of the text.
 * @param style Overall text style (merged with others).
 */
@Composable
fun TypingAnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    delayBetweenEachChar: () -> Long = { 50 },
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    var currentText by rememberSaveable(text) { mutableStateOf("") }
    var currentIndex by rememberSaveable(text) { mutableStateOf(0) }

    LaunchedEffect(text) {
        currentText = ""
        currentIndex = 0
        while (currentIndex < text.length) {
            currentText += text[currentIndex]
            currentIndex++
            delay(delayBetweenEachChar())
        }
    }

    Text(
        text = currentText,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}
