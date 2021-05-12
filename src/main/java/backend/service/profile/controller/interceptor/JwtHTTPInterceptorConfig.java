package backend.service.profile.controller.interceptor;

import backend.service.authentication.repository.token.JwtTokenUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JwtHTTPInterceptorConfig implements WebMvcConfigurer {
    private final JwtTokenUtil jwtTokenUtil;

    public JwtHTTPInterceptorConfig(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new JwtHTTPInterceptor(jwtTokenUtil))
                .addPathPatterns("/profile/*");
    }
}
