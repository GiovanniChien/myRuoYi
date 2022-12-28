package cn.chien.poi.java2excel;

import cn.chien.poi.annotation.Excel;
import cn.chien.utils.DateUtils;
import cn.chien.utils.StringUtils;
import cn.chien.poi.DefaultExcelFieldHandler;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Data2StringConverter implements Java2ExcelConverter {
    
    @Override
    public boolean support(Class<?> sourceClass, Class<?> targetClass) {
        return String.class.isAssignableFrom(targetClass) && (Date.class.isAssignableFrom(sourceClass)
                || LocalDateTime.class.isAssignableFrom(sourceClass) || LocalDate.class.isAssignableFrom(sourceClass));
    }
    
    @Override
    public Object convert(Object source, Excel excel, Field field, DefaultExcelFieldHandler defaultExcelFieldHandler) {
        if (source == null) {
            return null;
        }
        String dateFormat = excel.dateFormat();
        if (StringUtils.isEmpty(dateFormat)) {
            return source.toString();
        }
        if (source instanceof Date date) {
            return DateUtils.parseDateToStr(dateFormat, date);
        }
        if (source instanceof LocalDateTime localDateTime) {
            return DateUtils.parseDateToStr(dateFormat, DateUtils.toDate(localDateTime));
        }
        if (source instanceof LocalDate localDate) {
            return DateUtils.parseDateToStr(dateFormat, DateUtils.toDate(localDate));
        }
        return source.toString();
    }
    
    @Override
    public int getOrder() {
        return 1;
    }
}
