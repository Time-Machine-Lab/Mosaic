package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.convert.CubeConvert;
import io.github.tml.mosaic.domain.cube.AngleCubeDomain;
import io.github.tml.mosaic.domain.cube.CubeDomain;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.entity.vo.cube.CubeInfoVO;
import io.github.tml.mosaic.service.CubeService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Cube信息查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CubeServiceImpl implements CubeService {

    private final CubeDomain cubeDomain;

    private final AngleCubeDomain angleCubeDomain;

    @Override
    public R<?> getCubeList() {
        List<CubeDTO> cubeList = cubeDomain.getCubeList();
        List<CubeInfoVO> cubeVOs = CubeConvert.convert2VOs(cubeList);
        
        return R.success(Map.of(
                "cubeList", cubeVOs,
                "total", cubeVOs.size()
        ));
    }

    @Override
    public R<?> getCubesByFilter(CubeFilterReq cubeFilterReq) {
        log.debug("Service: Applying filter criteria: {}", cubeFilterReq);
        
        List<CubeDTO> cubeList = cubeDomain.getCubesByFilter(cubeFilterReq);
        List<CubeInfoVO> cubeVOs = CubeConvert.convert2VOs(cubeList);
        
        return R.success(Map.of(
                "cubeList", cubeVOs,
                "total", cubeVOs.size(),
                "searchCriteria", Map.of(
                        "name", cubeFilterReq.getName() != null ? cubeFilterReq.getName() : "",
                        "model", cubeFilterReq.getModel() != null ? cubeFilterReq.getModel() : "",
                        "version", cubeFilterReq.getVersion() != null ? cubeFilterReq.getVersion() : ""
                )
        ));
    }

    @Override
    public R<?> getCubeById(String cubeId) {
        log.debug("Service: Fetching cube for ID: {}", cubeId);
        
        return cubeDomain.getCubeById(cubeId)
                .map(CubeConvert::convert2VO)
                .map(R::success)
                .orElse(R.error("Cube not found for ID: " + cubeId));
    }

    @Override
    public R<?> getCubeOverview() {
        log.debug("Service: Generating cube overview");
        return R.success(cubeDomain.generateCubeOverview());
    }

    @Override
    public R<?> checkCubeExists(String cubeId) {
        boolean exists = cubeDomain.hasCube(cubeId);
        
        return R.success(Map.of(
                "cubeId", cubeId,
                "exists", exists
        ));
    }

    @Override
    public R<?> updateAngelCubeStatus(AngelCubeStatusUpdateReq statusReq) {
        String cubeId = statusReq.getCubeId();
        AngelCubeStatusUpdateReq.AngelCubeAction action = statusReq.getAction();

        log.debug("Service: Updating Angel Cube status for ID: {} with action: {}", cubeId, action);

        try {
            boolean operationSuccess = false;
            String resultMessage = "";

            if (AngelCubeStatusUpdateReq.AngelCubeAction.START.equals(action)) {
                operationSuccess = angleCubeDomain.executeAngleCube(cubeId);
                resultMessage = operationSuccess ? "Angel Cube 启动成功" : "Angel Cube 启动失败";
            } else if (AngelCubeStatusUpdateReq.AngelCubeAction.STOP.equals(action)) {
                operationSuccess = angleCubeDomain.stopAngleCube(cubeId);
                resultMessage = operationSuccess ? "Angel Cube 停止成功" : "Angel Cube 停止失败";
            }

            if (operationSuccess) {
                // 操作成功，返回更新后的状态信息
                Map<String, Object> result = Map.of(
                        "cubeId", cubeId,
                        "action", action.name(),
                        "actionDescription", action.getDescription(),
                        "success", true,
                        "message", resultMessage,
                        "newStatus", action.equals(AngelCubeStatusUpdateReq.AngelCubeAction.START) ? "ACTIVE" : "INACTIVE",
                        "timestamp", System.currentTimeMillis()
                );

                log.info("Service: Angel Cube status updated successfully for ID: {} with action: {}", cubeId, action);
                return R.success(resultMessage, result);
            } else {
                // 操作失败
                log.warn("Service: Angel Cube status update failed for ID: {} with action: {}", cubeId, action);
                return R.error(resultMessage);
            }

        } catch (IllegalArgumentException e) {
            log.warn("Service: Invalid Angel Cube status update request for ID: {}", cubeId, e);
            return R.error("参数错误: " + e.getMessage());
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("Service: Failed to update Angel Cube status for ID: {} with action: {}. Error: {}", cubeId, action, errorMessage);
            return R.error("更新 Angel Cube 状态失败: " + errorMessage);
        }
    }
}