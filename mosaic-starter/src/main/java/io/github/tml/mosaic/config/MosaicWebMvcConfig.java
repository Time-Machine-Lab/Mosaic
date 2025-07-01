package io.github.tml.mosaic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * mosaic框架页面配置
 */
@Configuration
public class MosaicWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/mosaic/**")
                .addResourceLocations("classpath:/view/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        System.out.println(resourcePath);
                        // 1. 处理静态资源文件请求
                        if (resourcePath.startsWith("static/") ||
                                resourcePath.endsWith(".js") ||
                                resourcePath.endsWith(".css") ||
                                resourcePath.endsWith(".png") ||
                                resourcePath.endsWith(".jpg") ||
                                resourcePath.endsWith(".ico")) {

                            if (requestedResource.exists() && requestedResource.isReadable()) {
                                return requestedResource;
                            }
                            return null; // 返回404而不是index.html
                        }
                        // 2. 处理API请求（如果有）
                        if (resourcePath.startsWith("mosaic/")) {
                            return null; // 让Controller处理API请求
                        }
                        // 3. 其他请求返回index.html（前端路由）
                        return location.createRelative("index.html");
                    }
                });

    }


}
