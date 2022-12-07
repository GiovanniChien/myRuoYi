package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import cn.chien.annotation.Excels;
import cn.chien.utils.ReflectionUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.style.AbstractCellStyleStrategy;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author qiandq3
 * @date 2022/12/6
 */
public class ExcelUtil<T> {
    
    private final Class<T> clazz;
    
    private List<ExcelField> excelFieldList;
    
    private Excel.Type type;
    
    private List<List<Excel>> headExcelAnnotations;
    
    private int maxHeight;
    
    public ExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    public void init(Excel.Type type) {
        this.type = type;
        this.excelFieldList = initExcelField(clazz);
        this.excelFieldList = this.excelFieldList == null ? null : this.excelFieldList.stream()
                .sorted(Comparator.comparing(excelField -> excelField.getExcelAnnotation().sort()))
                .collect(Collectors.toList());
        this.headExcelAnnotations = getHeadExcelAnnotations(this.excelFieldList);
    }
    
    private List<ExcelField> initExcelField(Class<?> clazz) {
        if (ReflectionUtils.isPrimitive(clazz)) {
            return null;
        }
        List<ExcelField> result = new ArrayList<>();
        ReflectionUtils.doWithFields(clazz, field -> {
            if (field.isAnnotationPresent(Excel.class)) {
                Excel attr = field.getAnnotation(Excel.class);
                maxHeight = Math.max(maxHeight, attr.height());
                if (Excel.Type.ALL.equals(attr.type()) || this.type.equals(attr.type())) {
                    field.setAccessible(true);
                    ExcelField excelField = new ExcelField(field, attr);
                    Class<?> fieldType = field.getType();
                    if (fieldType.isArray()) {
                        excelField.setSubField(initExcelField(fieldType.getComponentType()));
                    } else if (Collection.class.isAssignableFrom(fieldType)) {
                        ParameterizedType pt = (ParameterizedType) field.getGenericType();
                        Class<?> subClass = (Class<?>) pt.getActualTypeArguments()[0];
                        excelField.setSubField(initExcelField(subClass));
                    } else {
                        excelField.setSubField(initExcelField(fieldType));
                    }
                    result.add(excelField);
                }
            } else if (field.isAnnotationPresent(Excels.class)) {
                Excels attrs = field.getAnnotation(Excels.class);
                Excel[] excels = attrs.value();
                for (Excel attr : excels) {
                    maxHeight = Math.max(maxHeight, attr.height());
                    if (Excel.Type.ALL.equals(attr.type()) || this.type.equals(attr.type())) {
                        field.setAccessible(true);
                        result.add(new ExcelField(field, attr));
                    }
                }
            }
        });
        return result.isEmpty() ? null : result;
    }
    
    private List<List<String>> getHeader() {
        List<List<String>> headers = new ArrayList<>();
        for (List<Excel> excelAnnotations : this.headExcelAnnotations) {
            List<String> list = new ArrayList<>();
            for (Excel excel : excelAnnotations) {
                list.add(excel.name());
            }
            headers.add(list);
        }
        return headers;
    }
    
    private List<List<Excel>> getHeadExcelAnnotations(List<ExcelField> excelFieldList) {
        if (CollectionUtils.isEmpty(excelFieldList)) {
            return null;
        }
        List<List<Excel>> headers = new ArrayList<>();
        for (ExcelField excelField : excelFieldList) {
            Excel excelAnnotation = excelField.getExcelAnnotation();
            List<List<Excel>> subHeader = getHeadExcelAnnotations(excelField.getSubField());
            if (CollectionUtils.isNotEmpty(subHeader)) {
                for (List<Excel> sub : subHeader) {
                    List<Excel> header = new ArrayList<>();
                    header.add(excelAnnotation);
                    header.addAll(sub);
                    headers.add(header);
                }
            } else {
                List<Excel> header = new ArrayList<>();
                header.add(excelAnnotation);
                headers.add(header);
            }
        }
        return headers;
    }
    
