package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
public class HeaderWidthStyleStrategy extends AbstractColumnWidthStyleStrategy {
    
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
