package com.nl.pm.server.common;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class DownloadUtils {
    public static void excelDownload(HttpServletResponse res, HSSFWorkbook wb,String title) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        wb.write(outByteStream);

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title+".xls", "UTF-8"));
        ServletOutputStream outputStream = res.getOutputStream();
        // 将文件输出
        wb.write(outputStream);
        outputStream.close();
    }

}
