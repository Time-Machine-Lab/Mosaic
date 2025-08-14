# 事件系统

Mosaic 提供事件广播与监听机制，便于在配置变更、定义注册等时扩展行为。

## 监听器编写
继承 SelectiveMosaicEventListener，并指定关心的事件类型列表与处理逻辑：
```java
import io.github.tml.mosaic.core.event.event.CubeConfigUpdateEvent;
import io.github.tml.mosaic.core.event.event.CubeDefinitionRegisteredEvent;
import io.github.tml.mosaic.core.event.event.MosaicEvent;
import io.github.tml.mosaic.core.event.listener.SelectiveMosaicEventListener;

import java.util.Arrays;
import java.util.List;

public class CubeConfigUpdateListener extends SelectiveMosaicEventListener {
    @Override
    protected List<Class<? extends MosaicEvent>> getListenedEventTypes() {
        return Arrays.asList(
            CubeConfigUpdateEvent.class,
            CubeDefinitionRegisteredEvent.class
        );
    }

    @Override
    protected void onSelectiveEvent(MosaicEvent event) {
        when(event, CubeConfigUpdateEvent.class, e -> {
            // 处理配置更新
        }).orWhen(CubeDefinitionRegisteredEvent.class, e -> {
            // 处理定义注册
        }).orDefault();
    }

    @Override
    public String getListenerName() {
        return "CubeConfigUpdateListener";
    }
}
```

## AngelCube 与监听器
- 对于 AngelCube 类型的方块，init 成功后会自动将其监听器注册到事件广播器。