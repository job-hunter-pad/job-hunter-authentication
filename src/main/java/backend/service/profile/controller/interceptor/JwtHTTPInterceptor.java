package backend.service.profile.controller.interceptor;

import backend.service.authentication.repository.token.JwtTokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtHTTPInterceptor implements HandlerInterceptor {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenUtil jwtTokenUtil;
    private final BearerExtractor bearerExtractor;

    public JwtHTTPInterceptor(JwtTokenUtil jwtTokenUtil, BearerExtractor bearerExtractor) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.bearerExtractor = bearerExtractor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        String token = bearerExtractor.extract(header);

        if (!jwtTokenUtil.isTokenExpired(token)) {
            return true;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
