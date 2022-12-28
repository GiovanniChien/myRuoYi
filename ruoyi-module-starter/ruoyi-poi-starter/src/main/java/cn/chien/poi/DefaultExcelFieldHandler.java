package cn.chien.poi;

import cn.chien.poi.annotation.Excel;
import cn.chien.poi.excel2java.ExcelType2JavaConverter;
import cn.chien.poi.java2excel.Java2ExcelConverter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
@Slf4j
public final class DefaultExcelFieldHandler implements ExcelHandlerAdapter {
    
    private ServiceLoader<ExcelType2JavaConverter> excel2JavaConvert;
    
    private List<Java2ExcelConverter> java2ExcelConverter;
    
    public DefaultExcelFieldHandler() { }
    
    public void setExcel2JavaConvert(ServiceLoader<ExcelType2JavaConverter> excel2JavaConvert) {
        this.excel2JavaConvert = excel2JavaConvert;
    }
    
    public void setJava2ExcelConverter(List<Java2ExcelConverter> java2ExcelConverter) {
        this.java2ExcelConverter = java2ExcelConverter;
    }
    
    @Override
    public Object excel2JavaFormat(Object value, Class<?> targetType, Excel excel) {
        for (ExcelType2JavaConverter<?> converter : excel2JavaConvert) {
            if (converter.support(targetType)) {
                return converter.convert(value, excel, targetType, this);
            }
        }
        return value;
    }
    
    @Override
    public Object java2ExcelFormat(Object value, Class<?> sourceType, Class<?> targetType, Excel excel, Field field) {
        for (Java2ExcelConverter converter : java2ExcelConverter) {
            if (converter.support(sourceType, targetType)) {
                return converter.convert(value, excel, field, this);
            }
        }
        return value;
    }
}
