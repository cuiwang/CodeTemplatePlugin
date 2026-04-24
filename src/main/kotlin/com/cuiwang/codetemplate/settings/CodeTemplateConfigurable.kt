package com.cuiwang.codetemplate.settings

import com.cuiwang.codetemplate.model.CodeTemplateEntry
import com.cuiwang.codetemplate.state.CodeTemplateSettingsService
import com.cuiwang.codetemplate.state.CodeTemplateSettingsState
import com.google.gson.GsonBuilder
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.io.File
import javax.swing.DefaultCellEditor
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.ListSelectionModel

class CodeTemplateConfigurable : SearchableConfigurable {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private var mainPanel: JPanel? = null
    private var shortcutField: ShortcutCaptureField? = null
    private var tableModel: TemplateTableModel? = null
    private var table: JBTable? = null
    private var suppressAutoSave = false

    override fun getId(): String = "com.cuiwang.codetemplate.settings"

    override fun getDisplayName(): String = "Code Template"

    override fun createComponent(): JPanel {
        if (mainPanel != null) {
            return mainPanel!!
        }

        val hintText = JBTextArea(
            "使用说明：在编辑器右键通过 Code Template 的 Create/Insert 管理与插入代码块，" +
                "也可使用快捷键快速呼出插入窗口。",
        ).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            isFocusable = false
            isOpaque = false
            border = null
            columns = 42
        }

        val detailLink = ActionLink("查看详情") {
            BrowserUtil.browse("https://github.com/cuiwang/CodeTemplatePlugin")
        }

        val hintPanel = JPanel(BorderLayout(0, 6)).apply {
            isOpaque = false
            add(hintText, BorderLayout.CENTER)
            add(detailLink, BorderLayout.SOUTH)
        }

        val importButton = JButton("导入")
        val exportButton = JButton("导出")

        importButton.addActionListener { importFromJson() }
        exportButton.addActionListener { exportToJson() }

