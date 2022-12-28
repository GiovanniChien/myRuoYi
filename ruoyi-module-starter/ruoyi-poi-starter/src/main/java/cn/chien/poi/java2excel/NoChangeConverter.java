package cn.chien.poi.java2excel;

import cn.chien.poi.annotation.Excel;
import cn.chien.utils.StringUtils;
import cn.chien.poi.DefaultExcelFieldHandler;

import java.lang.reflect.Field;

public class NoChangeConverter implements Java2ExcelConverter {
    
    private static final String DEFAULT_SEPARATOR = ",";
    
    @Override
    public boolean support(Class<?> sourceClass, Class<?> targetClass) {
        return targetClass.isAssignableFrom(sourceClass);
    }
    
    @Override
    public Object convert(Object source, Excel excel, Field field, DefaultExcelFieldHandler defaultExcelFieldHandler) {
        String converterExp = excel.readConverterExp();
        String dictType = excel.dictType();
        String separator = excel.separator();
        if (StringUtils.isNotEmpty(converterExp)) {
            return writeConvert(source, converterExp, separator);
        }
        // todo dict cache
//        if (StringUtils.isNotEmpty(dictType)) {
//            return
//        }
        return source;
    }
    
    private Object writeConvert(Object source, String readConverterExp, String separator) {
        if (source == null) {
            return null;
        }
        if (StringUtils.isEmpty(separator)) {
            separator = DEFAULT_SEPARATOR;
        }
        String[] split = readConverterExp.split(separator);
        for (String s : split) {
            String[] relas = s.split("=");
            if (relas[0].equals(source.toString())) {
                return relas[1];
            }
        }
        return source;
    }
    
    @Override
    public int getOrder() {
        return 3;
    }
}
