package io.github.tml.mosaic.controller;

import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;
import io.github.tml.mosaic.service.WorldService;
import io.github.tml.mosaic.util.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/mosaic/world")
public class WorldController {
    @Resource
    private WorldService worldService;

    @PostMapping("/create")
    public R<?> createWorld(@RequestBody WorldContainerDTO worldContainerDTO){
        return R.success(worldService.createWorld(worldContainerDTO));
    }

    @GetMapping("/getAll")
    public R<?> getAllWorlds(){
        return R.success(worldService.getAllWorlds());
    }

    @GetMapping("/traverse")
    public R<?> traverseWorld(@RequestParam String uuid){
        WorldContainerVO worldContainerVO = worldService.traverseWorld(uuid);
        return Objects.isNull(worldContainerVO) ? R.error("未找到世界") : R.success(worldContainerVO);
    }

    @GetMapping("/remove")
    public R<?> removeWorld(@RequestParam String uuid){
        WorldContainerVO worldContainerVO = worldService.removeWorld(uuid);
        return Objects.isNull(worldContainerVO) ? R.error("未找到世界") : R.success(worldContainerVO);
    }

    @GetMapping("/getNowWorld")
    public R<?> getNowWorld(){
        return R.success(worldService.getNowWorld());
    }
}
