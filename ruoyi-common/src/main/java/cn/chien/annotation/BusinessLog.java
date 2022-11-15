package cn.chien.annotation;

import cn.chien.enums.BusinessType;
import cn.chien.enums.OperatorType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiandq3
 * @date 2022/11/14
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessLog {
    
    /**
     * 模块
     */
    public String title() default "";
    
    /**
     * 功能
     */
    public BusinessType businessType() default BusinessType.OTHER;
    
    /**
     * 操作人类别
     */
    public OperatorType operatorType() default OperatorType.MANAGE;
    
    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;
    
    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;
    
}
