---
home: true
heroImage: /logo.png
heroText: Mosaic Framework
tagline: 基于插件化架构的Java模块化框架
actionText: 快速开始 →
actionLink: /guide/getting-started
features:
- title: 🧩 插件化架构
  details: 基于Cube概念的模块化设计，支持插件的动态加载和管理
- title: 🔌 扩展点机制
  details: 通过注解驱动的扩展点系统，实现插件间的松耦合交互
- title: 📡 事件驱动
  details: 完整的事件系统支持插件间的异步通信
- title: ⚙️ 配置管理
  details: 灵活的配置系统，支持类型验证和默认值
- title: 🔄 生命周期管理
  details: 完整的插件生命周期管理（初始化、启动、停止、销毁）
- title: 🏗️ 多实例支持
  details: 支持单例和多例插件模式
footer: MIT Licensed | Copyright © 2024 Mosaic Framework
---

## 什么是 Mosaic Framework？

Mosaic Framework 是一个基于插件化架构的Java模块化框架，旨在帮助开发者构建可扩展、可维护的模块化应用系统。

### 核心特性

- **插件化架构**: 基于Cube概念的模块化设计
- **扩展点机制**: 注解驱动的扩展点系统
- **事件驱动**: 完整的事件系统
- **配置管理**: 灵活的配置系统
- **生命周期管理**: 完整的插件生命周期

### 快速体验

```xml
<dependency>
    <groupId>io.github.timemachinelab</groupId>
    <artifactId>mosaic-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

```java
@MCube(name = "示例插件", description = "这是一个示例插件")
public class ExampleCube extends MosaicCube {
    @Override
    public boolean init() {
        System.out.println("插件初始化成功！");
        return true;
    }
    
    @Override
    public String cubeId() {
        return "example.cube";
    }
}
```