package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.core.text.Convert;
import cn.chien.utils.DateUtils;
import cn.chien.utils.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author qiandq3
 * @date 2022/12/9
 */
public class Object2StringConverter implements ExcelType2JavaConverter<String> {
    
    @Override
    public boolean support(Class<?> targetClazz) {
        return String.class.isAssignableFrom(targetClazz);
    }
    
    @Override
    public String convert(Object source, Excel excel, Class<?> targetType,
            DefaultExcelFieldHandler defaultExcelFieldHandler) {
        String s = Convert.toStr(source);
        if (StringUtils.endsWith(s, ".0")) {
            s = StringUtils.substringBefore(s, ".0");
        } else {
            String dateFormat = excel.dateFormat();
            if (StringUtils.isNotEmpty(dateFormat)) {
                s = parseDateToStr(dateFormat, source);
            }
        }
        return s;
    }
    
    /**
     * 格式化不同类型的日期对象
     *
     * @param dateFormat 日期格式
     * @param val        被格式化的日期对象
     * @return 格式化后的日期字符
     */
    public String parseDateToStr(String dateFormat, Object val) {
        if (val == null) {
            return "";
        }
        String str;
        if (val instanceof Date) {
            str = DateUtils.parseDateToStr(dateFormat, (Date) val);
        } else if (val instanceof LocalDateTime) {
            str = DateUtils.parseDateToStr(dateFormat, DateUtils.toDate((LocalDateTime) val));
        } else if (val instanceof LocalDate) {
            str = DateUtils.parseDateToStr(dateFormat, DateUtils.toDate((LocalDate) val));
        } else {
            str = val.toString();
        }
        return str;
    }
}
