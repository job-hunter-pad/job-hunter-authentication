package backend.service.profile.controller.interceptor;

import backend.service.authentication.repository.token.JwtTokenUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JwtHTTPInterceptorConfig implements WebMvcConfigurer {
    private final JwtTokenUtil jwtTokenUtil;
    private final BearerExtractor bearerExtractor;

    public JwtHTTPInterceptorConfig(JwtTokenUtil jwtTokenUtil, BearerExtractor bearerExtractor) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.bearerExtractor = bearerExtractor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(new JwtHTTPInterceptor(jwtTokenUtil, bearerExtractor))
                .addPathPatterns("/profile/*");
    }
}
