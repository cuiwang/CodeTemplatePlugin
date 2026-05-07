# Code Template 插件

Code Template 是一个用于 JetBrains 平台（IntelliJ IDEA、PyCharm 等）的轻量插件，帮助你快速保存、管理并插入可重用的代码片段。

作者：崔旺  
仓库：[https://github.com/cuiwang/CodeTemplatePlugin](https://github.com/cuiwang/CodeTemplatePlugin)

主要功能

- 在编辑器右键菜单中快速访问 `Code Template`（支持 Create / Insert 等操作）
- 快捷键快速插入模板（可自定义）
- 插件设置页：Settings > Other Settings > Code Template，用于管理模板列表、导入/导出 JSON 等

快速构建与打包

- 本地构建：

  ```bash
  ./gradlew build
  ```

- 打包插件：

  ```bash
  ./gradlew buildPlugin
  ```

- 生成的插件包位于：`build/distributions/`

发布到 JetBrains Marketplace

- 请参阅仓库中的 `MARKETPLACE_UPLOAD.md`（若已删除，请使用 JetBrains 官方文档填写 Marketplace 上传信息）。
- 发布变更记录请查看 `CHANGELOG.md`。

联系方式

- GitHub: [https://github.com/cuiwang](https://github.com/cuiwang)

<!-- 仅保留发布所需的最小信息，详尽开发文档请放在 docs/ 或仓库的其他开发分支 -->
