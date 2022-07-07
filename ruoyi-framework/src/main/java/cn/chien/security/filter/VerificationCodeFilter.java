package cn.chien.security.filter;

import cn.chien.constant.CaptchaConstant;
import cn.chien.properties.SecurityProperties;
import cn.chien.security.exception.ExceptionPublisher;
import cn.chien.security.exception.VerificationCodeException;
import cn.chien.utils.spring.SpringUtils;
import cn.hutool.captcha.generator.CodeGenerator;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author qian.diqi
 * @date 2022/7/6
 */
public class VerificationCodeFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties = SpringUtils.getBean(SecurityProperties.class);

    private final CodeGenerator codeGenerator = SpringUtils.getBean(CodeGenerator.class);

    private final ExceptionPublisher exceptionPublisher = SpringUtils.getBean(ExceptionPublisher.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!securityProperties.getUser().getCaptchaEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!HttpMethod.POST.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String vCode = request.getParameter("validateCode");
        HttpSession session = request.getSession(false);
        try {
            if (session == null) {
                throw new VerificationCodeException("验证码错误");
            }
            if (vCode == null) {
                throw new VerificationCodeException("验证码错误");
            }
            String sessionCaptchaKey = (String) session.getAttribute(CaptchaConstant.CAPTCHA_SESSION_KEY);
            if (sessionCaptchaKey == null) {
                throw new VerificationCodeException("验证码错误");
            }
            if (!codeGenerator.verify(sessionCaptchaKey, vCode)) {
                throw new VerificationCodeException("验证码错误");
            }
        }
        catch (VerificationCodeException e) {
            exceptionPublisher.process(e, new HttpRequestResponseHolder(request, response), HttpStatus.OK);
            return;
        }
        filterChain.doFilter(request, response);
    }

}
