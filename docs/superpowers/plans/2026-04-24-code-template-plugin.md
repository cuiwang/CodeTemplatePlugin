# Code Template Plugin Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 JetBrains IDE 中提供右键创建/插入代码模板、快捷键快速插入、以及可导入导出的模板配置页。

**Architecture:** 使用 IntelliJ Platform `ActionSystem` 构建右键菜单与快捷动作；使用 `PersistentStateComponent` 保存模板和快捷键；设置页采用 Swing 表格作为模板输入源并提供 JSON 导入导出。插入逻辑统一走写命令，保证在编辑器线程安全写入。

**Tech Stack:** Kotlin, IntelliJ Platform SDK, Swing UI, Gson JSON

---

### Task 1: 模板存储与公共工具

**Files:**
- Create: `src/main/kotlin/com/cuiwang/codetemplate/model/CodeTemplateEntry.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/state/CodeTemplateSettingsService.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/core/TemplateInsertUtil.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/core/TemplatePreviewUtil.kt`

- [ ] 定义模板数据结构与持久化状态
- [ ] 实现模板读取/保存/新增接口
- [ ] 实现插入工具与预览截断工具

### Task 2: 右键菜单与快捷动作

**Files:**
- Create: `src/main/kotlin/com/cuiwang/codetemplate/actions/CreateTemplateFromSelectionAction.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/actions/InsertTemplatesActionGroup.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/actions/InsertTemplateAction.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/actions/OpenInsertPopupAction.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/ui/InsertTemplatePopup.kt`

- [ ] Create 动作从选区创建模板
- [ ] Insert 动态三级菜单生成
- [ ] Ctrl+I 弹出可滚动选择列表，支持数字键/方向键/回车

### Task 3: 设置页与 JSON 导入导出

**Files:**
- Create: `src/main/kotlin/com/cuiwang/codetemplate/settings/CodeTemplateConfigurable.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/settings/TemplateTableModel.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/settings/ShortcutCaptureField.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/settings/ShortcutManager.kt`
- Create: `src/main/kotlin/com/cuiwang/codetemplate/startup/CodeTemplateStartupActivity.kt`

- [ ] 构建 Other Settings > Code Template 配置界面
- [ ] 支持新增/删除/上移/下移与失焦自动保存
- [ ] 支持 JSON 导入导出与快捷键保存应用

### Task 4: 插件清单与资源

**Files:**
- Modify: `src/main/resources/META-INF/plugin.xml`
- Create: `src/main/resources/icons/codeTemplateMenu.svg`

- [ ] 注册 actions/configurable/startup
- [ ] 配置右键菜单位置、默认快捷键、图标
- [ ] 更新作者信息与插件描述

### Task 5: 构建验证

**Files:**
- Modify: （无代码改动，执行验证）

- [ ] 运行 `./gradlew build`
- [ ] 修复编译问题并再次验证
- [ ] 输出最终交付说明
