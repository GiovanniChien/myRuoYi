package cn.chien.poi.java2excel;

import cn.chien.poi.annotation.Excel;
import cn.chien.utils.ReflectionUtils;
import cn.chien.utils.StringUtils;
import cn.chien.poi.DefaultExcelFieldHandler;

import java.lang.reflect.Field;
import java.util.Collection;

public class Pojo2ObjectConverter implements Java2ExcelConverter {
    
    @Override
    public boolean support(Class<?> sourceClass, Class<?> targetClass) {
        return !ReflectionUtils.isPrimitive(sourceClass) && !sourceClass.isArray()
                && !Collection.class.isAssignableFrom(sourceClass);
    }
    
    @Override
    public Object convert(Object source, Excel excel, Field field, DefaultExcelFieldHandler defaultExcelFieldHandler) {
        if (StringUtils.isEmpty(excel.targetAttr())) {
            return source.toString();
        }
        String targetAttr = excel.targetAttr();
        String[] attrs = targetAttr.split("\\.");
        Class<?> clazz = field.getType();
        Object val = source;
        for (String attr : attrs) {
            Field subField = ReflectionUtils.findField(clazz, attr);
            subField.setAccessible(true);
            try {
                val = subField.get(val);
                clazz = subField.getClass();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return val;
    }
    
    @Override
    public int getOrder() {
        return 4;
    }
}
