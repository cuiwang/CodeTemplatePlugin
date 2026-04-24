# Code Template

Code Template 是一个 JetBrains IDE 插件，用于快速创建、管理并插入可复用代码块。

- 作者：崔 旺
- 主页：https://github.com/cuiwang
- 插件仓库：https://github.com/cuiwang/CodeTemplatePlugin

## 功能特性

- 编辑器右键菜单 `Code Template`
  - `Create`：将当前选中代码保存为模板
  - `Insert`：从模板列表插入代码到当前光标位置
- 快捷键快速插入
  - 默认 `Ctrl+I`（macOS 默认 `Cmd+I`）
  - 支持数字键快速选择、方向键选择、回车插入
- 设置页管理模板
  - 路径：`Settings > Other Settings > Code Template`
  - 支持新增/删除/上移/下移
  - 支持启用状态开关
  - 支持 JSON 导入/导出

## 使用方式

1. 在编辑器中选中代码。
2. 右键选择 `Code Template > Create`，输入模板名称并保存。
3. 在需要插入处使用：
   - 右键 `Code Template > Insert`，或
   - 快捷键呼出 `Insert Code Template`。

## 构建

```bash
./gradlew build
```

## 打包插件

```bash
./gradlew buildPlugin
```

生成的插件包位于：

- `build/distributions/`

## 发布到 JetBrains Marketplace

请查看 [MARKETPLACE_UPLOAD.md](./MARKETPLACE_UPLOAD.md)。
