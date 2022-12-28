package cn.chien.poi.excel2java;

import cn.chien.poi.annotation.Excel;
import cn.chien.core.text.Convert;
import cn.chien.poi.DefaultExcelFieldHandler;

import java.math.BigDecimal;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2BigDecimalConverter implements ExcelType2JavaConverter<BigDecimal> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return BigDecimal.class.isAssignableFrom(targetClazz);
    }
    
    @Override
    public BigDecimal convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        return Convert.toBigDecimal(source);
    }
}
