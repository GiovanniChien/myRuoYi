package cn.chien.security.access;

import cn.chien.constant.Constants;
import cn.chien.core.domain.AjaxResult;
import cn.chien.domain.SysUser;
import cn.chien.security.common.Logins;
import cn.chien.service.ISysLoginInfoService;
import cn.chien.service.ISysUserService;
import cn.chien.utils.DateUtils;
import cn.chien.utils.IpUtils;
import cn.chien.utils.MessageUtils;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qian.diqi
 * @date 2022/7/7
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FormLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ISysUserService sysUserService;
    
    private final ISysLoginInfoService sysLoginInfoService;
    
    private final CsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        recordLoginInfo(request, Logins.LOGIN_USER.get());
        Map<String, Object> resp = new HashMap<>();
        resp.put("user", Logins.LOGIN_USER.get());
        CsrfToken csrfToken = repository.generateToken(request);
        repository.saveToken(csrfToken, request, response);
        resp.put("_csrf", csrfToken);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter pw = response.getWriter();
        objectMapper.writeValue(pw, AjaxResult.success(resp));
        pw.flush();
        pw.close();
//        response.sendRedirect("/");
    }

    private void recordLoginInfo(HttpServletRequest request, SysUser sysUser) {
        SysUser user = new SysUser();
        user.setUserId(sysUser.getUserId());
        user.setLoginIp(IpUtils.getIpAddr(request));
        user.setLoginDate(DateUtils.getNowDate());
        sysUserService.updateUserInfo(user);
        sysLoginInfoService.recordLoginInfo(sysUser.getLoginName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"), request);
    }

}
