package cn.chien.security.access;

import cn.chien.cache.CacheClient;
import cn.chien.constant.UserConstants;
import cn.chien.domain.SysUser;
import cn.chien.enums.CacheNameSpace;
import cn.chien.properties.SecurityProperties;
import cn.chien.security.common.Logins;
import cn.chien.security.exception.PasswordRetryLimitExceedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qian.diqi
 * @date 2022/7/4
 */
@Component
public class FormLoginAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String password = new String(Base64.getDecoder().decode(authentication.getCredentials().toString()));
        String username = userDetails.getUsername();

        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new BadCredentialsException("密码不在指定范围内");
        }

        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new BadCredentialsException("用户名不在指定范围内");
        }

        // 查询用户信息
        SysUser user = Logins.LOGIN_USER.get();

        if (user == null)
        {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new BadCredentialsException("用户不存在");
        }

//        if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
//        {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.delete")));
//            throw new UserDeleteException();
//        }

//        if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
//        {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.blocked", user.getRemark())));
//            throw new UserBlockedException();
//        }

        boolean matches = passwordEncoder.matches(password, userDetails.getPassword());

        check(matches, user);
    }

    private void check(boolean matches, SysUser user) throws AuthenticationException {
        String loginName = user.getLoginName();

        AtomicInteger retryCount = CacheClient.get(CacheNameSpace.LOGIN_RECORD_CACHE.namespace(), loginName, AtomicInteger.class);

        if (retryCount == null)
        {
            retryCount = new AtomicInteger(0);
            CacheClient.put(CacheNameSpace.LOGIN_RECORD_CACHE.namespace(), loginName, retryCount);
        }
        if (retryCount.incrementAndGet() > securityProperties.getUser().getPassword().getMaxRetryCount()) {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.exceed", maxRetryCount)));
            throw new PasswordRetryLimitExceedException("超过重试上限");
        }

        if (!matches) {
//            AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.LOGIN_FAIL, MessageUtils.message("user.password.retry.limit.count", retryCount)));
            CacheClient.put(CacheNameSpace.LOGIN_RECORD_CACHE.namespace(), loginName, retryCount);
            throw new BadCredentialsException("密码不匹配");
        }
        else {
            CacheClient.evictIfPresent(CacheNameSpace.LOGIN_RECORD_CACHE.namespace(), loginName);
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return userDetailsService.loadUserByUsername(username);
    }

}
