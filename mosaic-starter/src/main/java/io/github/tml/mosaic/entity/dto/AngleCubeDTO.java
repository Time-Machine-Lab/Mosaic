package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.entity.vo.cube.CubeStatus;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AngleCubeDTO extends CubeDTO{

    public static final boolean DISABLE = false;

    private boolean isEnable = DISABLE;

    public void enable(){
        this.isEnable = true;
    }

    public void disable(){
        this.isEnable = false;
    }

    public boolean isEnable(){
        return this.isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public CubeStatus getCubeStatus(){
        return this.isEnable ? CubeStatus.ACTIVE : CubeStatus.INACTIVE;
    }
}
