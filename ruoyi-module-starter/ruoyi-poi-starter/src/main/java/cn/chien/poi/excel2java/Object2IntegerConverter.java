package cn.chien.poi.excel2java;

import cn.chien.poi.annotation.Excel;
import cn.chien.core.text.Convert;
import cn.chien.poi.DefaultExcelFieldHandler;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2IntegerConverter implements ExcelType2JavaConverter<Integer> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return Integer.TYPE.isAssignableFrom(targetClazz) || Integer.class.isAssignableFrom(targetClazz);
    }
    
    @Override
    public Integer convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        return Convert.toInt(source);
    }
}
