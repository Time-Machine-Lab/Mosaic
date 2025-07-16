# Mosaic 📟
<a id="section1"></a>
<div align="center">

<img src="https://github.com/user-attachments/assets/b425421b-d3dd-4b6c-b1a7-a944ff63c3e9" alt="Mosaic Logo" width="150" />

<h1>🧩 Mosaic 插件框架</h1>

<p><strong><em>让插件像 Minecraft 方块一样无缝融入你的项目世界</em></strong></p>

*轻量化 · 热插拔 · 可视化 · 智能化*

<a href="https://Geniusay.github.io/ChopperBot-Doc/">

<img src="https://img.shields.io/badge/文档-简体中文-blue.svg?style=for-the-badge" alt="简体中文文档" />

</a>

<a href="https://github.com/Geniusay/ChopperBot/blob/master/CHANGELOG.md">

<img src="https://img.shields.io/badge/ChangeLog-English-blue.svg?style=for-the-badge" alt="Update Log" />

</a>

<a href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">

<img src="https://img.shields.io/badge/JDK-11+-green.svg?style=for-the-badge" />

</a>

<a href='https://github.com/Geniusay/ChopperBot'>

<img src="https://img.shields.io/github/stars/Geniusay/ChopperBot.svg?style=for-the-badge" alt="github stars"/>

</a>

<a href="">

<img src="https://img.shields.io/badge/Status-Developing-yellow?style=for-the-badge" alt="development status"/>

</a>

<p align='center'>

<b>English</b> | <a href="README.zh-CN.md">简体中文</a>

</p>

</div>

---

## 👻 什么是 Mosaic？
>**Mosaic** 是一个面向企业级应用的现代化Java插件框架，它重新定义了插件系统的设计理念。就像Minecraft的方块系统一样，每个插件都是一个独立的"方块"，可以灵活地插入到应用的任意"世界"中。
### 🎯 核心理念

```
🔌 即插即用 ⚡ 热插拔 📊 可视化 🚀 零配置
```

>Mosaic 打破了传统插件框架的复杂性壁垒，让开发者能够像搭积木一样构建应用功能。无论你是希望扩展现有系统功能，还是构建模块化的微服务架构，Mosaic 都能为你提供最优雅的解决方案。

---

# 🎥 让我们开一局？
## 控制台
![screenshot-17517748496291](https://github.com/user-attachments/assets/3e50b2c6-7f0d-4728-9778-f14fffaff8a8)
## 创建世界
![屏幕截图_6-7-2025_12519_localhost1](https://github.com/user-attachments/assets/b4fecd68-38f3-4ca4-aa6b-2b3b5a38968b)
## 热更新
![QQ20250706-125422](https://github.com/user-attachments/assets/84e62a81-96fd-4e90-9d47-58598dfb6b9a)
## 插件管理
![QQ20250706-144833](https://github.com/user-attachments/assets/8399d97a-bc8c-49ec-acf1-c8057989333f)
## 扩展包管理
![QQ20250706-145035](https://github.com/user-attachments/assets/4e1dcee2-93e8-4f56-ad30-814436cca5ef)
## 配置管理
![QQ20250706-144921](https://github.com/user-attachments/assets/2348ffeb-85a2-4625-a696-c6e31c5195f6)

---

## 🔱 核心特性

<table>
<tr>
<td width="50%" valign="top">

### 🚀 极简开发体验
- **一行代码启动**: Maven依赖即插即用
- **模板化开发**: 提供完整插件开发模板
- **智能代码生成**: 自动生成样板代码
- **开发热重载**: 开发期间实时预览效果

### 🔄 强大的插件管理
- **多源加载**: 支持多种多样的加载方式
- **版本控制**: 完整的插件版本管理体系
- **依赖解析**: 智能处理插件间依赖关系
- **冲突检测**: 自动检测并解决插件冲突

</td>
<td width="50%" valign="top">

### 🔌 真正的热插拔
- **零停机部署**: 无需重启应用即可更新插件
- **动态切换**: 实时启用/禁用插件功能
- **回滚机制**: 支持插件版本快速回滚
- **状态保持**: 插件切换时保持业务状态

### 👁️ 全方位可视化
- **零前端开发**: 自动生成管理界面
- **实时监控**: 插件运行状态实时展示
- **参数配置**: 可视化的插件参数调整
- **性能分析**: 插件性能指标可视化

</td>
</tr>
</table>

---

## 🚀 快速开始

### 📦 引入框架

只需一个依赖，即可拥有完整的插件生态：

```xml
<!-- 全功能包（一行依赖搞定所有） -->
<dependency>
    <groupId>io.github.tml</groupId>
    <artifactId>mosaic-starter</artifactId>
    <version>0.0.5-beta</version>
</dependency>
```

---

## 🕹️ 使用场景

<table>
<tr>
<td align="center" width="25%">
<img src="https://img.icons8.com/fluency/96/puzzle.png" alt="模块化开发"/>
<h4>🧩 模块化开发</h4>
<p>将复杂业务拆分为独立插件，提高代码复用性和维护性</p>
</td>
<td align="center" width="25%">
<img src="https://img.icons8.com/fluency/96/api-settings.png" alt="微服务扩展"/>
<h4>⚙️ 微服务扩展</h4>
<p>为微服务架构提供动态功能扩展能力，实现真正的服务治理</p>
</td>
<td align="center" width="25%">
<img src="https://img.icons8.com/fluency/96/cloud-development.png" alt="SaaS平台"/>
<h4>☁️ SaaS 平台</h4>
<p>构建多租户SaaS平台，为不同客户提供定制化插件服务</p>
</td>
<td align="center" width="25%">
<img src="https://img.icons8.com/fluency/96/workflow.png" alt="工作流引擎"/>
<h4>🔄 工作流引擎</h4>
<p>构建灵活的工作流系统，支持动态节点和处理逻辑</p>
</td>
</tr>
</table>

---

## 🔋 功能对比

| 特性 | Mosaic | 传统插件框架 | OSGi | Spring Plugin |
|------|--------|-------------|------|---------------|
| 🚀 上手难度 | ⭐ 极简 | ⭐⭐⭐ 困难 | ⭐⭐⭐⭐ 复杂 | ⭐⭐ 中等 |
| 🔌 热插拔 | ✅ 完全支持 | ❌ 需重启 | ✅ 支持 | ❌ 需重启 |
| 👁️ 可视化 | ✅ 零配置 | ❌ 需开发 | ❌ 需开发 | ❌ 需开发 |
| 📦 打包方式 | ✅ 多样化 | ⭐⭐ JAR only | ⭐⭐ Bundle | ⭐⭐ JAR only |
| 🔧 配置管理 | ✅ 智能化 | ⭐⭐ 手动 | ⭐⭐ 手动 | ⭐⭐⭐ 注解 |

---

## 🍖 参与贡献

我们欢迎所有形式的贡献！无论是：

- 🐛 **Bug 修复**
- ✨ **新功能开发**
- 📝 **文档完善**
- 🔌 **插件贡献**
- 💡 **想法建议**

### 贡献步骤

1. 🍴 Fork 项目
2. 🌿 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 💾 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 📤 推送分支 (`git push origin feature/AmazingFeature`)
5. 🔃 创建 Pull Request

---

## ⚖️ 开源协议

本项目采用 [Apache License 2.0](LICENSE) 开源协议。

---

## 🙏 致谢

感谢所有为 Mosaic 项目贡献代码、文档、想法的开发者们！

<div align="center">

**如果觉得 Mosaic 对你有帮助，请给我们一个 ⭐ Star！**

[⬆ 回到顶部](#section1)

</div>
