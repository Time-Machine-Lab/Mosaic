package io.github.tml.mosaic.entity.dto;

import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.entity.vo.cube.CubeStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CubeDTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CubeDTO extends CubeDefinition {

    private CubeStatus status;
    private LocalDateTime registeredTime;
    private LocalDateTime lastUpdatedTime;

    public CubeDTO(String id, String name, String version, 
                   String description, String model, String scope,
                   String className, ClassLoader classLoader) {
        super(id, name, version, description, model, scope, className, classLoader);
        status = CubeStatus.INACTIVE;
        this.registeredTime = LocalDateTime.now();
        this.lastUpdatedTime = LocalDateTime.now();
    }

    /**
     * 判断Cube是否配置完整
     */
    public boolean isConfigComplete() {
        return Objects.nonNull(this.getId()) &&
               Objects.nonNull(this.getName()) &&
               Objects.nonNull(this.getClassName()) &&
               !this.getExtensionPackages().isEmpty();
    }

    /**
     * 获取扩展点总数
     */
    public int getTotalExtensionPointCount() {
        return this.getExtensionPackages().stream()
                .mapToInt(pkg -> pkg.getExtensionPoints().size())
                .sum();
    }
}