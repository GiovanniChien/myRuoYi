package cn.chien.poi;

import cn.chien.poi.annotation.Excel;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
public class ExcelField {
    
    private Field field;
    
    private Excel excelAnnotation;
    
    private List<ExcelField> subField;
    
    public ExcelField() {
    }
    
    public ExcelField(Field field, Excel excelAnnotation) {
        this.field = field;
        this.excelAnnotation = excelAnnotation;
    }
    
    public Field getField() {
        return field;
    }
    
    public void setField(Field field) {
        this.field = field;
    }
    
    public Excel getExcelAnnotation() {
        return excelAnnotation;
    }
    
    public void setExcelAnnotation(Excel excelAnnotation) {
        this.excelAnnotation = excelAnnotation;
    }
    
    public List<ExcelField> getSubField() {
        return subField;
    }
    
    public void setSubField(List<ExcelField> subField) {
        this.subField = subField;
    }
}
