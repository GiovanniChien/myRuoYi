package cn.chien.poi.java2excel;

import cn.chien.poi.annotation.Excel;
import cn.chien.utils.StringUtils;
import cn.chien.poi.DefaultExcelFieldHandler;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class List2ObjectConverter implements Java2ExcelConverter {
    
    private static final String DEFAULT_DELIMITER = ",";
    
    private Class<?> targetClass;
    
    @Override
    public boolean support(Class<?> sourceClass, Class<?> targetClass) {
        if (List.class.isAssignableFrom(sourceClass) || sourceClass.isArray()) {
            this.targetClass = targetClass;
            return true;
        }
        return false;
    }
    
    @Override
    public Object convert(Object source, Excel excel, Field field, DefaultExcelFieldHandler defaultExcelFieldHandler) {
        if (source instanceof List<?> list) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            Class<?> actualType = (Class<?>) genericType.getActualTypeArguments()[0];
            if (!excel.requireMerge() || StringUtils.isNotEmpty(excel.separator())) {
                // 数组转换为 分隔符 分割的字符串
                String delimiter = StringUtils.isEmpty(excel.separator()) ? DEFAULT_DELIMITER : excel.separator();
                return StringUtils.join(list, delimiter);
            }
            List<Object> result = new ArrayList<>();
            for (Object sub : list) {
                result.add(defaultExcelFieldHandler.java2ExcelFormat(sub, actualType, targetClass, excel, field));
            }
            return result;
        }
        Class<?> actualType = source.getClass().getComponentType();
        Object[] objs = convertToArray(source);
        if (!excel.requireMerge() || StringUtils.isNotEmpty(excel.separator())) {
            // 数组转换为 分隔符 分割的字符串
            String delimiter = StringUtils.isEmpty(excel.separator()) ? DEFAULT_DELIMITER : excel.separator();
            return StringUtils.join(objs, delimiter);
        }
        List<Object> result = new ArrayList<>();
        for (Object sub : objs) {
            result.add(defaultExcelFieldHandler.java2ExcelFormat(sub, actualType, targetClass, excel, field));
        }
        return result;
    }
    
    private Object[] convertToArray(Object obj) {
        int length = Array.getLength(obj);
        Object[] objs = new Object[length];
        for (int i = 0; i < length; i++) {
            objs[i] = Array.get(obj, i);
        }
        return objs;
    }
    
    @Override
    public int getOrder() {
        return 2;
    }
}
