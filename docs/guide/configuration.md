# 配置

## 读取配置
插件可通过 CubeApi.getCubeConfig() 获取配置：
```java
import io.github.tml.mosaic.cube.CubeConfig;
import io.github.tml.mosaic.cube.external.MosaicCube;

public class MyCube extends MosaicCube {
    @Override
    public boolean init() {
        CubeConfig cfg = getCubeConfig();
        String model = cfg.getConfig("model", String.class, "gpt-3.5-turbo");
        Integer timeout = cfg.getConfig("timeout", Integer.class, 30);
        return true;
    }
}
```

CubeConfig 支持：
- getConfig(name, type)
- getConfig(name, type, defaultValue)
- getAllConfigs()

## scope 与实例化
- scope=singleton：已存在实例时复用；
- scope=property：存在实例时先 destroy 再重建，适合多实例/按配置隔离。

## configId
- Slot.SetupCubeInfo.configId 用于按配置隔离检索 Cube 实例（同一 cubeId 可有多份实例，每份绑定不同配置）。