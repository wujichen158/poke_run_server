package com.qdu.pokerun_server.api;

import com.qdu.pokerun_server.api.interceptor.ParamCheckInterceptor;
import com.qdu.pokerun_server.api.interceptor.PermissionCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private PermissionCheckInterceptor permissionCheckInterceptor;
    @Autowired
    private ParamCheckInterceptor paramCheckInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(permissionCheckInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(paramCheckInterceptor).addPathPatterns("/api/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**").allowedOrigins().allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(1800)
                .allowedOrigins("http://123.57.62.185:8080")
                .allowCredentials(true);
    }
}
