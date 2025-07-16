package io.github.tml.mosaic.domain.cube;

import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.execption.SlotException;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.constant.CubeModelType;
import io.github.tml.mosaic.domain.slot.SlotDomain;
import io.github.tml.mosaic.entity.dto.AngleCubeDTO;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.dto.SlotDTO;
import io.github.tml.mosaic.entity.dto.SlotSetupDTO;
import io.github.tml.mosaic.entity.req.AngelCubeStatusUpdateReq;
import io.github.tml.mosaic.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.tml.mosaic.util.RandomUtil.generateSecureRandomCode;

@Slf4j
@Component
public class AngleCubeDomain {

    @Autowired
    private CubeDomain cubeDomain;

    @Autowired
    private SlotDomain slotDomain;

    @Autowired
    private CubeActuatorProxy cubeActuatorProxy;

    // 存放anglecube对应的槽id
    private final Map<String, String> angleCubeSlotMap = new ConcurrentHashMap<>();

    private final Map<String, AngleCubeDTO> angleCubeMap = new ConcurrentHashMap<>();

    /**
     * 验证是否为有效的Angel Cube
     */
    private AngleCubeDTO validateAndGetAngelCube(String cubeId) {

        if (angleCubeMap.containsKey(cubeId)) {
            return angleCubeMap.get(cubeId);
        }
        // 检查Cube是否存在
        AngleCubeDTO cube = createAngleCube(cubeId);

        // 检查是否为angle类型
        if (!CubeModelType.ANGLE_TYPE.equals(cube.getModel())) {
            throw new IllegalArgumentException("cube {} is not angel cube type" + cube.getId());
        }

        return cube;
    }

    public AngleCubeDTO createAngleCube(String cubeId) {

        Optional<CubeDTO> cubeOpt = cubeDomain.getCubeById(cubeId);
        cubeOpt.orElseThrow(()->CubeException.cubeNotExistException(cubeId));

        CubeDTO cube = cubeOpt.get();
        AngleCubeDTO angleCubeDTO = new AngleCubeDTO();
        BeanUtils.copyProperties(cube, angleCubeDTO);

        angleCubeMap.put(cubeId, angleCubeDTO);

        return angleCubeDTO;
    }

    /**
     * 暂停天使方块
     * @param cubeId 方块id
     * @return 是否暂停成功
     * @throws Exception
     */
    public boolean stopAngleCube(String cubeId) throws Exception{
        try {
            AngleCubeDTO cube = validateAndGetAngelCube(cubeId);
            if (cube.isEnable()) {
                String slotId = angleCubeSlotMap.get(cubeId);
                if(StringUtils.isBlank(slotId)){
                    log.error("angle cube {} dont have running slot", cubeId);
                    cube.disable();
                    return true;
                }

                boolean stop = cubeActuatorProxy.stop(new DotNotationId(slotId));
                cube.disable();
                return stop;
            }else{
                throw new CubeException(String.format("cube %s is not enable", cubeId));
            }
        }catch (Exception e){
            log.error("try to stop angle cube error: {}", cubeId, e);
            throw new CubeException("try to stop angle cube error", e);
        }
    }

    /**
     * 启动天使方块
     * @param cubeId 天使方块id
     * @return 是否启动成功
     * @throws Exception 启动失败原因
     */
    public boolean executeAngleCube(String cubeId) throws Exception{
        try {
            AngleCubeDTO cube = validateAndGetAngelCube(cubeId);
            String slotId;
            // 如果天使方块从未执行过，则需要为其分配一个运行槽
            if (StringUtils.isBlank(slotId = angleCubeSlotMap.get(cubeId))) {
                slotId = createAngleCubeSlot(cubeId);
                if(StringUtils.isBlank(slotId)){
                    throw SlotException.CREATE_SLOT_FAILED;
                }
                angleCubeSlotMap.put(cubeId, slotId);
            }
            if (!cube.isEnable()) {
                cubeActuatorProxy.execute(new DotNotationId(slotId));
                cube.enable();
                return true;
            }
            throw new RuntimeException(String.format("angle cube %s is enable", cubeId));
        }catch (Exception e){
            log.error("try to start angle cube error: {}", cubeId, e);
            throw new CubeException("try to start angle cube error", e);
        }
    }

    /**
     * 创建天使方块安装槽
     * @param cubeId 方块id
     * @return 槽id
     */
    public String createAngleCubeSlot(String cubeId) {
        String slotId = buildAngelCubeSlotId(cubeId);
        Optional<SlotDTO> slotOptional = slotDomain.createSlot(slotId);
        if (slotOptional.isEmpty()) {
            return null;
        }
        // 安装槽信息
        SlotSetupDTO setupDTO = new SlotSetupDTO();
        setupDTO.setSlotId(slotId);
        setupDTO.setCubeId(new GUUID(cubeId));
        if (!slotDomain.setupSlot(setupDTO)) {
            return null;
        }
        angleCubeSlotMap.put(cubeId, slotId);
        return slotId;
    }


    private String buildAngelCubeSlotId(String cubeId) {
        return cubeId+".slot."+ RandomUtil.generateSecureRandomCode(6);
    }
}
