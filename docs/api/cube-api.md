# Cube API

## 接口
```java
public interface CubeApi {
    boolean init();
    boolean destroy();
    String cubeId();
    default CubeConfig getCubeConfig() { ... }
}
```

## 基类
- MosaicCube：插件实现通常继承此类，覆盖 init/destroy/cubeId。
- AngelCube：守护型方块，需实现 start()/stop()，由 AngelCubeActuator 管理其生命周期。

## 注解
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MCube {
    String name() default "";
    String version() default "1.0.0";
    String description() default "";
    String scope() default "singleton";
}
```

## 运行特性
- scope=singleton/property 控制实例复用与重建；
- 获取配置：getCubeConfig()；
- AngelCube 的 execute 只负责启动守护任务，停止请使用 ActuatorProxy.stop(slotId)。