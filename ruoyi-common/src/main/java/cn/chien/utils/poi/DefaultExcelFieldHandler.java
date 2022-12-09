package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
@Slf4j
public final class DefaultExcelFieldHandler implements ExcelHandlerAdapter {
    
    private ServiceLoader<ExcelType2JavaConverter> converters;
    
    public DefaultExcelFieldHandler() { }
    
    public void setConverters(ServiceLoader<ExcelType2JavaConverter> converters) {
        this.converters = converters;
    }
    
    @Override
    public Object format(Object value, Class<?> targetType, Excel excel) {
        for (ExcelType2JavaConverter<?> converter : converters) {
            if (converter.support(targetType)) {
                return converter.convert(value, excel, targetType, this);
            }
        }
        return value;
    }
    
}
