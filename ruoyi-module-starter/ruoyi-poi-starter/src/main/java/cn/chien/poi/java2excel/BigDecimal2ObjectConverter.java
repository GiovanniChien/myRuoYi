package cn.chien.poi.java2excel;

import cn.chien.poi.annotation.Excel;
import cn.chien.poi.DefaultExcelFieldHandler;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimal2ObjectConverter implements Java2ExcelConverter {
    
    @Override
    public boolean support(Class<?> sourceClass, Class<?> targetClass) {
        return BigDecimal.class.isAssignableFrom(sourceClass);
    }
    
    @Override
    public Object convert(Object source, Excel excel, Field field, DefaultExcelFieldHandler defaultExcelFieldHandler) {
        int scale = excel.scale();
        RoundingMode roundingMode = excel.roundingMode();
        return (((BigDecimal) source).setScale(scale, roundingMode)).doubleValue();
    }
    
    @Override
    public int getOrder() {
        return 0;
    }
}
