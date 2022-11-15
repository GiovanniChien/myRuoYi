package cn.chien.aspect;

import cn.chien.annotation.BusinessLog;
import cn.chien.domain.SysOperLog;
import cn.chien.domain.entity.SysUser;
import cn.chien.enums.BusinessStatus;
import cn.chien.service.ISysOperLogService;
import cn.chien.utils.AddressUtils;
import cn.chien.utils.IpUtils;
import cn.chien.utils.ServletUtils;
import cn.chien.utils.StringUtils;
import cn.chien.utils.json.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

/**
 * @author qiandq3
 * @date 2022/11/14
 */
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LogAspect {
    
    private final ISysOperLogService sysOperLogService;
    
    @AfterReturning(pointcut = "@annotation(businessLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, BusinessLog businessLog, Object jsonResult) {
        businessLog(joinPoint, businessLog, null, jsonResult);
    }
    
    @AfterThrowing(pointcut = "@annotation(businessLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, BusinessLog businessLog, Exception e) {
        businessLog(joinPoint, businessLog, e, null);
    }
    
    private void businessLog(final JoinPoint joinPoint, BusinessLog businessLog, final Exception e, Object jsonResult) {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            // 获取当前的用户
            SysUser currentUser = (SysUser) request.getSession().getAttribute("sysUser");
            
            // *========数据库日志=========*//
            SysOperLog operLog = new SysOperLog();
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = IpUtils.getIpAddr(request);
            operLog.setOperIp(ip);
            operLog.setOperUrl(StringUtils.substring(request.getRequestURI(), 0, 255));
            if (currentUser != null) {
                operLog.setOperName(currentUser.getLoginName());
                if (StringUtils.isNotNull(currentUser.getDept()) && StringUtils.isNotEmpty(
                        currentUser.getDept().getDeptName())) {
                    operLog.setDeptName(currentUser.getDept().getDeptName());
                }
            }
            
            if (e != null) {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, businessLog, operLog, jsonResult);
            operLog.setOperLocation(AddressUtils.getRealAddressByIP(ip));
            // 保存数据库
            sysOperLogService.insertOperlog(operLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }
    
    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    private void getControllerMethodDescription(JoinPoint joinPoint, BusinessLog log, SysOperLog operLog,
            Object jsonResult) throws Exception {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog);
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && StringUtils.isNotNull(jsonResult)) {
            operLog.setJsonResult(StringUtils.substring(JsonUtils.writeValueAsString(jsonResult), 0, 2000));
        }
    }
    
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog) {
        Map<String, String[]> map = ServletUtils.getRequest().getParameterMap();
        if (StringUtils.isNotEmpty(map)) {
            String params = JsonUtils.writeValueAsStringMasking(map);
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        } else {
            Object args = joinPoint.getArgs();
            if (StringUtils.isNotNull(args)) {
                String params = argsArrayToString(joinPoint.getArgs());
                operLog.setOperParam(StringUtils.substring(params, 0, 2000));
            }
        }
    }
    
    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (StringUtils.isNotNull(o) && !isFilterObject(o)) {
                    try {
                        String jsonObj = JsonUtils.writeValueAsStringMasking(o);
                        params.append(jsonObj).append(" ");
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return params.toString().trim();
    }
    
    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
    
}
