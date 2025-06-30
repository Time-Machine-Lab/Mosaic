package io.github.tml.mosaic.cube.external;

import java.lang.annotation.*;

/**
 * 描述: Cube注解，标识这是一个方块
 * @author suifeng
 * 日期: 2025/5/27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MCube {

    /**
     * Cube的name
     */
    String name() default "";
    
    /**
     * 版本号
     */
    String version() default "1.0.0";
    
    /**
     * 描述信息
     */
    String description() default "";
    
    /**
     * 方块的模式: 单例、多例
     */
    String model() default "singleton";

    /**
     * 前端页面地址
     * @return
     */
    String frontPath() default "";
}