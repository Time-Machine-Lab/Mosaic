package io.github.tml.mosaic.cube.factory.context.support;

import com.alibaba.fastjson.JSONObject;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.core.tools.param.ConfigInfo;
import io.github.tml.mosaic.core.tools.param.ConfigItem;
import io.github.tml.mosaic.cube.config.ConfigReader;
import io.github.tml.mosaic.cube.config.YamlConfigReader;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract Cube context for loading configuration from files.
 * Handles config reading, parsing, and storage.
 *
 * Author: suifeng
 * Date: 2025/6/7
 */
@Getter
@Slf4j
public abstract class AbstractConfigLoaderCubeContext extends AbstractRefreshableCubeContext {

    /** Stores configuration data by top-level config key */
    protected final Map<String, JSONObject> configurationMap = new ConcurrentHashMap<>();

    /** The config file reader */
    private ConfigReader configReader;

    @Override
    public Map<String, Object> updateConfigurations(String cubeId, Map<String, Object> config) {
        CubeDefinition cubeDefinition = getAllCubeDefinitionMap().get(new GUUID(cubeId));
        if (cubeDefinition == null) {
            log.error("Cube definition not found for cubeId: {}", cubeId);
            throw new CubeException("Cube definition not found, cubeId: " + cubeId);
        }
        ConfigInfo configInfo = cubeDefinition.getConfigInfo();

        // Check required configs
        List<String> missing = configInfo.validateRequiredConfigs(config);
        List<String> result = new ArrayList<>(missing);

        // Validate each config item
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            ConfigItem item = configInfo.getConfigItem(name);

            if (item != null && !item.getValidationResult(value).isValid()) {
                result.add("Invalid config value: " + name + " = " + value);
            }
        }

        if (!result.isEmpty()) {
            throw new CubeException("Cube configs update failed: { " + String.join(", ", result) + " }");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(config);
        configurationMap.put(cubeId, jsonObject);

        // If singleton, remove and re-initialize
        if ("singleton".equals(cubeDefinition.getModel())) {
            removeSingletonCube(new GUUID(cubeId));
        }

        return config;
    }

    /** Constructor: initialize config reader */
    public AbstractConfigLoaderCubeContext() {
        super();
        this.configReader = createDefaultConfigReader();
        log.info("AbstractConfigLoaderCubeContext initialized with default config reader");
    }

    /** Refresh and reload all configuration resources from files */
    @Override
    protected void refreshConfigurationResources() throws CubeException {
        log.info("Refreshing configuration resources...");

        try {
            clearConfigurations();
            String[] configLocations = getConfigLocations();

            if (configLocations == null || configLocations.length == 0) {
                log.warn("No configuration locations specified, loading default configuration.");
                loadDefaultConfiguration();
                return;
            }

            log.info("Loading configuration from {} locations: {}", configLocations.length, Arrays.toString(configLocations));
            for (String configLocation : configLocations) {
                if (configLocation == null || configLocation.trim().isEmpty()) {
                    log.warn("Skipping empty configuration location.");
                    continue;
                }
                loadConfigurationFromLocation(configLocation.trim());
            }

        } catch (Exception e) {
            log.error("Failed to refresh configuration resources", e);
            throw new CubeException("Configuration refresh failed: " + e.getMessage(), e);
        }
    }

    /** Remove all stored configuration data */
    protected void clearConfigurations() {
        configurationMap.clear();
        log.info("All configurations cleared.");
    }

    /** Load and parse configuration from a given file location */
    private void loadConfigurationFromLocation(String configLocation) throws CubeException {
        log.debug("Loading configuration from: {}", configLocation);

        try {
            // Set config file path if using YAML reader
            if (configReader instanceof YamlConfigReader) {
                ((YamlConfigReader) configReader).setYamlFilePath(configLocation);
            }

            JSONObject configData = configReader.readConfig();
            if (configData == null || configData.isEmpty()) {
                log.warn("No configuration data found in: {}", configLocation);
                return;
            }

            parseAndStoreConfiguration(configData, configLocation);
            log.info("Loaded configuration from: {}", configLocation);

        } catch (Exception e) {
            log.error("Failed to load configuration from: {}", configLocation, e);
            throw new CubeException("Failed to load configuration from: " + configLocation, e);
        }
    }

