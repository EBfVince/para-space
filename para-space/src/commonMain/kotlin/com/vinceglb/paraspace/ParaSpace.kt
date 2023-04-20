package com.vinceglb.paraspace

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

class ParaSpace(private val paragraphSpacing: TextUnit) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = paragraphFormat(text.text, paragraphSpacing),
            offsetMapping = paragraphMapping(text)
        )
    }

    companion object {
        fun paragraphFormat(text: String, paragraphSpacing: TextUnit = 10.sp): AnnotatedString {
            val builder = AnnotatedString.Builder()

            // We separate the text in paragraphs
            // Watch out! We remove at the same time the '\n' character
            val paragraphs = text.split("\n")

            // For each paragraphs
            paragraphs.forEachIndexed { index, paragraph ->
                // We add the paragraph
                builder.withStyle(style = ParagraphStyle()) {
                    builder.append(paragraph)
                    if (index < paragraphs.size - 1) {
                        builder.append(" ")
                    }
                }

                // If it's not the last paragraph
                if (index != paragraphs.size - 1) {
                    builder.withStyle(style = SpanStyle(fontSize = paragraphSpacing)) {
                        builder.append('\u0000')
                    }
                }
            }

            return builder.toAnnotatedString()
        }
    }

    private fun paragraphMapping(text: AnnotatedString) = object : OffsetMapping {
        /**
         * We add an hidden character at the end of every paragraph.
         * We need to add these characters to the offset
         */
        override fun originalToTransformed(offset: Int): Int {
            val paragraphs = text.split("\n")
            var count = 0
            var charactersAdded = 0

            paragraphs.forEachIndexed { index, paragraph ->
                // If offset is in the current paragraph
                if (offset <= count + paragraph.length) {
                    return offset + charactersAdded
                }

                // Increment count with the length of the current paragraph
                count += paragraph.length

                // If it's not the last paragraph, add the hidden character
                if (index < paragraphs.size - 1) {
                    count += 1
                    charactersAdded += 1
                }
            }

            throw IllegalStateException("impossible")
        }

        /**
         * We need to remove the hidden characters from the paragraphs
         */
        override fun transformedToOriginal(offset: Int): Int {
            val paragraphs = text.split("\n")
            var count = 0
            var charactersAdded = 0

            paragraphs.forEachIndexed { index, paragraph ->
                // If offset is in the current paragraph
                if (offset <= count + paragraph.length + charactersAdded) {
                    return offset - charactersAdded
                }

                // Increment count with the length of the current paragraph
                count += paragraph.length

                // If it's not the last paragraph, add the hidden character
                if (index < paragraphs.size - 1) {
                    count += 1
                    charactersAdded += 1
                }
            }

            throw IllegalStateException("impossible")
        }
    }

}