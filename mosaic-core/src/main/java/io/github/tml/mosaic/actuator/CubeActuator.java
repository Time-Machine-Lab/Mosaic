package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.cube.ExtPointResult;
import io.github.tml.mosaic.cube.ExtensionPoint;
import io.github.tml.mosaic.cube.external.MosaicCube;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.mosaic.slot.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 方块执行器
 */
public interface CubeActuator {
    // TODO execute方法改写
    <T> T execute(ExecuteContext executeContext);

    boolean stop(ExecuteContext executeContext);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ExecuteContext{

        private Slot slot;

        private MosaicCube cube;

        private boolean isAngleCube;

        private MosaicExtPackage<? extends MosaicCube> exPackage;

        private ExtensionPoint exPoint;

        private Object[] args;

    }
}
