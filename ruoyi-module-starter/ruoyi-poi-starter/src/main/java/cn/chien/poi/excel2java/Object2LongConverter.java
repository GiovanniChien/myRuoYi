package cn.chien.poi.excel2java;

import cn.chien.poi.annotation.Excel;
import cn.chien.core.text.Convert;
import cn.chien.poi.DefaultExcelFieldHandler;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2LongConverter implements ExcelType2JavaConverter<Long> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return Long.TYPE.isAssignableFrom(targetClazz) || Long.class.isAssignableFrom(targetClazz);
    }
    
    @Override
    public Long convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        return Convert.toLong(source);
    }
}
