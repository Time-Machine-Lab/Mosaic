package io.github.tml.mosaic.cube.factory.support;

import io.github.tml.mosaic.cube.factory.config.CubeManager;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GuuidAllocator;
import io.github.tml.mosaic.cube.Cube;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 描述: 默认Cube管理器实现
 * @author suifeng
 * 日期: 2025/6/11
 */
@Slf4j
public class DefaultCubeManager extends DefaultSingletonCubeRegistry implements CubeManager {

    // 主存储结构：CubeId -> Cube实例列表
    private final Map<GUID, List<Cube>> cubeInstancesMap = new ConcurrentHashMap<>();
    
    // 实例ID到Cube的映射，便于快速查找
    private final Map<GUID, Cube> instanceIdToCubeMap = new ConcurrentHashMap<>();
    
    // 实例ID到CubeId的映射，便于反向查找
    private final Map<GUID, GUID> instanceIdToCubeIdMap = new ConcurrentHashMap<>();
    
    // ID分配器
    private final GuuidAllocator guidAllocator = new GuuidAllocator();

    @Override
    public GUID registerCube(GUID cubeId, Cube cube) {
        // 为实例分配唯一ID
        GUID instanceId = guidAllocator.nextGUID();
        
        // 添加到主存储结构
        cubeInstancesMap.computeIfAbsent(cubeId, k -> new CopyOnWriteArrayList<>()).add(cube);
        
        // 建立映射关系
        instanceIdToCubeMap.put(instanceId, cube);
        instanceIdToCubeIdMap.put(instanceId, cubeId);
        
        return instanceId;
    }

    @Override
    public boolean removeCubeByInstanceId(GUID instanceId) {
        Cube cube = instanceIdToCubeMap.get(instanceId);
        if (cube == null) {
            log.warn("⚠ 尝试移除不存在的Cube实例 | 实例ID: {}", instanceId);
            return false;
        }
        
        GUID cubeId = instanceIdToCubeIdMap.get(instanceId);
        
        // 从主存储结构中移除
        List<Cube> instances = cubeInstancesMap.get(cubeId);
        if (instances != null) {
            instances.remove(cube);
            // 如果列表为空，移除整个条目
            if (instances.isEmpty()) {
                cubeInstancesMap.remove(cubeId);
            }
        }
        
        // 清理映射关系
        instanceIdToCubeMap.remove(instanceId);
        instanceIdToCubeIdMap.remove(instanceId);
        
        log.debug("✓ Cube实例移除成功 | CubeId: {} | 实例ID: {}", cubeId, instanceId);
        return true;
    }

    @Override
    public int removeCubesByCubeId(GUID cubeId) {
        List<Cube> instances = cubeInstancesMap.remove(cubeId);
        if (instances == null || instances.isEmpty()) {
            return 0;
        }
        
        int removedCount = instances.size();
        
        // 清理所有相关的映射关系
        instanceIdToCubeIdMap.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(cubeId)) {
                instanceIdToCubeMap.remove(entry.getKey());
                return true;
            }
            return false;
        });
        
        log.debug("✓ 移除CubeId所有实例 | CubeId: {} | 数量: {}", cubeId, removedCount);
        return removedCount;
    }

    @Override
    public Cube getCubeByInstanceId(GUID instanceId) {
        return instanceIdToCubeMap.get(instanceId);
    }

    @Override
    public List<Cube> getCubesByCubeId(GUID cubeId) {
        List<Cube> instances = cubeInstancesMap.get(cubeId);
        return instances != null ? new ArrayList<>(instances) : new ArrayList<>();
    }

    @Override
    public Cube getFirstCubeByCubeId(GUID cubeId) {
        List<Cube> instances = cubeInstancesMap.get(cubeId);
        return (instances != null && !instances.isEmpty()) ? instances.get(0) : null;
    }

    @Override
    public boolean containsCubeId(GUID cubeId) {
        return cubeInstancesMap.containsKey(cubeId) && !cubeInstancesMap.get(cubeId).isEmpty();
    }

    @Override
    public boolean containsInstanceId(GUID instanceId) {
        return instanceIdToCubeMap.containsKey(instanceId);
    }

    @Override
    public Set<GUID> getAllCubeIds() {
        return new HashSet<>(cubeInstancesMap.keySet());
    }

    @Override
    public Set<GUID> getAllInstanceIds() {
        return new HashSet<>(instanceIdToCubeMap.keySet());
    }

    @Override
    public int getCubeInstanceCount(GUID cubeId) {
        List<Cube> instances = cubeInstancesMap.get(cubeId);
        return instances != null ? instances.size() : 0;
    }

    @Override
    public int getTotalInstanceCount() {
        return instanceIdToCubeMap.size();
    }

    @Override
    public Map<GUID, Cube> getAllCubeInstances() {
        return new HashMap<>(instanceIdToCubeMap);
    }

    @Override
    public void clear() {
        cubeInstancesMap.clear();
        instanceIdToCubeMap.clear();
        instanceIdToCubeIdMap.clear();
        log.info("✓ 管理器已清空所有Cube实例");
    }
}