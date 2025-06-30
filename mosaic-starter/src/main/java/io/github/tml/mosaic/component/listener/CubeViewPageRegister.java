package io.github.tml.mosaic.component.listener;

import io.github.tml.mosaic.core.event.event.CubeDefinitionRegisteredEvent;
import io.github.tml.mosaic.core.event.listener.CubeDefinitionRegisteredEventListener;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CubeViewPageRegister implements CubeDefinitionRegisteredEventListener {

    @Autowired
    private SimpleUrlHandlerMapping handlerMapping;

    @Autowired
    private ResourceHttpRequestHandler resourceHandler;


    @Override
    public void onEvent(CubeDefinitionRegisteredEvent event) {
        CubeDefinition cubeDefinition = event.getCubeDefinition();
        if (!StringUtils.hasText(cubeDefinition.getFrontPath())) {
            return;
        }
        log.info("cube {} import front view page,path:{}", cubeDefinition.getId() ,cubeDefinition.getFrontPath());
        String id = cubeDefinition.getId();
        String frontPath = "/cube/"+id+"/";

        Map<String, Object> urlMap = (Map<String, Object>) handlerMapping.getUrlMap();
        urlMap.put(frontPath+"**", resourceHandler);

        List<org.springframework.core.io.Resource> locations = resourceHandler.getLocations();
        locations.add(new FileSystemResource(getClassPathUrl(cubeDefinition)));

        cubeDefinition.setFrontPath(frontPath);
    }

    private String getClassPathUrl(CubeDefinition cubeDefinition){
        String frontPath = cubeDefinition.getFrontPath();
        if (frontPath.startsWith("classPath:")) {
            return frontPath.split("classPath:")[1];
        }else{
            return frontPath;
        }
    }

    @Override
    public int getPriority() {
        return CubeDefinitionRegisteredEventListener.super.getPriority();
    }

    @Override
    public boolean isAsyncSupported() {
        return true;
    }

    @Override
    public String getListenerName() {
        return "CubeViewPageRegister";
    }
}
