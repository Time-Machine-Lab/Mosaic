package io.github.tml.mosaic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 子插件页面重定向过滤器
 */
@Configuration
public class ChildAssetPathFilter extends OncePerRequestFilter {

    private static final Set<String> ASSET_PATH_SET = new TreeSet<>();

    static {
        ASSET_PATH_SET.add("/mosaic");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = req.getRequestURI();

        // 处理子页面的静态资源
        String assetPath = null;
        if(Objects.nonNull(assetPath = getAssetPath(req)) && requestURI.startsWith("/assets/")) {
            String newPath = assetPath + requestURI;
            req.getRequestDispatcher(newPath).forward(req, resp);
            return;
        }

        filterChain.doFilter(req, resp);
    }


    private String getAssetPath(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            for (String assetPath : ASSET_PATH_SET) {
                if (referer.contains(assetPath)) {
                    return assetPath;
                }
            }
        }
        return null;
    }

    public static void addAssetResource(String assetPath){
        ASSET_PATH_SET.add(assetPath);
    }
}
