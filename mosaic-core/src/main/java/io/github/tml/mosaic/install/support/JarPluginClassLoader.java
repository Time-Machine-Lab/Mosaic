package io.github.tml.mosaic.install.support;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.infrastructure.CommonComponent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * JAR Plugin Class Loader
 * Loads classes from a specific JAR file with unique loader identification.
 * Author: suifeng
 * Date: 2025/6/3
 */
@Slf4j
@Data
public class JarPluginClassLoader extends URLClassLoader {

    private final GUID loaderId;             // Unique loader ID
    private final String jarPath;            // Path to the JAR file
    private final Map<String, Class<?>> loadedClasses; // Cache for loaded classes
    private volatile boolean closed = false; // Loader closed flag

    public JarPluginClassLoader(String jarPath, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.loaderId = CommonComponent.GuidAllocator().nextGUID();
        this.jarPath = jarPath;
        this.loadedClasses = new ConcurrentHashMap<>();
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            loadedClasses.clear();
            try {
                super.close();
                System.out.println("JAR class loader closed: " + jarPath);
            } catch (Exception e) {
                System.err.println("Failed to close class loader: " + e.getMessage());
            }
        }
    }
}