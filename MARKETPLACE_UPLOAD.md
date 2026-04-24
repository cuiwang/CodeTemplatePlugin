# 发布到 JetBrains Marketplace

本文档用于指导你将 `Code Template` 插件发布到 https://plugins.jetbrains.com/ 。

## 1. 准备账号与凭据

1. 登录 JetBrains Marketplace。
2. 创建/获取 `Marketplace Token`。
3. 生成插件签名所需凭据：
   - `Certificate Chain`
   - `Private Key`
   - `Private Key Password`

## 2. 配置凭据

两种方式任选其一。

### 方式 A：使用环境变量

```bash
export JETBRAINS_MARKETPLACE_TOKEN="..."
export PLUGIN_CERTIFICATE_CHAIN="..."
export PLUGIN_PRIVATE_KEY="..."
export PLUGIN_PRIVATE_KEY_PASSWORD="..."
```

### 方式 B：使用 Gradle 属性文件

```bash
cp marketplace.properties.example ~/.gradle/gradle.properties
# 然后把值填入
```

> 建议将凭据写到用户级 `~/.gradle/gradle.properties`，不要提交到仓库。

## 3. 构建与签名

```bash
./gradlew clean buildPlugin signPlugin
```

签名后的产物在 `build/distributions/`。

## 4. 发布

```bash
./gradlew publishPlugin
```

## 5. 手动网页上传（可选）

如果你选择在网页手动上传：

1. 先执行 `./gradlew buildPlugin` 生成 ZIP。
2. 进入 Marketplace 插件后台上传 ZIP。
3. 按页面提示完成版本发布。

## 6. 发布前检查建议

- `version` 已更新（不可重复）。
- `CHANGELOG.md` 已更新。
- 插件描述与截图已在 Marketplace 页面配置。
- 本地 `./gradlew build` 成功。
