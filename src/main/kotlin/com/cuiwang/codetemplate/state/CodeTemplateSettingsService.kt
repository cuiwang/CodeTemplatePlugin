package com.cuiwang.codetemplate.state

import com.cuiwang.codetemplate.model.CodeTemplateEntry
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.util.SystemInfo

data class CodeTemplateSettingsState(
    var quickInsertShortcut: String = defaultShortcut(),
    var templates: MutableList<CodeTemplateEntry> = mutableListOf(),
) {
    companion object {
        fun defaultShortcut(): String = if (SystemInfo.isMac) "meta I" else "ctrl I"
    }
}

@Service(Service.Level.APP)
@State(
    name = "CodeTemplateSettings",
    storages = [Storage("CodeTemplateSettings.xml")],
)
class CodeTemplateSettingsService : PersistentStateComponent<CodeTemplateSettingsState> {
    private var state = CodeTemplateSettingsState()

    override fun getState(): CodeTemplateSettingsState = state

    override fun loadState(state: CodeTemplateSettingsState) {
        this.state = state
        if (this.state.quickInsertShortcut.isBlank()) {
            this.state.quickInsertShortcut = CodeTemplateSettingsState.defaultShortcut()
        }
        this.state.templates = this.state.templates.map { sanitizeTemplate(it) }.toMutableList()
    }

    fun getTemplates(): List<CodeTemplateEntry> = state.templates.map { it.copy() }

    fun getEnabledTemplates(): List<CodeTemplateEntry> =
        state.templates
            .filter { it.enabled && it.name.isNotBlank() && it.content.isNotBlank() }
            .map { it.copy() }

    fun addTemplate(template: CodeTemplateEntry) {
        val normalized = sanitizeTemplate(template)
        if (normalized.name.isBlank() || normalized.content.isBlank()) {
            return
        }
        state.templates.add(normalized)
    }

    fun setTemplates(templates: List<CodeTemplateEntry>) {
        state.templates = templates
            .map { sanitizeTemplate(it) }
            .toMutableList()
    }

    fun getQuickInsertShortcut(): String =
        state.quickInsertShortcut.ifBlank { CodeTemplateSettingsState.defaultShortcut() }

    fun setQuickInsertShortcut(shortcut: String) {
        state.quickInsertShortcut = shortcut.ifBlank { CodeTemplateSettingsState.defaultShortcut() }
    }

    private fun sanitizeTemplate(template: CodeTemplateEntry): CodeTemplateEntry {
        return CodeTemplateEntry(
            name = template.name.trim(),
            content = template.content,
            enabled = template.enabled,
        )
    }

    companion object {
        fun getInstance(): CodeTemplateSettingsService = service()
    }
}
