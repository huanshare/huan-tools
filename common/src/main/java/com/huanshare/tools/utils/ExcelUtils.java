package com.huanshare.tools.utils;

import com.huanshare.tools.exception.BizErrorBusinessException;
import com.huanshare.tools.exception.BizErrorBusinessException;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  2017/6/15.
 */
public class ExcelUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

    public static void exportExcel(String title, Map<String, String> headers, List dataList, OutputStream out) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            createSheet(workbook, title, headers, dataList);
            workbook.write(out);
        } catch (Exception e) {
            LOGGER.error("ExcelUtils exportExcel error. message : " + e.getMessage(), e);
        } finally {
            if(out != null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    LOGGER.error("ExcelUtils exportExcel out.close error. message : " + e.getMessage(), e);
                }
            }
        }
    }

    public static void createSheet(HSSFWorkbook workbook, String title, Map<String, String> headers, List dataList) throws Exception {
        if(workbook == null){
            return;
        }

        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(25);
        //设置列名
        HSSFRow titleRow = sheet.createRow(0);
        if(MapUtils.isNotEmpty(headers)){
            HSSFCellStyle cellStyle = getCellStyle(workbook);
            int i = 0;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                HSSFCell cell = titleRow.createCell(i);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(CommonUtils.getString(entry.getKey()));
                i++;
            }
        }
        //设置列值
        if (ListUtils.isNotEmpty(dataList)) {
            int index = 1;
            HSSFCellStyle contentStyle = getContentStyle(workbook);
            for (Object obj : dataList) {
                HSSFRow row = sheet.createRow(index);
                int j = 0;
                Class<?> aClass = obj.getClass();

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String propertyName = entry.getValue();
                    if (StringUtils.isEmpty(propertyName)) {
                        continue;
                    }
                    PropertyDescriptor pd = new PropertyDescriptor(propertyName, aClass);
                    Method method = pd.getReadMethod();
                    Object value = ReflectionUtils.invokeMethod(method, obj);

                    HSSFCell cell = row.createCell(j);
                    cell.setCellStyle(contentStyle);
                    String textValue;
                    if (value instanceof Date) {
                        textValue = DateUtils.dateToString((Date) value);
                    } else {
                        //其它数据类型都当作字符串简单处理
                        textValue = CommonUtils.getString(value);
                    }
                    cell.setCellValue(textValue);
                    j++;
                }
                index++;
            }
        }
    }

    private static HSSFCellStyle getCellStyle(HSSFWorkbook workbook){
        // 生成标题样式
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        // 设置这些样式
        titleStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        titleStyle.setFont(font);
        return titleStyle;
    }

    private static HSSFCellStyle getContentStyle(HSSFWorkbook workbook){
        // 生成并设置内容样式
        HSSFCellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont contentFont = workbook.createFont();
        contentFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        contentStyle.setFont(contentFont);
        return contentStyle;
    }

    /**
     * 通过http servlet导出excel
     */
    public static void exportExcelForServlet(String fileName, String excelTitle, Map<String, String> contentHeaders, HttpServletResponse response, AbstractExcelLoadBean excelLoadBean){
        List dataList;
        try {
            dataList = excelLoadBean.load();
        } catch (Exception e) {
            dataList = null;
            LOGGER.error("ExcelUtils exportExcelForServlet excelLoadBean.load error. message : " + e.getMessage(), e);
            //业务错误需要抛出去
            if(e instanceof BizErrorBusinessException){
                throw new BizErrorBusinessException(e.getMessage());
            }
        }

        String exportFileName;
        if(StringUtils.isNotBlank(fileName)){
            try {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                if(request != null && request.getHeader("USER-AGENT").toLowerCase().contains("firefox")){
                    exportFileName = new String(fileName.getBytes(), "ISO8859-1");
                }else{
                    exportFileName = URLEncoder.encode(fileName, "UTF-8");
                }
            } catch (Exception e) {
                exportFileName = fileName;
            }
        }else{
            exportFileName = DateUtils.dateToString(new Date(), DateUtils.FORMAT_LONG);
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + exportFileName + ".xls");
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        try {
            exportExcel(excelTitle, contentHeaders, dataList, response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("ExcelUtils exportExcelForServlet exportExcel error. message : " + e.getMessage(), e);
        }
    }
}
