package io.github.tml.mosaic.actuator;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用执行器
 */
@Slf4j
public class GenericCubeActuator extends AbstractCubeActuator{

    protected GenericCubeActuator() {

    }

    @Override
    public <T> T execute(ExecuteContext executeContext) {
        preExecuteLog(executeContext);

        return this.execute0(executeContext);
    }

    @Override
    public boolean stop(ExecuteContext executeContext) {
        return true;
    }

    private void preExecuteLog(ExecuteContext executeContext) {
        log.info(" {} ready execute plugin, cube:{}, slot:{}, exPackage:{}, exPoint:{}",
                this.getClass().getName(),
                executeContext.getCube().cubeId(),
                executeContext.getSlot().getId(),
                executeContext.getExPackage().extPackageId(),
                executeContext.getExPoint().getId());
    }
}
