package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.core.text.Convert;

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
