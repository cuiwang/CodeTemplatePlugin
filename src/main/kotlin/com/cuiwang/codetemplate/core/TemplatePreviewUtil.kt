package com.cuiwang.codetemplate.core

object TemplatePreviewUtil {
    fun toInlinePreview(content: String, maxLength: Int = 40): String {
        val oneLine = content.replace(Regex("\\s+"), " ").trim()
        if (oneLine.isEmpty()) {
            return "(空模板)"
        }
        return oneLine.takeWithEllipsis(maxLength)
    }

    fun toMultilinePreview(content: String, maxLines: Int = 4, maxCharsPerLine: Int = 72): String {
        val lines = content.lines()
            .map { it.trimEnd() }
            .filter { it.isNotBlank() }

        if (lines.isEmpty()) {
            return "(空模板)"
        }

        val selected = lines.take(maxLines).map { it.takeWithEllipsis(maxCharsPerLine) }
        val suffix = if (lines.size > maxLines) "\n..." else ""
        return selected.joinToString("\n") + suffix
    }

    private fun String.takeWithEllipsis(limit: Int): String {
        if (length <= limit) {
            return this
        }
        if (limit <= 1) {
            return "…"
        }
        return take(limit - 1) + "…"
    }
}
