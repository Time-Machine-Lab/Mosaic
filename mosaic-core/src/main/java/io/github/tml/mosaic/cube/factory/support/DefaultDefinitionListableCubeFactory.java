package io.github.tml.mosaic.cube.factory.support;

import io.github.tml.mosaic.core.event.MosaicEventBroadcaster;
import io.github.tml.mosaic.core.event.DefaultMosaicEventBroadcaster;
import io.github.tml.mosaic.core.event.event.CubeDefinitionRegisteredEvent;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 描述: Cube工厂的核心实现类
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public class DefaultDefinitionListableCubeFactory extends ListableCubeFactory {

    // 使用ConcurrentHashMap保证线程安全
    private final Map<GUID, CubeDefinition> cubeDefinitionMap = new ConcurrentHashMap<>();
    private final MosaicEventBroadcaster eventBroadcaster;

    public DefaultDefinitionListableCubeFactory() {
        this.eventBroadcaster = DefaultMosaicEventBroadcaster.broadcaster();
    }

    @Override
    public CubeRegistrationResult registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition) {
        if (cubeId == null) {
            return new CubeRegistrationResult(null, false, "CubeId cannot be null");
        }
        if (cubeDefinition == null) {
            return new CubeRegistrationResult(cubeId, false, "CubeDefinition cannot be null");
        }

        if (cubeDefinitionMap.containsKey(cubeId)) {
            String warnMsg = String.format("CubeDefinition [%s] is already registered", cubeId);
            log.warn(warnMsg);
            return new CubeRegistrationResult(cubeId, false, warnMsg);
        }

        // 注册CubeDefinition
        cubeDefinitionMap.put(cubeId, cubeDefinition);

        // 发布事件（简化异常处理）
        try {
            CubeDefinitionRegisteredEvent event = new CubeDefinitionRegisteredEvent(this, cubeId, cubeDefinition);
            eventBroadcaster.broadcastEvent(event);
            return new CubeRegistrationResult(cubeId, true, "Registration successful");
        } catch (Exception e) {
            String errorMsg = String.format("Event publish failed for cubeId [%s]", cubeId);
            log.error(errorMsg, e);
            return new CubeRegistrationResult(cubeId, false, errorMsg + ": " + e.getMessage());
        }
    }

    @Override
    protected CubeDefinition getCubeDefinition(GUID cubeId) throws CubeException {
        CubeDefinition cubeDefinition = cubeDefinitionMap.get(cubeId);
        if(cubeDefinition == null){
            log.warn("CubeDefinition not found for cubeId: {}", cubeId);
        }
        return cubeDefinition;
    }

    // ==================== CubeDefinitionAccessor实现 ====================

    @Override
    public List<CubeDefinition> getAllCubeDefinitions() {
        return List.copyOf(cubeDefinitionMap.values());
    }

    @Override
    public Map<GUID, CubeDefinition> getAllCubeDefinitionMap() {
        return Map.copyOf(cubeDefinitionMap);
    }

    @Override
    public boolean containsCubeDefinition(String cubeId) {
        return cubeId != null && cubeDefinitionMap.containsKey(new GUUID(cubeId));
    }
}