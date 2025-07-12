package com.peter.tanxuannewapp.config;

import com.peter.tanxuannewapp.domain.Role;
import com.peter.tanxuannewapp.domain.User;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.service.UserService;
import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Transactional
public class PermissionInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String methodType = request.getMethod();
        logger.info("path: {}", path);
        logger.info("requestURI: {}", requestURI);
        logger.info("methodType: {}", methodType);

        String email = JwtTokenUtil.getCurrentUserLogin().orElse(null);
        if (email != null && !email.isEmpty()) {
            User currentUser = this.userService.handleGetUserByEmail(email);
            if (currentUser != null) {
                Role role = currentUser.getRole();
                if (role != null) {
                    boolean isAllow = role.getPermissions().stream()
                            .anyMatch(permission -> permission.getRoute().equals(requestURI)
                                    && permission.getMethod().equals(methodType));
                    if (!isAllow) {
                        throw new ResourceNotFoundException("Access denied");
                    }
                } else {
                    throw new ResourceNotFoundException("Access denied");
                }
            }
        }
        return true;
    }
}
