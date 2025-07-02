package io.github.tml.mosaic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 子插件页面重定向过滤器
 */
@Configuration
public class ChildAssetPathFilter extends OncePerRequestFilter {

    private Map<String, String> pathMap = new HashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        if(requestURI.endsWith("/index.html")){
            pathMap.put(requestURI,requestURI.split("/index.html")[0]);
        } else {
            pathMap.put(requestURI,pathMap.get(getAssetPath(req)));
        }

        // 处理子页面的静态资源
        if(pathMap.containsKey(requestURI)&&
                !requestURI.endsWith("/index.html")&&
                requestURI.contains(".")) {
            String newPath = pathMap.get(requestURI) + requestURI;
            req.getRequestDispatcher(newPath).forward(req, resp);
            return;
        }

        filterChain.doFilter(req, resp);
    }


    private String getAssetPath(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                return new URI(referer).getPath();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
