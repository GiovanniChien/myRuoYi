package cn.chien.poi.java2excel;

import cn.chien.poi.annotation.Excel;
import cn.chien.poi.DefaultExcelFieldHandler;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;

public interface Java2ExcelConverter extends Ordered {
    
    boolean support(Class<?> sourceClass, Class<?> targetClass);
    
    Object convert(Object source, Excel excel, Field field, DefaultExcelFieldHandler defaultExcelFieldHandler);
    
}
