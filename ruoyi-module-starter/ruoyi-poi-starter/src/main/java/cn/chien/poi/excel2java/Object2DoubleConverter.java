package cn.chien.poi.excel2java;

import cn.chien.poi.annotation.Excel;
import cn.chien.core.text.Convert;
import cn.chien.poi.DefaultExcelFieldHandler;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2DoubleConverter implements ExcelType2JavaConverter<Double> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return Double.TYPE.isAssignableFrom(targetClazz) || Double.class.isAssignableFrom(targetClazz);
    }
    
    @Override
    public Double convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        return Convert.toDouble(source);
    }
}