        val topPanel = JPanel(BorderLayout())
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 8, 0)).apply {
            add(importButton)
            add(exportButton)
        }
        topPanel.add(hintPanel, BorderLayout.CENTER)
        topPanel.add(buttonPanel, BorderLayout.EAST)

        shortcutField = ShortcutCaptureField()
        val shortcutPanel = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply {
            add(JLabel("快捷键（快速呼出代码块插入菜单）："))
            add(shortcutField)
        }

        tableModel = TemplateTableModel()
        table = JBTable(tableModel).apply {
            setShowGrid(true)
            rowHeight = 48
            autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
            cellSelectionEnabled = true
            setSurrendersFocusOnKeystroke(true)
            putClientProperty("terminateEditOnFocusLost", true)
        }

        val enabledComboBox = JComboBox(arrayOf(TemplateTableModel.ENABLED_TEXT, TemplateTableModel.DISABLED_TEXT))
        val nameEditor = DefaultCellEditor(JTextField()).apply {
            clickCountToStart = 1
        }
        val enabledEditor = DefaultCellEditor(enabledComboBox).apply {
            clickCountToStart = 1
        }
        table!!.columnModel.getColumn(0).preferredWidth = 60

        table!!.columnModel.getColumn(1).minWidth = 80
        table!!.columnModel.getColumn(1).preferredWidth = 80
        table!!.columnModel.getColumn(1).cellEditor = nameEditor

        table!!.columnModel.getColumn(2).minWidth = 200
        table!!.columnModel.getColumn(2).preferredWidth = 200
        table!!.columnModel.getColumn(2).cellRenderer = TemplateContentCellRenderer()
        table!!.columnModel.getColumn(2).cellEditor = TemplateContentCellEditor()

        table!!.columnModel.getColumn(3).preferredWidth = 90
        table!!.columnModel.getColumn(3).maxWidth = 100
        table!!.columnModel.getColumn(3).cellEditor = enabledEditor

        tableModel!!.addTableModelListener {
            if (!suppressAutoSave) {
                saveTemplatesOnly()
            }
        }

        val addButton = JButton("新增")
        val removeButton = JButton("删除")
        val moveUpButton = JButton("上移")
        val moveDownButton = JButton("下移")

        addButton.addActionListener {
            stopEditing()
            val index = tableModel!!.addRow(CodeTemplateEntry(name = "", content = "", enabled = true))
            table!!.setRowSelectionInterval(index, index)
            table!!.requestFocusInWindow()
        }

        removeButton.addActionListener {
            stopEditing()
            val selected = table!!.selectedRow
            if (selected >= 0) {
                tableModel!!.removeRow(selected)
            }
        }

        moveUpButton.addActionListener {
            stopEditing()
            val selected = table!!.selectedRow
            if (selected >= 0) {
                val newIndex = tableModel!!.moveUp(selected)
                table!!.setRowSelectionInterval(newIndex, newIndex)
            }
        }

        moveDownButton.addActionListener {
            stopEditing()
            val selected = table!!.selectedRow
            if (selected >= 0) {
                val newIndex = tableModel!!.moveDown(selected)
                table!!.setRowSelectionInterval(newIndex, newIndex)
            }
        }

        val operationPanel = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply {
            add(addButton)
            add(removeButton)
            add(moveUpButton)
            add(moveDownButton)
        }

        val contentHint = JLabel("代码内容支持多行输入，按 Ctrl+Enter（Mac: Cmd+Enter）结束编辑。")

        val tablePanel = JPanel(BorderLayout(0, 8))
        tablePanel.add(operationPanel, BorderLayout.NORTH)
        tablePanel.add(JBScrollPane(table), BorderLayout.CENTER)
        tablePanel.add(contentHint, BorderLayout.SOUTH)

        val centerPanel = JPanel(BorderLayout(0, 10))
        centerPanel.add(shortcutPanel, BorderLayout.NORTH)
        centerPanel.add(tablePanel, BorderLayout.CENTER)

        mainPanel = JPanel(BorderLayout(0, 12)).apply {
            add(topPanel, BorderLayout.NORTH)
            add(centerPanel, BorderLayout.CENTER)
        }

        shortcutField!!.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent) {
                saveShortcutOnly()
            }
        })

        reset()
        return mainPanel!!
    }

    override fun isModified(): Boolean {
        if (table?.isEditing == true) {
            return true
        }
        val service = CodeTemplateSettingsService.getInstance()
        val currentTemplates = tableModel?.getRowsCopy().orEmpty()
        val currentShortcut = shortcutField?.getShortcut().orEmpty()
        val normalizedShortcut = ShortcutManager.normalizeShortcut(currentShortcut)
            ?: CodeTemplateSettingsState.defaultShortcut()

        return currentTemplates != service.getTemplates() ||
            normalizedShortcut != service.getQuickInsertShortcut()
    }

    override fun apply() {
        saveNow()
    }

    override fun reset() {
        suppressAutoSave = true
        val service = CodeTemplateSettingsService.getInstance()
        shortcutField?.setShortcut(service.getQuickInsertShortcut())
        tableModel?.setRows(service.getTemplates())
        suppressAutoSave = false
    }

    override fun disposeUIResources() {
        mainPanel = null
        shortcutField = null
        tableModel = null
        table = null
    }

    private fun saveNow(stopEditingFirst: Boolean = true) {
        if (suppressAutoSave || tableModel == null || shortcutField == null) {
            return
        }

        if (stopEditingFirst) {
            stopEditing()
        }
        saveTemplatesOnly()
        saveShortcutOnly()
    }

    private fun saveTemplatesOnly() {
        if (suppressAutoSave || tableModel == null) {
            return
        }
        val service = CodeTemplateSettingsService.getInstance()
        val templates = tableModel!!.getRowsCopy()
        service.setTemplates(templates)
    }

    private fun saveShortcutOnly() {
        if (suppressAutoSave || shortcutField == null) {
            return
        }
        val shortcut = ShortcutManager.normalizeShortcut(shortcutField!!.getShortcut())
            ?: CodeTemplateSettingsState.defaultShortcut()
        val service = CodeTemplateSettingsService.getInstance()
        service.setQuickInsertShortcut(shortcut)
        shortcutField!!.setShortcut(shortcut)
        ShortcutManager.applyShortcut(shortcut)
    }

    private fun stopEditing() {
        if (table?.isEditing == true) {
            table?.cellEditor?.stopCellEditing()
        }
    }

    private fun exportToJson() {
        stopEditing()
        val chooser = javax.swing.JFileChooser().apply {
            dialogTitle = "导出模板到 JSON"
            selectedFile = File("code-templates.json")
        }

        val result = chooser.showSaveDialog(mainPanel)
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) {
            return
        }

        val target = ensureJsonSuffix(chooser.selectedFile)
        val payload = ExportPayload(
            quickInsertShortcut = shortcutField?.getShortcut().orEmpty(),
            templates = tableModel?.getRowsCopy().orEmpty(),
        )

        runCatching {
            target.writeText(gson.toJson(payload), Charsets.UTF_8)
        }.onSuccess {
            Messages.showInfoMessage(mainPanel, "模板已导出到：${target.absolutePath}", "Code Template")
        }.onFailure {
            Messages.showErrorDialog(mainPanel, "导出失败：${it.message}", "Code Template")
        }
    }

    private fun importFromJson() {
        stopEditing()
        val chooser = javax.swing.JFileChooser().apply {
            dialogTitle = "从 JSON 导入模板"
        }

        val result = chooser.showOpenDialog(mainPanel)
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) {
            return
        }

        val source = chooser.selectedFile
        runCatching {
            gson.fromJson(source.readText(Charsets.UTF_8), ExportPayload::class.java)
        }.onSuccess { payload ->
            val importedTemplates = payload.templates.orEmpty().map { it.copy() }
            val importedShortcut = payload.quickInsertShortcut.orEmpty()
            tableModel?.setRows(importedTemplates)
            shortcutField?.setShortcut(importedShortcut)
            saveNow()
            Messages.showInfoMessage(mainPanel, "模板已成功导入。", "Code Template")
        }.onFailure {
            Messages.showErrorDialog(mainPanel, "导入失败：${it.message}", "Code Template")
        }
    }

    private fun ensureJsonSuffix(file: File): File {
        return if (file.name.endsWith(".json", ignoreCase = true)) file else File(file.parentFile, "${file.name}.json")
    }

    private data class ExportPayload(
        var quickInsertShortcut: String? = null,
        var templates: List<CodeTemplateEntry>? = null,
    )
}
