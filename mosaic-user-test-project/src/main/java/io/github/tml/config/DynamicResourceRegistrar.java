package io.github.tml.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class DynamicResourceRegistrar {

    @Autowired
    private ApplicationContext context;

    private final Map<String, ResourceHandlerRegistration> registrations = new ConcurrentHashMap<>();

    public void registerPluginResources(String pluginName) {
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(
                context,
                context.getBean(ServletContext.class),
                context.getBean(ContentNegotiationManager.class)
        );
        String resourcePath = "/" + pluginName + "/**";
        String resourceLocation = "classpath:/io/github/tml/plugin/" + pluginName + "/view/";

        ResourceHandlerRegistration registration = registry.addResourceHandler(resourcePath)
                .addResourceLocations(resourceLocation);

        // 保存注册引用
        registrations.put(pluginName, registration);

        // 刷新资源处理器
        refreshResourceHandlers();
    }

    private void refreshResourceHandlers() {
        // 实现同前，刷新HandlerMapping
    }
}