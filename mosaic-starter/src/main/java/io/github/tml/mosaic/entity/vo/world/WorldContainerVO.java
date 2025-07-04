package io.github.tml.mosaic.entity.vo.world;

import io.github.tml.mosaic.core.tools.guid.GUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class WorldContainerVO {
    private GUID id;

    private String name;

    private Integer version;

    public WorldContainerVO(GUID id, String name, Integer version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    @Override
    public String toString() {
        return "WorldContainerDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version=" + version +
                '}';
    }
}
