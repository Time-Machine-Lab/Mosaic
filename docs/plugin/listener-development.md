# 开发监听器

## 模板
```java
import io.github.tml.mosaic.core.event.event.MosaicEvent;
import io.github.tml.mosaic.core.event.listener.SelectiveMosaicEventListener;

import java.util.Collections;
import java.util.List;

public class MyListener extends SelectiveMosaicEventListener {
    @Override
    protected List<Class<? extends MosaicEvent>> getListenedEventTypes() {
        return Collections.singletonList(/* 某事件类 */);
    }
    @Override
    protected void onSelectiveEvent(MosaicEvent event) {
        when(event, /* 某事件类 */ , e -> {
            // handle
        }).orDefault();
    }
    @Override
    public String getListenerName() {
        return "MyListener";
    }
}
```

提示：
- 对 AngelCube，监听器注册会在 init 时自动完成。