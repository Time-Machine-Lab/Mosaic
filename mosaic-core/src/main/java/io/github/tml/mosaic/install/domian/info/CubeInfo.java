package io.github.tml.mosaic.install.domian.info;

import io.github.tml.mosaic.core.tools.param.ConfigInfo;
import io.github.tml.mosaic.cube.external.MCube;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// 方块信息
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CubeInfo {

    private String id;
    private String name;
    private String version;
    private String description;
    private String model;
    private String className;
    private String frontPath;
    private Class<?> clazz;
    private List<ExtensionPackageInfo> extensionPackages = new ArrayList<>();
    private transient ClassLoader classLoader;

    /**
     * Cube配置信息
     */
    private ConfigInfo configInfo;

    public void setInfoByMCube(MCube mCube) {
        String name = mCube.name().isEmpty() ? this.getClass().getSimpleName() : mCube.name();
        this.setName(name);
        this.setVersion(mCube.version());
        this.setDescription(mCube.description());
        this.setModel(mCube.model());
        this.setFrontPath(mCube.frontPath());
    }
}