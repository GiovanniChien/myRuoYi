package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.core.text.Convert;

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
