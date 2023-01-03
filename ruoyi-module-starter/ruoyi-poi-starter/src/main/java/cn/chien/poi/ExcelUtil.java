package cn.chien.poi;

import cn.chien.poi.annotation.Excel;
import cn.chien.poi.annotation.ExcelClass;
import cn.chien.poi.annotation.Excels;
import cn.chien.poi.java2excel.Java2ExcelConverter;
import cn.chien.utils.ReflectionUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author qiandq3
 * @date 2022/12/6
 */
public class ExcelUtil<T> {
    
    private final Class<T> clazz;
    
    private final ExcelClass excelClass;
    
    private List<ExcelField> excelFieldList;
    
    private Excel.Type type;
    
    private List<List<Excel>> headExcelAnnotations;
    
    private int maxHeight;
    
    private List<Java2ExcelConverter> converters;
    
    public ExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
        this.excelClass = clazz.getDeclaredAnnotation(ExcelClass.class);
    }
    
    public void init(Excel.Type type) {
        this.type = type;
        this.excelFieldList = initExcelField(clazz);
        this.excelFieldList = this.excelFieldList == null ? null : this.excelFieldList.stream()
                .sorted(Comparator.comparing(excelField -> excelField.getExcelAnnotation().sort()))
                .collect(Collectors.toList());
        this.headExcelAnnotations = getHeadExcelAnnotations(this.excelFieldList);
        ServiceLoader<Java2ExcelConverter> java2ExcelConverters = ServiceLoader.load(Java2ExcelConverter.class);
        this.converters = java2ExcelConverters.stream().sorted(Comparator.comparing(converter -> converter.get().getOrder())).map(
                ServiceLoader.Provider::get).collect(Collectors.toList());
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
                .registerWriteHandler(new DataValidationWriterStrategy(this.headExcelAnnotations))
                .doWrite(new ArrayList<>());
    }
    
    public void export(String sheetName, HttpServletResponse response, List<T> data)
            throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = encodingFilename(sheetName);
        String encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodeFileName);
        response.setHeader("ajax-filename", encodeFileName);
        export(sheetName, response.getOutputStream(), data);
    }
    
    public void export(String sheetName, OutputStream outputStream, List<T> data)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        init(Excel.Type.EXPORT);
        List<List<Object>> exportData = prepareExportData(data);
        EasyExcel.write(outputStream)
                .head(getHeader())
                .useDefaultStyle(false)
                .automaticMergeHead(true)
                .sheet(sheetName)
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) this.maxHeight, (short) this.maxHeight))
                .registerWriteHandler(new HeaderWidthStyleStrategy(this.headExcelAnnotations))
                .registerWriteHandler(new CellStyleStrategy(this.headExcelAnnotations))
                .registerWriteHandler(new DataValidationWriterStrategy(this.headExcelAnnotations))
                .doWrite(exportData);
                
    }
    
    private List<List<Object>> prepareExportData(List<T> data)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        List<List<Object>> result = new ArrayList<>();
        for (T obj : data) {
            result.addAll(convertData(obj, this.excelFieldList));
        }
        return result;
    }
    
    private List<List<Object>> convertData(Object obj, List<ExcelField> excelFields)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        List<List<Object>> list = new ArrayList<>();
        // 至少有一行数据
        list.add(new ArrayList<>());
        for (ExcelField excelField : excelFields) {
            Excel excelAnnotation = excelField.getExcelAnnotation();
            if (!excelAnnotation.isExport()) {
                setDataToList(list, null);
            }
            if (CollectionUtils.isEmpty(excelField.getSubField())) {
                Object val = excelField.getField().get(obj);
                if (val == null) {
                    setDataToList(list, null);
                    continue;
                }
                Class<? extends ExcelHandlerAdapter> handlerClass = excelAnnotation.handler();
                ExcelHandlerAdapter handler = handlerClass.getDeclaredConstructor().newInstance();
                if (handler instanceof DefaultExcelFieldHandler defaultAdapter) {
                    defaultAdapter.setJava2ExcelConverter(this.converters);
                }
                Class<?> sourceType = excelField.getField().getType();
                Class<?> targetType;
                if (Excel.ColumnType.NUMERIC.equals(excelField.getExcelAnnotation().cellType())) {
                    targetType = Number.class;
                } else {
                    targetType = String.class;
                }
                setDataToList(list, handler.java2ExcelFormat(val, sourceType, targetType, excelAnnotation, excelField.getField()));
            } else {
                Object subVal = excelField.getField().get(obj);
                if (subVal instanceof List<?> l) {
                    List<Object> tmpList = new ArrayList<>();
                    for (Object o : l) {
                        List<List<Object>> lists = convertData(o, excelField.getSubField());
                        tmpList.addAll(lists);
                    }
                    setDataToList(list, tmpList);
                } else {
                    List<List<Object>> lists = convertData(subVal, excelField.getSubField());
                    setDataToList(list, lists);
                }
            }
        }
        return list;
    }
    
    private void setDataToList(List<List<Object>> list, Object data) {
        if (data instanceof List<?> collection) {
            int tSize = collection.size();
            int oSize = list.size();
            if (oSize < tSize) {
                // 原数组要拷贝
                List<Object> objects0 = new ArrayList<>(list.get(0));
                for (int i = oSize; i < tSize; i++) {
                    list.add(objects0);
                }
            }
            for (int i = 0; i < tSize; i++) {
                Object o = collection.get(i);
                if (o instanceof List<?> l) {
                    list.get(i).addAll(l);
                } else {
                    list.get(i).add(o);
                }
            }
            return;
        }
        for (List<Object> objects : list) {
            objects.add(data);
        }
    }
    
    public List<T> importExcel(InputStream is) {
        List<T> datas = new ArrayList<>();
        init(Excel.Type.IMPORT);
        EasyExcel.read(is, new ExcelModelDataListener<T>(this.excelFieldList, clazz, datas))
                .head(getHeader())
                .sheet()
                .doRead();
        return datas;
    }
    
    public String encodingFilename(String filename) {
        filename = UUID.randomUUID() + "_" + filename + ".xlsx";
        return filename;
    }
    
}
