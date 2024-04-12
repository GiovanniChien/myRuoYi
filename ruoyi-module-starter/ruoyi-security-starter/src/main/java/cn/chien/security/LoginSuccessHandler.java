package cn.chien.security;

import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author qiandq3
 * @date 2022/11/8
 */
public interface LoginSuccessHandler {
    
    void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication);
    
}