    /** Parse configuration data and store by top-level key */
    private void parseAndStoreConfiguration(JSONObject configData, String configLocation) {
        log.debug("Parsing configuration from: {}", configLocation);
        for (Map.Entry<String, Object> entry : configData.entrySet()) {
            String topLevelKey = entry.getKey();
            Object value = entry.getValue();

            if (topLevelKey == null || topLevelKey.trim().isEmpty()) {
                log.warn("Skipping configuration with empty key from: {}", configLocation);
                continue;
            }

            JSONObject configSection;
            if (value instanceof Map) {
                configSection = new JSONObject((Map<String, Object>) value);
            } else {
                configSection = new JSONObject();
                configSection.put("value", value);
            }
            updateConfiguration(topLevelKey, configSection);
            log.debug("Stored config section: {} from: {}", topLevelKey, configLocation);
        }
    }

    /** Load default configuration if no location is provided */
    private void loadDefaultConfiguration() {
        log.info("Loading default configuration...");
        try {
            JSONObject defaultConfig = configReader.readConfig();
            if (defaultConfig != null && !defaultConfig.isEmpty()) {
                parseAndStoreConfiguration(defaultConfig, "default");
                log.info("Default configuration loaded.");
            } else {
                log.warn("No default configuration found.");
            }
        } catch (Exception e) {
            log.error("Failed to load default configuration", e);
        }
    }

    /** Create the default config reader (YAML by default) */
    protected ConfigReader createDefaultConfigReader() {
        return new YamlConfigReader();
    }

    /** Set the config reader instance */
    public void setConfigReader(ConfigReader configReader) {
        this.configReader = Objects.requireNonNull(configReader, "ConfigReader cannot be null");
        log.info("ConfigReader set to: {}", configReader.getClass().getSimpleName());
    }

    /** Update or add configuration under a particular key */
    protected void updateConfiguration(String configKey, JSONObject configValue) {
        if (configKey == null || configKey.trim().isEmpty()) {
            log.warn("Attempted to update configuration with empty key.");
            return;
        }
        if (configValue == null) {
            log.warn("Attempted to update configuration with null value for key: {}", configKey);
            configValue = new JSONObject();
        }
        configurationMap.put(configKey, configValue);
        log.debug("Configuration updated for key: {}", configKey);
    }

    /** Get configuration by key; return empty JSONObject if not found */
    @Override
    public Map<String, Object> getCubeConfiguration(String configKey) {
        if (configKey == null || configKey.trim().isEmpty()) {
            log.warn("Attempted to get configuration with empty key.");
            return new JSONObject();
        }
        JSONObject config = configurationMap.get(configKey);
        return config != null ? config : new JSONObject();
    }

    /** Convert JSONObject to Map<String, Object>, supporting recursion */
    private Map<String, Object> convertJsonObjectToMap(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            log.debug("Empty or null JSONObject, returning empty map.");
            return Collections.emptyMap();
        }

        Map<String, Object> resultMap = new HashMap<>(jsonObject.size());
        try {
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof JSONObject) {
                    resultMap.put(key, convertJsonObjectToMap((JSONObject) value));
                } else if (value instanceof com.alibaba.fastjson.JSONArray) {
                    resultMap.put(key, convertJsonArrayToList((com.alibaba.fastjson.JSONArray) value));
                } else {
                    resultMap.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error("Error converting JSONObject to Map: {}", jsonObject, e);
            throw new CubeException("Failed to convert configuration to map", e);
        }
        return resultMap;
    }

    /** Convert JSONArray to List<Object>, supporting recursion */
    private List<Object> convertJsonArrayToList(com.alibaba.fastjson.JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> resultList = new ArrayList<>(jsonArray.size());
        for (Object item : jsonArray) {
            if (item instanceof JSONObject) {
                resultList.add(convertJsonObjectToMap((JSONObject) item));
            } else if (item instanceof com.alibaba.fastjson.JSONArray) {
                resultList.add(convertJsonArrayToList((com.alibaba.fastjson.JSONArray) item));
            } else {
                resultList.add(item);
            }
        }
        return resultList;
    }

    /**
     * Abstract method for providing config file locations.
     * Subclasses must implement to specify config paths.
     */
    protected abstract String[] getConfigLocations();
}