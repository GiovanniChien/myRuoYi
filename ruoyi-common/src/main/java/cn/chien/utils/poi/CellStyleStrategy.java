package cn.chien.utils.poi;

import cn.chien.annotation.Excel;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.style.AbstractCellStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.util.List;

/**
 * @author qiandq3
 * @date 2022/12/8
 */
public class CellStyleStrategy extends AbstractCellStyleStrategy {
    
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
