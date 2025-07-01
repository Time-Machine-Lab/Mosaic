package io.github.tml.mosaic.config;

import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.util.StringUtil;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MosaicComponentConfig {
    private static final List<Class<?>> componentClasses = List.of(CubeContext.class, SlotManager.class, CubeActuatorProxy.class);

    private static Map<Class<?>,  String> beanNameMap = componentClasses.stream()
            .collect(ConcurrentHashMap::new, (map, clazz) -> map.put(clazz, StringUtil.getFirstLowerCase(clazz.getSimpleName()) + WorldContainerFactory.getOriginalUid()), ConcurrentHashMap::putAll);;

    public static List<Class<?>> getComponentClasses() {
        return componentClasses;
    }

    public static String getBeanName(Class<?> clazz) {
        return beanNameMap.get(clazz);
    }
}
