package com.app.config;

import com.app.session.UserSessionBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class GlobalInterceptor implements HandlerInterceptor {

    private final UserSessionBean userSessionBean;

    public GlobalInterceptor(UserSessionBean userSessionBean) {
        this.userSessionBean = userSessionBean;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            if ((path.equals("/") || path.startsWith("/login")) && userSessionBean.isLoggedIn()) {
                response.sendRedirect("/home");
                return false;
            }
            return true;
        }

        // logout-confirm requires authentication
        if (path.startsWith("/logout-confirm")) {
            if (!userSessionBean.isLoggedIn()) {
                response.sendRedirect("/login");
                return false;
            }
            return true;
        }

        // All other paths require authentication
        if (!userSessionBean.isLoggedIn()) {
            response.sendRedirect("/login");
            return false;
        }

//        if (userSessionBean.getUser().isBanned()) {
//            response.sendRedirect("/banned");
//            return false;
//        }

        return true;
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/login") ||
                path.startsWith("/oauth2") ||
                path.startsWith("/css") ||
                path.startsWith("/js") ||
                path.startsWith("/banned") ||
                path.equals("/");
    }
}
