package com.cuiwang.codetemplate.settings

import com.cuiwang.codetemplate.model.CodeTemplateEntry
import javax.swing.table.AbstractTableModel

class TemplateTableModel : AbstractTableModel() {
    private val rows = mutableListOf<CodeTemplateEntry>()

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = 4

    override fun getColumnName(column: Int): String {
        return when (column) {
            0 -> "序号"
            1 -> "代码名称"
            2 -> "代码内容"
            3 -> "是否启用"
            else -> ""
        }
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = columnIndex != 0

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val row = rows[rowIndex]
        return when (columnIndex) {
            0 -> rowIndex + 1
            1 -> row.name
            2 -> row.content
            3 -> if (row.enabled) ENABLED_TEXT else DISABLED_TEXT
            else -> ""
        }
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        val row = rows[rowIndex]
        when (columnIndex) {
            1 -> row.name = (aValue as? String).orEmpty()
            2 -> row.content = (aValue as? String).orEmpty()
            3 -> {
                row.enabled = when (aValue) {
                    is Boolean -> aValue
                    ENABLED_TEXT -> true
                    DISABLED_TEXT -> false
                    else -> true
                }
            }
        }

        if (columnIndex == 1 || columnIndex == 2 || columnIndex == 3) {
            fireTableRowsUpdated(rowIndex, rowIndex)
        }
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return when (columnIndex) {
            0 -> Int::class.java
            else -> String::class.java
        }
    }

    fun addRow(entry: CodeTemplateEntry = CodeTemplateEntry(enabled = true)): Int {
        rows.add(entry)
        val index = rows.lastIndex
        fireTableRowsInserted(index, index)
        return index
    }

    fun removeRow(index: Int) {
        if (index !in rows.indices) {
            return
        }
        rows.removeAt(index)
        fireTableDataChanged()
    }

    fun moveUp(index: Int): Int {
        if (index <= 0 || index >= rows.size) {
            return index
        }
        val row = rows.removeAt(index)
        rows.add(index - 1, row)
        fireTableDataChanged()
        return index - 1
    }

    fun moveDown(index: Int): Int {
        if (index < 0 || index >= rows.lastIndex) {
            return index
        }
        val row = rows.removeAt(index)
        rows.add(index + 1, row)
        fireTableDataChanged()
        return index + 1
    }

    fun setRows(entries: List<CodeTemplateEntry>) {
        rows.clear()
        rows.addAll(entries.map { it.copy() })
        fireTableDataChanged()
    }

    fun getRowsCopy(): List<CodeTemplateEntry> = rows.map { it.copy() }

    companion object {
        const val ENABLED_TEXT = "启用"
        const val DISABLED_TEXT = "禁用"
    }
}
