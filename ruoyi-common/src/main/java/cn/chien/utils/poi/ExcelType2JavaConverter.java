package cn.chien.utils.poi;

import cn.chien.annotation.Excel;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
public interface ExcelType2JavaConverter<T> {
    
    boolean support(Class<?> targetClazz);
    
    T convert(Object source, Excel excel, Class<?> targetType, DefaultExcelFieldHandler defaultExcelFieldHandler);

}
