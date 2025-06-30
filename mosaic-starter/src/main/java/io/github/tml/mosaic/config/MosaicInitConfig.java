package io.github.tml.mosaic.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.converter.CubeDefinitionConverter;
import io.github.tml.mosaic.converter.InfoContextConverter;
import io.github.tml.mosaic.core.event.DefaultMosaicEventBroadcaster;
import io.github.tml.mosaic.core.event.MosaicEventBroadcaster;
import io.github.tml.mosaic.core.event.listener.MosaicEventListener;
import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.world.construct.MosaicWorld;
import io.github.tml.mosaic.world.replace.ReplaceBeanContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

/**
 * mosaic框架初始化
 */
@Slf4j
@Configuration
public class MosaicInitConfig {

    /*
    * 当前世界
    * */
    @Bean
    public MosaicWorld mosaicWorld(ReplaceBeanContext replaceBeanContext){
        return new MosaicWorld(MosaicComponentConfig.getComponentClasses(), replaceBeanContext);
    }

    /**
     * cube上下文容器
     */
    @Bean
    @DependsOn({"infoContextInstaller", "cubeEventBroadcaster"})
    public CubeContext cubeContext(InfoContextInstaller infoContextInstaller) {
        ClassPathCubeContext context = new ClassPathCubeContext();

        // 初始化安装项Context 收集 -> List<CubeInfo>
        List<CubeInfo> cubeInfos = InfoContextConverter.convertInfoContextsToCubeInfoList(infoContextInstaller.installCubeInfoContext());

        // List<CubeInfo> -> List<CubeDefinition>
        List<CubeDefinition> cubeDefinitions = CubeDefinitionConverter.convertCubeInfoListToCubeDefinitionList(cubeInfos);

        // 注册进context容器
        context.registerAllCubeDefinition(cubeDefinitions);

        // 刷新容器
        context.refresh();

        return context;
    }

    /**
     * 槽管理器
     * @return 槽管理器
     */
    @Bean
    public SlotManager slotManager(){
        GenericSlotManager manager = GenericSlotManager.manager();
        GoldenShovel.loadSlotManager(manager);
        return manager;
    }

    @Bean
    public CubeActuatorProxy cubeActuatorProxy(SlotManager slotManager, CubeContext cubeContext){
        CubeActuatorProxy cubeActuatorProxy = new CubeActuatorProxy();
        cubeActuatorProxy.init(cubeContext, slotManager);
        GoldenShovel.loadCubeActuatorProxy(cubeActuatorProxy);
        return cubeActuatorProxy;
    }
}