package com.cuiwang.codetemplate.ui

import com.cuiwang.codetemplate.core.TemplateInsertUtil
import com.cuiwang.codetemplate.core.TemplatePreviewUtil
import com.cuiwang.codetemplate.model.CodeTemplateEntry
import com.cuiwang.codetemplate.state.CodeTemplateSettingsService
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer
import javax.swing.ListSelectionModel
import javax.swing.ScrollPaneConstants

object InsertTemplatePopup {
    fun show(project: Project, editor: Editor) {
        val templates = CodeTemplateSettingsService.getInstance().getEnabledTemplates()
        if (templates.isEmpty()) {
            Messages.showInfoMessage(project, "暂无启用模板，请先在 Code Template 配置中新增模板。", "Code Template")
            return
        }

        val items = templates.mapIndexed { index, template -> PopupItem(index + 1, template) }
        val model = DefaultListModel<PopupItem>()
        items.forEach(model::addElement)

        val list = JBList(model).apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            selectedIndex = 0
            fixedCellHeight = 78
            border = JBUI.Borders.empty()
            cellRenderer = TemplateCellRenderer()
        }

        val tips = JBLabel("↑/↓ 选择  Enter 插入  数字键快速插入")
        tips.font = JBFont.small()

        val scrollPane = JBScrollPane(list).apply {
            border = JBUI.Borders.empty()
            viewportBorder = JBUI.Borders.empty()
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            preferredSize = Dimension(620, 320)
        }

        val contentPanel = JPanel(BorderLayout(0, 8)).apply {
            border = JBUI.Borders.empty(8)
            add(tips, BorderLayout.NORTH)
            add(scrollPane, BorderLayout.CENTER)
        }

        lateinit var popup: JBPopup

        val insertItem: (PopupItem) -> Unit = { item ->
            TemplateInsertUtil.insertAtCaret(project, editor, item.template.content)
            popup.cancel()
        }

        fun insertSelected() {
            val selected = list.selectedValue ?: return
            insertItem(selected)
        }

        list.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    insertSelected()
                }
            }
        })

        list.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    e.consume()
                    insertSelected()
                    return
                }

                val number = toQuickInsertNumber(e) ?: return
                if (number <= items.size) {
                    e.consume()
                    insertItem(items[number - 1])
                }
            }
        })

        popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(contentPanel, list)
            .setProject(project)
            .setTitle("Insert Code Template")
            .setMovable(true)
            .setResizable(true)
            .setRequestFocus(true)
            .setCancelOnClickOutside(true)
            .setCancelOnWindowDeactivation(true)
            .createPopup()

        popup.showInBestPositionFor(editor)
        list.requestFocusInWindow()
    }

    private fun toQuickInsertNumber(e: KeyEvent): Int? {
        return when (e.keyCode) {
            in KeyEvent.VK_1..KeyEvent.VK_9 -> e.keyCode - KeyEvent.VK_0
            KeyEvent.VK_0 -> 10
            in KeyEvent.VK_NUMPAD1..KeyEvent.VK_NUMPAD9 -> e.keyCode - KeyEvent.VK_NUMPAD0
            KeyEvent.VK_NUMPAD0 -> 10
            else -> null
        }
    }

    private data class PopupItem(
        val order: Int,
        val template: CodeTemplateEntry,
    )

    private class TemplateCellRenderer : ListCellRenderer<PopupItem> {
        override fun getListCellRendererComponent(
            list: JList<out PopupItem>,
            value: PopupItem,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean,
        ): Component {
            val title = JBLabel("${value.order}. ${value.template.name}").apply {
                font = JBFont.label().deriveFont(Font.BOLD)
            }

            val preview = JBTextArea(TemplatePreviewUtil.toMultilinePreview(value.template.content)).apply {
                isEditable = false
                isFocusable = false
                lineWrap = true
                wrapStyleWord = true
                border = JBUI.Borders.empty()
                columns = 64
                rows = 3
                font = JBFont.small()
                background = JBColor.PanelBackground
            }

            val panel = JPanel(BorderLayout(0, 4)).apply {
                border = JBUI.Borders.empty(6, 8)
                add(title, BorderLayout.NORTH)
                add(preview, BorderLayout.CENTER)
                isOpaque = true
            }

            if (isSelected) {
                panel.background = list.selectionBackground
                title.foreground = list.selectionForeground
                preview.foreground = list.selectionForeground
                preview.background = list.selectionBackground
            } else {
                panel.background = list.background
                title.foreground = list.foreground
                preview.foreground = JBColor.GRAY
                preview.background = list.background
            }

            return panel
        }
    }
}
