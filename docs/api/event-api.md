# 事件 API

## 监听器编写
- 继承 SelectiveMosaicEventListener，覆盖 getListenedEventTypes() 指定关心事件；
- 在 onSelectiveEvent 中用 when(...).orWhen(...).orDefault() 分派处理；
- 通过 getListenerName() 返回监听器名称。

常见事件（示例）：
- CubeConfigUpdateEvent：Cube 配置变更
- CubeDefinitionRegisteredEvent：Cube 定义注册完成

提示：
- AngelCube 的监听器会在 init 成功后自动注册到广播器。