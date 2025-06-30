package io.github.tml.mosaic.install.collector;

import io.github.tml.mosaic.install.collector.core.CommonInfoCollector;
import io.github.tml.mosaic.install.domian.InfoContext;
import io.github.tml.mosaic.install.domian.info.CubeInfo;

public class CubeFrontViewCollector implements CommonInfoCollector {
    
    @Override
    public void collect(InfoContext infoContext) {
        CubeInfo cubeInfo = infoContext.getCubeInfoList().get(0);

    }
}
