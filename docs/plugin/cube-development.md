# 开发 Cube

## 1. 普通 Cube
```java
import io.github.tml.mosaic.cube.external.MCube;
import io.github.tml.mosaic.cube.external.MosaicCube;

@MCube(name="示例Cube", description="演示用", version="1.0.0", scope="singleton")
public class ExampleCube extends MosaicCube {
    @Override
    public boolean init() {
        // 读取配置
        String v = getCubeConfig().getConfig("k", String.class, "default");
        return true;
    }
    @Override
    public boolean destroy() { return true; }
    @Override
    public String cubeId() { return "example.cube"; }
}
```

## 2. 守护型 Cube（AngelCube）
```java
import io.github.tml.mosaic.cube.external.AngelCube;
import io.github.tml.mosaic.cube.external.MCube;

@MCube(name="守护Cube", description="长驻任务", version="1.0.0")
public class DaemonCube extends AngelCube {
    @Override public boolean init() { return true; }
    @Override public String cubeId() { return "daemon.cube"; }
    @Override public void start() { /* 长驻逻辑 */ }
    @Override public void stop() { /* 关闭逻辑 */ }
}
```

说明：
- AngelCube 的执行由 AngelCubeActuator 管理，支持 start/stop、并发保护与超时清理。