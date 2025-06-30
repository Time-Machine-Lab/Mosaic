package io.github.tml.mosaic.install.adpter.core;

import io.github.tml.mosaic.install.domian.install.InstallationItem;
import io.github.tml.mosaic.cube.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.install.collector.core.InfoCollector;
import io.github.tml.mosaic.install.domian.InfoContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 资源收集适配器
 */
@Slf4j
public abstract class AbstractResourceFileAdapter implements ResourceFileAdapter {

    private final ResourceLoader resourceLoader;

    private List<InfoCollector> collectorList;

    public AbstractResourceFileAdapter(List<InfoCollector> collectorList, ResourceLoader resourceLoader) {
        this.collectorList = collectorList;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public InfoContext adapter(InstallationItem item) {
        InfoContext infoContext = buildInfoContext(item);
        runCollector(infoContext);
        return infoContext;
    }

    protected InfoContext buildInfoContext(InstallationItem item) {
        InfoContext infoContext = new InfoContext();
        infoContext.setResourceType(resourceFileType());
        return infoContext;
    }

    protected void runCollector(InfoContext infoContext) {
        try {
            Optional.ofNullable(collectorList)
                    .orElse(Collections.emptyList())
                    .forEach(collector -> {
                        long start = System.currentTimeMillis();
                        collector.collect(infoContext);
                        long timeSpent = System.currentTimeMillis() - start;
                        log.info("Collecting info with [{}] took {} ms", collector.getClass().getSimpleName(), timeSpent);
                    });
        } catch (Exception e) {
            log.error("Error during info collection: {}", e.getMessage(), e);
        }
    }
}
