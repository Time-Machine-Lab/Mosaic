# 开发扩展包与扩展点

## 1. 扩展包类
```java
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;

public class ExampleExtPkg extends MosaicExtPackage<ExampleCube> {
    // mosaicCube 字段由框架在 initCube 时注入
}
```

## 2. 扩展点方法
```java
import io.github.tml.mosaic.cube.external.MExtension;

public class ExampleExtPkg extends MosaicExtPackage<ExampleCube> {

    @MExtension(extPointId="example.echo", name="回声", description="返回输入")
    public String echo(String input) {
        return "[echo] " + input;
    }
}
```

## 3. 返回值
- 返回普通类型：框架封装为 MosaicResult，并以 default 命名；
- 返回 MosaicResult：可自定义多项命名结果；
- 返回 void/MosaicVoid：表示无返回。