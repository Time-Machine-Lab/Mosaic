# 扩展 API

## 扩展点注解
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MExtension {
    String extPointId();
    String name() default "";
    String description() default "";
    int priority() default 100;
}
```

## 扩展包抽象
```java
public abstract class MosaicExtPackage<T extends MosaicCube> implements ExtensionPackageApi {
    protected T mosaicCube;
    @Override
    public void initCube(MosaicCube mosaicCube) {
        this.mosaicCube = (T) mosaicCube;
    }
}
```

## 返回值约定
- 返回 MosaicResult：框架原样使用（并以 default 名再存一份），调用方可按 resName + itemName 拿到命名结果；
- 返回普通类型：框架封装为 MosaicResult 并以 default 命名；
- 返回 void/MosaicVoid：表示无返回。