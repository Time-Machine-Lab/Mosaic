package io.github.tml.mosaic.domain.cube;

import io.github.tml.mosaic.convert.CubeConvert;
import io.github.tml.mosaic.converter.CubeDefinitionConverter;
import io.github.tml.mosaic.converter.InfoContextConverter;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.constant.CubeModelType;
import io.github.tml.mosaic.cube.external.AngelCube;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeMultiConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigAddReq;
import io.github.tml.mosaic.entity.req.CubeConfigCloneReq;
import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.entity.dto.CubeOverviewDTO;
import io.github.tml.mosaic.entity.vo.cube.CubeInfoVO;
import io.github.tml.mosaic.entity.vo.cube.CubeStatus;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.install.support.ReaderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Cube领域服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CubeDomain {
    private final CubeContext cubeContext;
    private final InfoContextInstaller installer;


    /**
     * 获取所有Cube列表
     */
    public List<CubeDTO> getCubeList() {
        List<CubeDefinition> cubeDefinitions = cubeContext.getAllCubeDefinitions();

        return cubeDefinitions.stream()
                .filter(cubeDefinition -> !cubeDefinition.isAngleCube())
                .map(CubeConvert::convert2DTO)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(CubeDTO::getId))
                .collect(Collectors.toList());
    }

    /**
     * 获取Cube默认配置信息
     * @param cubeId Cube标识
     */
    public Map<String, Object> getCubeConfiguration(String cubeId) {
        log.debug("Domain: Fetching default configuration for cube ID: {}", cubeId);

        try {
            Map<String, Object> configurations = cubeContext.getCubeConfiguration(cubeId);
            log.info("Domain: Successfully retrieved default configuration for cube ID: {}", cubeId);
            return configurations;
        } catch (Exception e) {
            log.error("Domain: Failed to fetch default configuration for cube ID: {}", cubeId, e);
            throw new RuntimeException("获取Cube默认配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取Cube所有配置信息
     * @param cubeId Cube标识
     */
    public Map<String, Map<String, Object>> getAllCubeConfigurations(String cubeId) {
        log.debug("Domain: Fetching all configurations for cube ID: {}", cubeId);

        try {
            Map<String, Map<String, Object>> configurations = cubeContext.getAllCubeConfigurations(cubeId);
            log.info("Domain: Successfully retrieved all configurations for cube ID: {}", cubeId);
            return configurations;
        } catch (Exception e) {
            log.error("Domain: Failed to fetch all configurations for cube ID: {}", cubeId, e);
            throw new RuntimeException("获取Cube所有配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取Cube指定配置信息
     * @param cubeId Cube标识
     * @param configId 配置标识
     */
    public Map<String, Object> getCubeConfiguration(String cubeId, String configId) {
        log.debug("Domain: Fetching configuration for cube ID: {}, config ID: {}", cubeId, configId);

        try {
            Map<String, Object> configurations = cubeContext.getCubeConfiguration(cubeId, configId);
            log.info("Domain: Successfully retrieved configuration for cube ID: {}, config ID: {}", cubeId, configId);
            return configurations;
        } catch (Exception e) {
            log.error("Domain: Failed to fetch configuration for cube ID: {}, config ID: {}", cubeId, configId, e);
            throw new RuntimeException("获取Cube指定配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证是否为有效的Angel Cube
     */
    private void validateAngelCube(String cubeId) {
        Optional<CubeDTO> cubeOpt = getCubeById(cubeId);
        if (cubeOpt.isEmpty()) {
            throw new IllegalArgumentException("Cube不存在，ID: " + cubeId);
        }

        CubeDTO cube = cubeOpt.get();
        if (!CubeModelType.ANGLE_TYPE.equals(cube.getModel())) {
            throw new IllegalArgumentException("只有angle类型的Cube才支持状态管理，当前类型: " + cube.getModel());
        }

        log.debug("Angel Cube validation passed for ID: {}", cubeId);
    }

    /**
     * 更新Cube默认配置信息
     * @param cubeId Cube标识
     * @param configReq 配置更新请求
     */
    public Map<String, Object> updateCubeConfiguration(String cubeId, CubeConfigUpdateReq configReq) {
        log.debug("Domain: Updating default configuration for cube ID: {} with request: {}", cubeId, configReq);

        try {
            Map<String, Object> updatedConfigurations = cubeContext.updateConfigurations(
                    cubeId,
                    configReq.getConfigurations()
            );

            log.info("Domain: Successfully updated default configuration for cube ID: {}", cubeId);
            return updatedConfigurations;
        } catch (Exception e) {
            log.error("Domain: Failed to update default configuration for cube ID: {}", cubeId, e);
            throw new RuntimeException("更新Cube默认配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新Cube指定配置信息
     * @param configReq 多配置更新请求
     */
    public Map<String, Object> updateCubeConfiguration(CubeMultiConfigUpdateReq configReq) {
        String cubeId = configReq.getCubeId();
        String configId = configReq.getConfigId();
        log.debug("Domain: Updating configuration for cube ID: {}, config ID: {} with request: {}",
                cubeId, configId, configReq);

        try {
            Map<String, Object> updatedConfigurations = cubeContext.updateCubeConfiguration(
                    cubeId,
                    configId,
                    configReq.getConfigurations()
            );

            log.info("Domain: Successfully updated configuration for cube ID: {}, config ID: {}", cubeId, configId);
            return updatedConfigurations;
        } catch (Exception e) {
            log.error("Domain: Failed to update configuration for cube ID: {}, config ID: {}", cubeId, configId, e);
            throw new RuntimeException("更新Cube指定配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除Cube指定配置
     * @param cubeId Cube标识
     * @param configId 配置标识
     */
    public boolean removeCubeConfiguration(String cubeId, String configId) {
        log.debug("Domain: Removing configuration for cube ID: {}, config ID: {}", cubeId, configId);

        try {
            boolean removed = cubeContext.removeCubeConfiguration(cubeId, configId);

            if (removed) {
                log.info("Domain: Successfully removed configuration for cube ID: {}, config ID: {}", cubeId, configId);
            } else {
                log.warn("Domain: Configuration not found or cannot be removed for cube ID: {}, config ID: {}", cubeId, configId);
            }

            return removed;
        } catch (Exception e) {
            log.error("Domain: Failed to remove configuration for cube ID: {}, config ID: {}", cubeId, configId, e);
            throw new RuntimeException("删除Cube配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 克隆Cube配置
     * @param cloneReq 配置克隆请求
     */
    public String cloneCubeConfiguration(CubeConfigCloneReq cloneReq) {
        String cubeId = cloneReq.getCubeId();
        String sourceConfigId = cloneReq.getSourceConfigId();
        log.debug("Domain: Cloning configuration for cube ID: {}, source config ID: {}", cubeId, sourceConfigId);

        try {
            String newConfigId = cubeContext.cloneCubeConfiguration(cubeId, sourceConfigId);
            log.info("Domain: Successfully cloned configuration for cube ID: {}, source config ID: {}, new config ID: {}",
                    cubeId, sourceConfigId, newConfigId);
            return newConfigId;
        } catch (Exception e) {
            log.error("Domain: Failed to clone configuration for cube ID: {}, source config ID: {}", cubeId, sourceConfigId, e);
            throw new RuntimeException("克隆Cube配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 安装并注册Cube
     * @param configLocations 配置位置数组
     * @return 注册结果列表
     */
    public List<CubeRegistrationResult> installAndRegisterCubes(String... configLocations) {
        try {
            List<CubeInfo> cubeInfos = InfoContextConverter.convertInfoContextsToCubeInfoList(
                    installer.installCubeInfoContext(configLocations)
            );

            List<CubeDefinition> cubeDefinitions = CubeDefinitionConverter.convertCubeInfoListToCubeDefinitionList(cubeInfos);

            return cubeContext.registerAllCubeDefinition(cubeDefinitions);

        } catch (Exception e) {
            log.error("安装并注册Cube失败", e);
            throw new RuntimeException("安装并注册Cube失败: " + e.getMessage(), e);
        }
    }

    /**
     * 通过JAR路径安装并注册Cube
     * @param jarPath JAR文件路径
     * @return 注册结果列表
     */
    public List<CubeRegistrationResult> installAndRegisterCube(Path jarPath) {
        String[] configLocations = new String[]{ReaderType.FILE.getPrefix() + jarPath};
        return installAndRegisterCubes(configLocations);
    }

    /**
     * 根据过滤条件获取Cube信息
     */
    public List<CubeDTO> getCubesByFilter(CubeFilterReq filterDTO) {
        log.debug("Domain: Applying filter criteria: {}", filterDTO);

        List<CubeDefinition> allDefinitions = cubeContext.getAllCubeDefinitions();

        List<CubeDefinition> filteredDefinitions = allDefinitions.stream()
                .filter(buildDomainFilterPredicate(filterDTO))
                .collect(Collectors.toList());

        List<CubeDTO> result = filteredDefinitions.stream()
                .map(CubeConvert::convert2DTO)
                .sorted(Comparator.comparing(CubeDTO::getId))
                .collect(Collectors.toList());

        log.info("Domain: Filter applied, found {} matching cubes", result.size());
        return result;
    }

    /**
     * 根据ID获取特定Cube信息
     */
    public Optional<CubeDTO> getCubeById(String cubeId) {
        log.debug("Domain: Fetching cube definition for ID: {}", cubeId);

        Map<GUID, CubeDefinition> definitionMap = cubeContext.getAllCubeDefinitionMap();
        CubeDefinition definition = definitionMap.get(new GUUID(cubeId));

        if (definition == null) {
            log.warn("Domain: Cube definition not found for ID: {}", cubeId);
            return Optional.empty();
        }

        CubeDTO result = CubeConvert.convert2DTO(definition);
        log.debug("Domain: Successfully retrieved cube definition for ID: {}", cubeId);

        return Optional.of(result);
    }

    /**
     * 生成Cube概览统计信息
     */
    public CubeOverviewDTO generateCubeOverview() {
        log.debug("Domain: Generating cube overview statistics");

        List<CubeDefinition> allDefinitions = cubeContext.getAllCubeDefinitions();
        CubeOverviewDTO overview = CubeConvert.convertToOverviewDTO(allDefinitions);

        log.info("Domain: Successfully generated cube overview statistics");
        return overview;
    }

    /**
     * 检查Cube是否存在
     */
    public boolean hasCube(String cubeId) {
        log.debug("Domain: Checking cube existence for ID: {}", cubeId);
        boolean exists = cubeContext.containsCubeDefinition(cubeId);
        log.debug("Domain: Cube {} existence check result: {}", cubeId, exists);
        return exists;
    }

    /**
     * 构建领域级别的过滤谓词
     */
    private Predicate<CubeDefinition> buildDomainFilterPredicate(CubeFilterReq filterDTO) {
        Predicate<CubeDefinition> predicate = def -> true;

        if (filterDTO.hasName()) {
            predicate = predicate.and(def ->
                    def.getName().toLowerCase().contains(filterDTO.getName().toLowerCase()));
        }

        if (filterDTO.hasModel()) {
            predicate = predicate.and(def ->
                    filterDTO.getModel().equals(def.getModel()));
        }

        if (filterDTO.hasVersion()) {
            predicate = predicate.and(def ->
                    filterDTO.getVersion().equals(def.getVersion()));
        }

        return predicate;
    }
}