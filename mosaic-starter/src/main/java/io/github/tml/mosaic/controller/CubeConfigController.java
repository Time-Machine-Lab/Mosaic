package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.entity.req.CubeConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeMultiConfigUpdateReq;
import io.github.tml.mosaic.entity.req.CubeConfigCloneReq;
import io.github.tml.mosaic.service.CubeConfigService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 描述: Cube配置管理控制器
 * @author suifeng
 * 日期: 2025/6/7
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/mosaic/cube")
public class CubeConfigController {

    private final CubeConfigService cubeConfigService;

    /**
     * 获取指定Cube的默认配置信息
     */
    @GetMapping("/{cubeId}/getConfiguration")
    public R<?> getCubeConfiguration(@PathVariable String cubeId) {
        return cubeConfigService.getCubeConfiguration(cubeId);
    }

    /**
     * 获取指定Cube的所有配置信息
     */
    @GetMapping("/{cubeId}/getAllConfigurations")
    public R<?> getAllCubeConfigurations(@PathVariable String cubeId) {
        return cubeConfigService.getAllCubeConfigurations(cubeId);
    }

    /**
     * 获取指定Cube的特定配置信息
     */
    @GetMapping("/{cubeId}/getConfiguration/{configId}")
    public R<?> getCubeConfiguration(@PathVariable String cubeId, @PathVariable String configId) {
        return cubeConfigService.getCubeConfiguration(cubeId, configId);
    }

    /**
     * 更新指定Cube的默认配置信息
     */
    @PostMapping("/updateConfiguration")
    public R<?> updateCubeConfiguration(@RequestBody CubeConfigUpdateReq configReq) {
        return cubeConfigService.updateCubeConfiguration(configReq);
    }

    /**
     * 更新指定Cube的特定配置信息
     */
    @PostMapping("/updateConfiguration/multi")
    public R<?> updateCubeConfiguration(@RequestBody CubeMultiConfigUpdateReq configReq) {
        return cubeConfigService.updateCubeConfiguration(configReq);
    }

    /**
     * 删除指定Cube的特定配置
     */
    @DeleteMapping("/{cubeId}/removeConfiguration/{configId}")
    public R<?> removeCubeConfiguration(@PathVariable String cubeId, @PathVariable String configId) {
        return cubeConfigService.removeCubeConfiguration(cubeId, configId);
    }

    /**
     * 克隆指定Cube的配置
     */
    @PostMapping("/cloneConfiguration")
    public R<?> cloneCubeConfiguration(@RequestBody CubeConfigCloneReq cloneReq) {
        return cubeConfigService.cloneCubeConfiguration(cloneReq);
    }
}
