package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.core.text.Convert;

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
