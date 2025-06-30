package io.github.tml.mosaic.cube;

import io.github.tml.mosaic.core.tools.param.ConfigurableEntity;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.api.CubeApi;
import io.github.tml.mosaic.cube.external.MosaicCube;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 方块抽象基类 - 所有插件必须继承此类
 */
@Slf4j
public class Cube extends ConfigurableEntity implements CubeApi {

    @Getter
    private MetaData metaData;

    @Getter
    @Setter
    private MosaicCube mosaicCube;

    protected volatile boolean initialized = false;

    public Cube(GUID id) {
        super(id);
        this.metaData = new MetaData();
    }

    @Override
    public boolean init() {
        // 判断mosaicCube是否存在
        if (mosaicCube == null) {
            // 不存在则直接实例化并初始化
            return createAndInitInstance();
        }

        // 存在则检查model模式
        String model = metaData.getModel();
        if ("property".equals(model)) {
            // property模式：销毁现有实例，重新创建
            mosaicCube.destroy();
            mosaicCube = null;
            initialized = false;
            return createAndInitInstance();
        }

        // singleton模式或其他情况：复用现有实例
        return true;
    }

    /**
     * 创建实例并初始化
     */
    private boolean createAndInitInstance() {
        // 设置线程变量
        CubeConfigThreadLocal.set(getCubeId().toString(), new CubeConfig(getConfigValues()));
        try {
            mosaicCube = (MosaicCube) metaData.getClazz().getDeclaredConstructor().newInstance();
            boolean result = mosaicCube.init();
            if (result) {
                initialized = true;
                // 初始化扩展包，为mosaicPackage塞入mosaicCube
                for (ExtensionPackage extensionPackage : this.getMetaData().extensionPackages) {
                    Optional.ofNullable(extensionPackage.getMosaicExtPackage())
                            .ifPresent(mosaicExtPackage -> mosaicExtPackage.initCube(mosaicCube));
                }
            }
            return result;
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            log.error("Failed to create and init cube instance: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean destroy() {
       return mosaicCube.destroy();
    }

    @Override
    public String cubeId() {
        return Optional.ofNullable(this.id).map(GUID::toString).orElse(null);
    }

    public GUID getCubeId() {
        return getId();
    }

    @Data
    public static class MetaData {

        private String name;
        private String version;
        private String description;
        private String model;
        private String frontPath; //Cube插件页面地址
        private Class<?> clazz;

        // 扩展包元数据
        private final List<ExtensionPackage> extensionPackages = new CopyOnWriteArrayList<>();
        private final Map<GUID, ExtensionPackage> extensionPackageMap = new ConcurrentHashMap<>();
    }

    public void addExtensionPackage(ExtensionPackage extensionPackage) {
        getMetaData().extensionPackages.add(extensionPackage);
        getMetaData().extensionPackageMap.put(extensionPackage.getId(), extensionPackage);
    }

    public ExtensionPackage findExPackage(GUID packageId) {
        return getMetaData().extensionPackageMap.get(packageId);
    }
}