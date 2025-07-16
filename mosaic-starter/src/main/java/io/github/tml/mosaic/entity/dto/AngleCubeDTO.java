package io.github.tml.mosaic.entity.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AngleCubeDTO extends CubeDTO{

    private boolean isEnable = false;

    public void enable(){
        this.isEnable = true;
    }

    public void disable(){
        this.isEnable = false;
    }

    public boolean isEnable(){
        return this.isEnable;
    }
}
