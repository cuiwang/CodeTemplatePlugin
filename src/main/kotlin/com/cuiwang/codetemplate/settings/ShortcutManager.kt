package com.cuiwang.codetemplate.settings

import com.cuiwang.codetemplate.state.CodeTemplateSettingsState
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.util.SystemInfo
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

object ShortcutManager {
    const val OPEN_INSERT_ACTION_ID = "com.cuiwang.codetemplate.action.OpenInsertPopup"

    fun applyShortcut(shortcutText: String) {
        val normalized = normalizeShortcut(shortcutText) ?: CodeTemplateSettingsState.defaultShortcut()
        val keyStroke = parseShortcut(normalized) ?: return
        val keymap = KeymapManager.getInstance().activeKeymap ?: return

        keymap.getShortcuts(OPEN_INSERT_ACTION_ID)
            .filterIsInstance<KeyboardShortcut>()
            .forEach { keymap.removeShortcut(OPEN_INSERT_ACTION_ID, it) }

        keymap.addShortcut(OPEN_INSERT_ACTION_ID, KeyboardShortcut(keyStroke, null))
    }

    fun normalizeShortcut(shortcutText: String?): String? {
        val keyStroke = parseShortcut(shortcutText ?: return null) ?: return null
        return keyStroke.toString()
    }

    fun shortcutFromKeyEvent(e: KeyEvent): String? {
        if (isModifierKey(e.keyCode)) {
            return null
        }
        return KeyStroke.getKeyStrokeForEvent(e).toString()
    }

    fun toDisplayText(shortcutText: String): String {
        val keyStroke = parseShortcut(shortcutText) ?: return shortcutText
        val parts = mutableListOf<String>()

        val modifiers = keyStroke.modifiers
        if (modifiers and InputEvent.CTRL_DOWN_MASK != 0) parts += "Ctrl"
        if (modifiers and InputEvent.ALT_DOWN_MASK != 0) parts += "Alt"
        if (modifiers and InputEvent.SHIFT_DOWN_MASK != 0) parts += "Shift"
        if (modifiers and InputEvent.META_DOWN_MASK != 0) parts += if (SystemInfo.isMac) "Cmd" else "Meta"

        if (keyStroke.keyCode != 0) {
            parts += KeyEvent.getKeyText(keyStroke.keyCode)
        }

        return if (parts.isEmpty()) shortcutText else parts.joinToString("+")
    }

    private fun parseShortcut(shortcutText: String): KeyStroke? {
        val trimmed = shortcutText.trim()
        if (trimmed.isBlank()) {
            return null
        }
        val normalized = trimmed
            .replace("⌘", "meta", ignoreCase = true)
            .replace("command", "meta", ignoreCase = true)
            .replace("cmd", "meta", ignoreCase = true)
        return KeyStroke.getKeyStroke(normalized)
            ?: KeyStroke.getKeyStroke(normalized.replace("+", " "))
    }

    private fun isModifierKey(keyCode: Int): Boolean {
        return keyCode == KeyEvent.VK_SHIFT ||
            keyCode == KeyEvent.VK_CONTROL ||
            keyCode == KeyEvent.VK_ALT ||
            keyCode == KeyEvent.VK_META
    }
}
