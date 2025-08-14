# 配置 API

## CubeConfig
```java
public class CubeConfig {
    <T> T getConfig(String name, Class<T> type);
    <T> T getConfig(String name, Class<T> type, T defaultValue);
    Map<String, Object> getAllConfigs();
}
```

## 读取示例
```java
CubeConfig cfg = getCubeConfig();
String model = cfg.getConfig("model", String.class, "gpt-3.5-turbo");
Integer timeout = cfg.getConfig("timeout", Integer.class, 30);
Boolean enableLogging = cfg.getConfig("enableLogging", Boolean.class, true);
```

说明：
- CubeConfig 由框架在实例化时注入（线程上下文绑定 cubeId -> 配置）。
- 不建议在插件中长期持有可变引用，按需读取或复制。