package cn.chien.security.access;

import cn.chien.core.domain.AjaxResult;
import cn.chien.security.LoginSuccessHandler;
import cn.chien.security.common.Logins;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author qian.diqi
 * @date 2022/7/7
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private final CsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        invokeLoginSuccessHandler(request, response, authentication);
        writeResponse(request, response);
    }
    
    private void invokeLoginSuccessHandler(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        ServiceLoader<LoginSuccessHandler> handlers = ServiceLoader.load(LoginSuccessHandler.class);
        for (LoginSuccessHandler handler : handlers) {
            handler.onLoginSuccess(request, response, authentication);
        }
    }
    
    private void writeResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> resp = new HashMap<>();
        resp.put("user", Logins.LOGIN_USER.get());
        CsrfToken csrfToken = repository.generateToken(request);
        repository.saveToken(csrfToken, request, response);
        resp.put("_csrf", csrfToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter pw = response.getWriter();
        objectMapper.writeValue(pw, AjaxResult.success(resp));
        pw.flush();
        pw.close();
    }

}
