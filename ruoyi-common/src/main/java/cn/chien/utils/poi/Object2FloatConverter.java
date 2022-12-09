package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.core.text.Convert;

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
