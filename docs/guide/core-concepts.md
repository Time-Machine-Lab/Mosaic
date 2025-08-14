# 核心概念

## Cube（方块）
- 插件的最小可运行单元，需继承 MosaicCube（或 AngelCube）。
- 由 @MCube 注解描述元数据（name/version/description/scope）。
- 生命周期：init() -> destroy()；可通过 CubeApi.getCubeConfig() 获取配置。

## ExtensionPackage（扩展包）
- 承载成组扩展点的容器，通常继承 MosaicExtPackage\<T extends MosaicCube\>。
- 初始化时会自动注入所属的 MosaicCube 实例。

## ExtensionPoint（扩展点）
- 扩展包中对外暴露的可执行方法，使用 @MExtension 标注（含 extPointId/name/description/priority 等）。
- 返回值可为：
  - 普通类型：框架会封装至 MosaicResult 并以默认名 default 暴露；
  - MosaicResult：原样返回，并可从中根据命名项解析；
  - void/MosaicVoid：表示无返回。

## Slot（槽位）
- 宿主中的“安装点”，通过 Slot.SetupCubeInfo 指定 cubeId / exPackageId / exPointId / resName / configId。
- 与 SlotManager 一起管理安装/卸载（setup/unSetup）。

## 执行器（Actuator）
- 根据上下文（是否 AngelCube）选择不同执行器：
  - 通用执行器（GenericCubeActuator）：反射调用扩展点方法，封装结果；
  - 守护型执行器（AngelCubeActuator）：管理 start/stop 及并发与超时清理；
  - 异步执行器（AsyncCubeActuator）：预留实现。

## Context/Factory/Definition
- 负责加载配置与定义（CubeDefinition/ExtensionPackageDefinition/ExtensionPointDefinition），构建可运行对象。