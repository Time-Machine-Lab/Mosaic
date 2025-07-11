package io.github.tml.mosaic.entity.vo.slot;

import lombok.Data;

@Data
public class SlotVO {

    private String slotId;

    // 方块唯一Id
    private String cubeId;

    // 调用的拓展包Id
    private String exPackageId;

    // 调用的拓展点Id
    private String exPointId;

    // 需要的返回名称
    private String resName;

    private boolean setupFlag = false;
}
