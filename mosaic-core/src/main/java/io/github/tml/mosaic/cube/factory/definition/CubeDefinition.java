package io.github.tml.mosaic.cube.factory.definition;

import io.github.tml.mosaic.core.tools.param.ConfigInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * 描述: Cube的定义信息
 * @author suifeng
 * 日期: 2025/6/6
 */
@Data
public class CubeDefinition {

    private String id;
    private String name;
    private String version;
    private String description;
    private String model;
    private String className;
    private String frontPath;
    private transient ClassLoader classLoader;
    private final List<ExtensionPackageDefinition> extensionPackages = new ArrayList<>();

    /**
     * Cube配置信息
     */
    private ConfigInfo configInfo;

    public CubeDefinition(String id, String name, String version,
                          String description, String model,
                          String className, ClassLoader classLoader) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.description = description;
        this.model = model;
        this.className = className;
        this.classLoader = classLoader;
    }

    public void addExtensionPackage(ExtensionPackageDefinition epd) {
        extensionPackages.add(epd);
    }
}