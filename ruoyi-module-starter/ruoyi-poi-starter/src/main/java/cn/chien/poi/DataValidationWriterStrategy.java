package cn.chien.poi;

import cn.chien.poi.annotation.Excel;
import cn.chien.utils.StringUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.List;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
public class DataValidationWriterStrategy implements CellWriteHandler {
    
    private final List<List<Excel>> headExcelAnnotations;
    
    public DataValidationWriterStrategy(List<List<Excel>> headExcelAnnotations) {
        this.headExcelAnnotations = headExcelAnnotations;
    }
    
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        int columnIndex = context.getCell().getColumnIndex();
        List<Excel> excels = headExcelAnnotations.get(columnIndex);
        Excel excel = excels.get(excels.size() - 1);
        String converterExp = excel.readConverterExp();
        if (StringUtils.isEmpty(converterExp)) {
            return;
        }
        // 下拉框的起始行，终止行，起始列，终止列
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 10000, columnIndex, columnIndex);
        DataValidationHelper helper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
        DataValidationConstraint explicitListConstraint = helper.createExplicitListConstraint(
                getExplicitList(converterExp));
        DataValidation validation = helper.createValidation(explicitListConstraint, cellRangeAddressList);
        // 处理兼容性问题
        if (validation instanceof XSSFDataValidation) {
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
        } else {
            validation.setSuppressDropDownArrow(false);
        }
        context.getWriteSheetHolder().getSheet().addValidationData(validation);
    }
    
    /**
     * @param converterExp exp 0=男,1=女,2=未知
     */
    private String[] getExplicitList(String converterExp) {
        String[] split = converterExp.split(",");
        String[] explicits = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            explicits[i] = s.split("=")[1];
        }
        return explicits;
    }
    
}
