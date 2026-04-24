package com.cuiwang.codetemplate.settings

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.Component
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import javax.swing.AbstractCellEditor
import javax.swing.JTable
import javax.swing.KeyStroke
import javax.swing.ScrollPaneConstants
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

class TemplateContentCellEditor : AbstractCellEditor(), TableCellEditor {
    private val textArea = JBTextArea().apply {
        lineWrap = false
        wrapStyleWord = false
        border = JBUI.Borders.empty(4)
        tabSize = 4

        val finishAction = "finish-edit"
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), finishAction)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.META_DOWN_MASK), finishAction)
        actionMap.put(finishAction, object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                stopCellEditing()
            }
        })
    }

    private val scrollPane = JBScrollPane(textArea).apply {
        border = JBUI.Borders.empty()
        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
    }

    override fun getTableCellEditorComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        row: Int,
        column: Int,
    ): Component {
        textArea.text = (value as? String).orEmpty()
        textArea.caretPosition = textArea.text.length
        return scrollPane
    }

    override fun getCellEditorValue(): Any = textArea.text
}

class TemplateContentCellRenderer : JBTextArea(), TableCellRenderer {
    init {
        isEditable = false
        lineWrap = true
        wrapStyleWord = true
        border = JBUI.Borders.empty(4, 6)
        rows = 2
    }

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int,
    ): Component {
        text = toPreview((value as? String).orEmpty())

        if (isSelected) {
            background = table.selectionBackground
            foreground = table.selectionForeground
        } else {
            background = table.background
            foreground = JBColor.foreground()
        }

        return this
    }

    private fun toPreview(raw: String): String {
        if (raw.isBlank()) {
            return ""
        }
        val lines = raw.lines().take(2)
        val preview = lines.joinToString("\n")
        return if (raw.lines().size > 2) "$preview\n..." else preview
    }
}
