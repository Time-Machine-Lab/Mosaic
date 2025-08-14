# 快速开始

本文展示如何在宿主中注册 Slot、安装 Cube，并执行扩展点。

## 1. 注册槽位 Slot
```java
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.slot.Slot;
import io.github.tml.mosaic.slot.service.GenericSlotManager;
import io.github.tml.mosaic.slot.service.SlotManager;

SlotManager slotManager = GenericSlotManager.manager();

// 创建槽位并注册
Slot slot = new Slot(new DotNotationId("demo.slot"));
slotManager.registerSlot(slot);
```

## 2. 安装 Cube 到 Slot
```java
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.slot.Slot;

Slot.SetupCubeInfo info = new Slot.SetupCubeInfo();
info.setCubeId(new GUID("your.cube.id"));          // 目标 Cube ID
info.setExPackageId(new GUID("your.ex.package"));  // 扩展包 ID（AngelCube 可为空）
info.setExPointId(new GUID("your.ex.point"));      // 扩展点 ID（AngelCube 可为空）
info.setResName("default");                        // 结果命名（需在扩展点返回结构中存在）
info.setConfigId("default");                       // 配置 ID（同一 Cube 可按配置隔离）

slotManager.setup(slot.getId(), info);
```

## 3. 执行调用
```java
import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.service.SlotManager;

// 初始化上下文与执行代理
CubeContext context = new ClassPathCubeContext();
CubeActuatorProxy proxy = new CubeActuatorProxy();
proxy.init(context, slotManager);

// 执行（同步，按扩展点签名传参）
Object result = proxy.execute(slot.getId(), "arg1", 123);
```

提示：
- 对于 AngelCube（守护型），execute 会启动/复用守护任务，停止请调用 proxy.stop(slotId)。
- args 需要与扩展点的方法签名参数一致。