    public void importTemplateExcel(String sheetName, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = encodingFilename(sheetName);
        String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodeFileName);
        importTemplateExcel(sheetName, response.getOutputStream());
    }
    
    public void importTemplateExcel(String sheetName, OutputStream outputStream) {
        init(Excel.Type.IMPORT);
        EasyExcel.write(outputStream)
                .head(getHeader())
                .useDefaultStyle(false)
                .automaticMergeHead(true)
                .sheet(sheetName)
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) this.maxHeight, (short) this.maxHeight))
                .registerWriteHandler(new HeaderWidthStyleStrategy(this.headExcelAnnotations))
                .registerWriteHandler(new CellStyleStrategy(this.headExcelAnnotations))
                .doWrite(new ArrayList<>());
    }
    
    public String encodingFilename(String filename) {
        filename = UUID.randomUUID() + "_" + filename + ".xlsx";
        return filename;
    }
    
    
    private static class HeaderWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {
    
        private final List<List<Excel>> headExcelAnnotations;
        
        public HeaderWidthStyleStrategy(List<List<Excel>> headExcelAnnotations) {
            this.headExcelAnnotations = headExcelAnnotations;
        }
    
        @Override
        protected void setColumnWidth(CellWriteHandlerContext context) {
            Sheet sheet = context.getWriteSheetHolder().getSheet();
            int colIndex = context.getCell().getColumnIndex();
            List<Excel> excels = headExcelAnnotations.get(colIndex);
            double maxWidth = 0;
            for (Excel excel : excels) {
                maxWidth = Math.max(maxWidth, excel.width());
            }
            sheet.setColumnWidth(colIndex, (int) ((maxWidth + 0.72) * 256));
        }
    }
    
    private static class CellStyleStrategy extends AbstractCellStyleStrategy {
        private final List<List<Excel>> headExcelAnnotations;
    
        public CellStyleStrategy(List<List<Excel>> headExcelAnnotations) {
            this.headExcelAnnotations = headExcelAnnotations;
        }
    
        @Override
        protected void setHeadCellStyle(CellWriteHandlerContext context) {
            int colIndex = context.getCell().getColumnIndex();
            List<Excel> excels = headExcelAnnotations.get(colIndex);
            Excel attr = excels.get(excels.size() - 1);
            CellStyle cellStyle = context.getWriteWorkbookHolder().getWorkbook().createCellStyle();
            Font titleFont = context.getWriteWorkbookHolder().getWorkbook().createFont();
            titleFont.setFontName("Arial");
            titleFont.setFontHeightInPoints((short) 10);
            titleFont.setBold(true);
            titleFont.setColor(attr.headerColor().getIndex());
            cellStyle.setFont(titleFont);
            cellStyle.setFillForegroundColor(attr.headerBackgroundColor().getIndex());
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setAlignment(attr.align());
            context.getCell().setCellStyle(cellStyle);
        }
    
        @Override
        protected void setContentCellStyle(CellWriteHandlerContext context) {
            int colIndex = context.getCell().getColumnIndex();
            List<Excel> excels = headExcelAnnotations.get(colIndex);
            Excel attr = excels.get(excels.size() - 1);
            CellStyle style = context.getWriteWorkbookHolder().getWorkbook().createCellStyle();
            style.setAlignment(attr.align());
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderRight(BorderStyle.THIN);
            style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setBorderLeft(BorderStyle.THIN);
            style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setBorderTop(BorderStyle.THIN);
            style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setBorderBottom(BorderStyle.THIN);
            style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(attr.backgroundColor().getIndex());
            Font dataFont = context.getWriteWorkbookHolder().getWorkbook().createFont();
            dataFont.setFontName("Arial");
            dataFont.setFontHeightInPoints((short) 10);
            dataFont.setColor(attr.color().getIndex());
            style.setFont(dataFont);
            context.getCell().setCellStyle(style);
        }
    }
    
    private static class ExcelField {
        
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
    
}