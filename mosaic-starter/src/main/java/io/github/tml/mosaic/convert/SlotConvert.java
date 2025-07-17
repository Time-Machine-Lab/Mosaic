package io.github.tml.mosaic.convert;

import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.entity.dto.SlotDTO;
import io.github.tml.mosaic.entity.dto.SlotSetupDTO;
import io.github.tml.mosaic.entity.req.AppendSlotReq;
import io.github.tml.mosaic.entity.vo.slot.SlotVO;
import io.github.tml.mosaic.slot.Slot;
import org.springframework.beans.BeanUtils;

import java.util.Optional;

public class SlotConvert {

    public static SlotDTO convert2DTO(Slot slot) {
        SlotDTO slotDTO = new SlotDTO(slot.getId().toString());
        slotDTO.Setup(slot.getSetupCubeInfo());
        return slotDTO;
    }

    public static SlotVO convert2VO(SlotDTO slotDTO) {
        SlotVO slotVO = new SlotVO();
        Slot.SetupCubeInfo setupCubeInfo = slotDTO.getSetupCubeInfo();
        slotVO.setSlotId(slotDTO.getId().toString());
        slotVO.setSetupFlag(slotDTO.isSetupReady());

        if(setupCubeInfo!=null){
            BeanUtils.copyProperties(setupCubeInfo, slotVO);
            slotVO.setCubeId(setupCubeInfo.getCubeId().toString());

            slotVO.setExPointId(Optional.ofNullable(setupCubeInfo.getExPointId()).map(Object::toString).orElse(""));
            slotVO.setExPackageId(Optional.ofNullable(setupCubeInfo.getExPackageId()).map(Object::toString).orElse(""));
        }
        return slotVO;
    }

    public static SlotSetupDTO appendSlotReqConvert2SetupDTO(AppendSlotReq appendSlotReq){
        SlotSetupDTO slotSetupDTO = new SlotSetupDTO();
        slotSetupDTO.setSlotId(appendSlotReq.getSlotId());
        slotSetupDTO.setCubeId(new GUUID(appendSlotReq.getCubeId()));
        slotSetupDTO.setResName(appendSlotReq.getResName());
        slotSetupDTO.setExPointId(new DotNotationId(appendSlotReq.getExPointId()));
        slotSetupDTO.setExPackageId(new DotNotationId(appendSlotReq.getExPackageId()));
        return slotSetupDTO;
    }
}
