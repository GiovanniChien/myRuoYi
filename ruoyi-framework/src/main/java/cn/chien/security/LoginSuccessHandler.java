package cn.chien.security;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author qiandq3
 * @date 2022/11/8
 */
public interface LoginSuccessHandler {
    
    void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication);
    
}
