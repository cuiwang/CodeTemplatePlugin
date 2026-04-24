package com.cuiwang.codetemplate.settings

import com.cuiwang.codetemplate.state.CodeTemplateSettingsState
import com.intellij.openapi.util.SystemInfo
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTextField

class ShortcutCaptureField : JTextField() {
    private var shortcut: String = CodeTemplateSettingsState.defaultShortcut()

    init {
        columns = 20
        isEditable = false
        text = ShortcutManager.toDisplayText(shortcut)
        val shortcutExample = if (SystemInfo.isMac) "Cmd+I" else "Ctrl+I"
        toolTipText = "点击后按下快捷键，例如 $shortcutExample"

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                requestFocusInWindow()
            }
        })

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                val captured = ShortcutManager.shortcutFromKeyEvent(e) ?: return
                shortcut = captured
                text = ShortcutManager.toDisplayText(captured)
                e.consume()
            }
        })
    }

    fun setShortcut(shortcutText: String) {
        shortcut = ShortcutManager.normalizeShortcut(shortcutText) ?: CodeTemplateSettingsState.defaultShortcut()
        text = ShortcutManager.toDisplayText(shortcut)
    }

    fun getShortcut(): String = shortcut
}
