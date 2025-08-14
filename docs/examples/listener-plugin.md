# 示例：监听器插件

## 概述
- 类：CubeConfigUpdateListener（继承 SelectiveMosaicEventListener）
- 监听事件：
  - CubeConfigUpdateEvent
  - CubeDefinitionRegisteredEvent

## 行为
- 收到配置更新时，访问事件中的 MosaicCube 并进行相应处理（例如切换标志位），同时打印 before/after；
- 定义注册事件时进行记录或其他逻辑。