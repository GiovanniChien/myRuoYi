package cn.chien.poi.excel2java;

import cn.chien.poi.annotation.Excel;
import cn.chien.core.text.Convert;
import cn.chien.poi.DefaultExcelFieldHandler;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2FloatConverter implements ExcelType2JavaConverter<Float> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return Float.TYPE.isAssignableFrom(targetClazz) || Float.class.isAssignableFrom(targetClazz);
    }
    
    @Override
    public Float convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        return Convert.toFloat(source);
    }
}
