package cn.chien.poi.excel2java;

import cn.chien.poi.annotation.Excel;
import cn.chien.poi.DefaultExcelFieldHandler;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
public interface ExcelType2JavaConverter<T> {
    
    boolean support(Class<?> targetClazz);
    
    T convert(Object source, Excel excel, Class<?> targetType, DefaultExcelFieldHandler defaultExcelFieldHandler);

}
