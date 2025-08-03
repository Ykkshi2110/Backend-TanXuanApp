package com.peter.tanxuannewapp.config;

import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.service.AuthService;
import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Transactional
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    @Autowired
    AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String methodType = request.getMethod();
        logger.info("path: {}", path);
        logger.info("requestURI: {}", requestURI);
        logger.info("methodType: {}", methodType);

        if (JwtTokenUtil
                .getCurrentUserLogin()
                .map(email -> authService.hasPermission(email, path, methodType)).orElse(false)){
            return true;
        } else {
            throw new ResourceNotFoundException("Access Denied");
        }
    }
}
