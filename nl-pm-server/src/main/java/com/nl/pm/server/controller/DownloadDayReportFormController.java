package com.nl.pm.server.controller;

import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.service.IDayReportService;
import com.nl.pm.server.service.IProjectService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.*;
import com.nl.pm.server.service.param.DayReportSearchModelParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author pf
 * @version 1.0
 * @date 2021/9/3 9:09
 */
@RestController
@Api(value = "日报报表下载",tags = {"日报报表下载"})
@RequestMapping("/downloadDayReportForm")
public class DownloadDayReportFormController {
    private static final Logger log = LoggerFactory.getLogger(DownloadDayReportFormController.class);

    @Autowired
    private IDayReportService iDayReportService;
    @Autowired
    private SecurityContextUtils securityContextUtils;
    @Autowired
    private IProjectService iProjectService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IUserService userService;


    @ApiOperation(value = "日报报表下载",notes = "日报报表下载")
    @PostMapping("/downDayReportFormDetails")
    public void downDayReportFormDetails(@RequestBody ReqSearchDayReportVO reqVO,HttpServletRequest req, HttpServletResponse res) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, IOException, BaseAuthException {
//        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
//        if(RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
//                || RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
//                || RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
//                || RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
//            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
//        }
        //查询范围限制人员列表
        List<Integer> limitUser = searchLimitUser(reqVO.getProjectId(), reqVO.getUserId());
        List<Integer> limitProject = searchLimitProject();

        DayReportSearchModelParam param = new DayReportSearchModelParam();
        param.setLimitUser(limitUser);
        param.setLimitProject(limitProject);
        param.setProjectId(reqVO.getProjectId());
        param.setUserId(reqVO.getUserId());
        param.setEndDate(DateUtils.convertLongToDate(reqVO.getEndDate()));
        param.setStartDate(DateUtils.convertLongToDate(reqVO.getStartDate()));
        param.setPageSize(reqVO.getPageSize());
        param.setCurrentPage(reqVO.getCurrentPage());
        BasePagesDomain<DayReportAdvanceModel> resModel = iDayReportService.searchDayReport(param);

        List<ResDayReportVO> resRecords = new ArrayList<>();
        for (DayReportAdvanceModel model : resModel.getTotalList()) {
            ResDayReportVO resVO = new ResDayReportVO();
            resVO.setId(model.getId());
            resVO.setUserId(model.getUserId());
            resVO.setNickname(model.getNickname());
            resVO.setAreaName(model.getAreaName());
            resVO.setDate(DateUtils.convertDateToLong(model.getDate()));
            DayExchangeModel leaveModel = model.getLeaveModel();

            ReqLeaveVO leaveVO = new ReqLeaveVO();
            if (leaveModel != null) {
                leaveVO.setLeaveHours(leaveModel.getLeaveHour());
                leaveVO.setDesc(leaveModel.getDesc());
            }
            resVO.setLeaveVO(leaveVO);

            List<ReqDayReportVO> resReportList = new ArrayList<>();
            for (DayReportTaskModel drModel : model.getDayReportList()) {
                ReqDayReportVO vo = new ReqDayReportVO();
                vo.setProjectId(drModel.getProjectId());
                vo.setProjectName(drModel.getProjectName());
                vo.setAreaName(drModel.getAreaName());
                vo.setHours(drModel.getHours());
                vo.setDesc(drModel.getDesc());
                resReportList.add(vo);
            }
            resVO.setDayReportList(resReportList);

            resRecords.add(resVO);
        }

        BasePagesDomain<ResDayReportVO> result = new BasePagesDomain<>(resModel.getCurrentPage(), resModel.getPageSize(), resModel.getTotalPage(), resModel.getTotal());
        result.setTotalList(resRecords);

        List<ResDayReportFormVO> list = new ArrayList<>();

        if(result == null ){
            throw new BaseServiceException(ServiceErrorCodeEnum.QUERY_NOT_DATA);
        }
        if(org.apache.commons.collections.CollectionUtils.isEmpty(result.getTotalList())){
            throw new BaseServiceException(ServiceErrorCodeEnum.QUERY_NOT_DATA);
        }

        for (int i = 0; i < result.getTotalList().size(); i++) {
            if(org.apache.commons.collections.CollectionUtils.isEmpty(result.getTotalList().get(i).getDayReportList())){
                ResDayReportFormVO vo = new ResDayReportFormVO();
                vo.setNickname(result.getTotalList().get(i).getNickname());
                vo.setDate(DateUtils.convertDateToStr(DateUtils.convertLongToDate(result.getTotalList().get(i).getDate())));
//                if(result.getTotalList().get(i).getLeaveVO() == null){
//                    vo.setLeaveHours(0.0);
//                    vo.setLeaveDesc("无请假描述信息");
//                }else {
                    vo.setLeaveHours(result.getTotalList().get(i).getLeaveVO().getLeaveHours());
                    vo.setLeaveDesc(result.getTotalList().get(i).getLeaveVO().getDesc() == null ?
                            " " : result.getTotalList().get(i).getLeaveVO().getDesc());
//                }
                list.add(vo);

            }else {
                for (int j = 0; j < result.getTotalList().get(i).getDayReportList().size(); j++) {
                    ResDayReportFormVO vo = new ResDayReportFormVO();
                    vo.setNickname(result.getTotalList().get(i).getNickname());
                    vo.setDate(DateUtils.convertDateToStr(DateUtils.convertLongToDate(result.getTotalList().get(i).getDate())));
                    if(result.getTotalList().get(i).getLeaveVO() == null){
                        vo.setLeaveHours(0.0);
                        vo.setLeaveDesc(" ");
                    }else {
                        vo.setLeaveHours(result.getTotalList().get(i).getLeaveVO().getLeaveHours() == null ?
                                0.0 : result.getTotalList().get(i).getLeaveVO().getLeaveHours());
                        vo.setLeaveDesc(result.getTotalList().get(i).getLeaveVO().getDesc() == null ?
                                " " : result.getTotalList().get(i).getLeaveVO().getDesc());
                    }
                    vo.setId(result.getTotalList().get(i).getId());
                    vo.setProjectName(result.getTotalList().get(i).getDayReportList().get(j).getProjectName());
                    vo.setProjectHours(result.getTotalList().get(i).getDayReportList().get(j).getHours());
                    vo.setProjectDesc(result.getTotalList().get(i).getDayReportList().get(j).getDesc() == null ?
                            " " : result.getTotalList().get(i).getDayReportList().get(j).getDesc());
                    list.add(vo);
                }
            }


        }

        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");


        sheet.setColumnWidth(0, 3500);
        sheet.setColumnWidth(1, 3500);
        sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 3500);
        sheet.setColumnWidth(4, 3500);
        sheet.setColumnWidth(5, 3500);
        sheet.setColumnWidth(6, 3500);

        //设置样式
        CellStyle blackStyle = wb.createCellStyle();
        //自动换行*重要*
        blackStyle.setWrapText(true);
        //自适应列宽
//        sheet.autoSizeColumn(1);
//        sheet.autoSizeColumn(1, true);

        //设置样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        DecimalFormat strFormat = new DecimalFormat("0.00");
        HSSFFont redHeadFont = wb.createFont();
        //颜色
        redHeadFont.setColor(Font.COLOR_RED);
        //设置字体大小
        redHeadFont.setFontHeightInPoints((short) 10);
        //字体
        //redHeadFont.setFontName("宋体");
        style.setFont(redHeadFont);

        //设置样式
        //设置headStyle样式
        HSSFCellStyle headStyle = wb.createCellStyle();
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
        headStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);//前景填充色
//        headStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
//        headStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
//        headStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
//        headStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中


        //设置bodyStyle样式
        HSSFCellStyle bodyStyle = wb.createCellStyle();
//        bodyStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
//        bodyStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);//前景填充色

//        bodyStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
//        bodyStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
//        bodyStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
//        bodyStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

        bodyStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        bodyStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);


        bodyStyle.setWrapText(true);//设置自动换行

        //冻结窗口设置
        sheet.createFreezePane(1,2,1,2);

        //第一行
        HSSFCell cell = sheet.createRow(0).createCell(0);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("日报报表统计").append("（").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getStartDate()))).append("---")
                .append( DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getEndDate()))).append("）");
//        cell.setCellValue("日报报表统计" + " （" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getStartDate())) + " --- "+
//                DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getEndDate())) + ")");
        cell.setCellValue(stringBuilder.toString());
        cell.setCellStyle(style);


        //设置起止行和列
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 6);
        sheet.addMergedRegion(region);

        //存储最大列宽
        Map<Integer,Integer> maxWidth = new HashMap<>();
        //创建2行，并先给第一列赋值，并赋值样式headStyle
        HSSFRow row2 = sheet.createRow(1);
        row2.setHeightInPoints(20);//目的是想把行高设置成20px
        row2.createCell(0).setCellValue("姓名");
        row2.createCell(1).setCellValue("日期");
        row2.createCell(2).setCellValue("请假时长");
        row2.createCell(3).setCellValue("请假描述");
        row2.createCell(4).setCellValue("关联项目");
        row2.createCell(5).setCellValue("项目时长");
        row2.createCell(6).setCellValue("项目描述");
        row2.getCell(0).setCellStyle(headStyle);
        row2.getCell(1).setCellStyle(headStyle);
        row2.getCell(2).setCellStyle(headStyle);
        row2.getCell(3).setCellStyle(headStyle);
        row2.getCell(4).setCellStyle(headStyle);
        row2.getCell(5).setCellStyle(headStyle);
        row2.getCell(6).setCellStyle(headStyle);

        // 初始化标题的列宽,字体
//        for (int i= 0; i<=6;i++){
//            maxWidth.put(i,row2.getCell(i).getStringCellValue().getBytes().length  * 256 + 200);
//            row2.getCell(i).setCellStyle(blackStyle);//设置自动换行
//        }

        //行
        int rowNum = 2;
        //列
        int lineNum = 0;

        //String name = StringUtils.EMPTY;
        Integer id = 0;
        int count = 1;
        int cycleNum = 0;
        int size = list.size();
        for (int i = 0; i < size; i++) {
            cycleNum ++;
            HSSFRow row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);

            //行高
            //row.setHeightInPoints(20);
            //if(list.get(i).getNickname().equals(name)){
            if(list.get(i).getId() == id){
                //执列
                lineNum += 4;
                count ++;
                if(cycleNum == size ){
                    CellRangeAddress region5 = new CellRangeAddress(rowNum - count, rowNum - 1, 0, 0);
                    sheet.addMergedRegion(region5);

                    CellRangeAddress region6 = new CellRangeAddress(rowNum - count, rowNum - 1, 1, 1);
                    sheet.addMergedRegion(region6);

                    CellRangeAddress region7 = new CellRangeAddress(rowNum - count, rowNum - 1, 2, 2);
                    sheet.addMergedRegion(region7);

                    CellRangeAddress region8 = new CellRangeAddress(rowNum - count, rowNum - 1, 3, 3);
                    sheet.addMergedRegion(region8);
                }
            }else {
                //name = list.get(i).getNickname();
                id =  list.get(i).getId();
                row.createCell(lineNum++).setCellValue(list.get(i).getNickname());
                row.getCell(0).setCellStyle(bodyStyle);
                row.createCell(lineNum++).setCellValue(list.get(i).getDate());
                row.getCell(lineNum-1).setCellStyle(bodyStyle);
                row.createCell(lineNum++).setCellValue(list.get(i).getLeaveHours());
                row.getCell(lineNum-1).setCellStyle(bodyStyle);
                if(count > 1){
                    count += 1;
                    CellRangeAddress region1 = new CellRangeAddress(rowNum - count, rowNum - 2, 0, 0);
                    sheet.addMergedRegion(region1);

                    CellRangeAddress region2 = new CellRangeAddress(rowNum - count, rowNum - 2, 1, 1);
                    sheet.addMergedRegion(region2);

                    CellRangeAddress region3 = new CellRangeAddress(rowNum - count, rowNum - 2, 2, 2);
                    sheet.addMergedRegion(region3);

                    CellRangeAddress region4 = new CellRangeAddress(rowNum - count, rowNum - 2, 3, 3);
                    sheet.addMergedRegion(region4);

                }
                count = 1;
                int length = list.get(i).getDate().getBytes().length * 256 +200;
                if (length>3000){
                    length = 3000;
                }
//                maxWidth.put(i,Math.max(length,maxWidth.get(i)));
//                row.getCell(lineNum).setCellStyle(blackStyle);//设置自动换行
                row.createCell(lineNum++).setCellValue(list.get(i).getLeaveDesc());
            }
            row.createCell(lineNum++).setCellValue(list.get(i).getProjectName() == null ? " " : list.get(i).getProjectName());
            row.getCell(lineNum-1).setCellStyle(bodyStyle);
            row.createCell(lineNum++).setCellValue(list.get(i).getProjectHours() == null ? " " : list.get(i).getProjectHours());
            row.getCell(lineNum-1).setCellStyle(bodyStyle);
            row.createCell(lineNum++).setCellValue(list.get(i).getProjectDesc() == null ? " " : list.get(i).getProjectDesc());
            row.getCell(lineNum-1).setCellStyle(bodyStyle);
            lineNum = 0;
        }


        System.out.println("执行完成");
        // 设置response的Header

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        wb.write(outByteStream);

        StringBuilder builder = new StringBuilder();
        builder.append("日报报表统计").append("（").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getStartDate()))).append("---")
                .append( DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getEndDate()))).append("）");
        String fileName = builder.toString();

//        String fileName = "日报报表统计" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getStartDate()))
//                + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getEndDate()));
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = res.getOutputStream();
        // 将文件输出
        wb.write(outputStream);
        outputStream.close();

    }

    /**
     * 查询范围限制人员列表
     *
     * @return
     * @throws BaseServiceException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private List<Integer> searchLimitUser(Integer projectId, Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();

        List<Integer> limitUser = new ArrayList<>();

        if (currentUserRole == RoleTypeEnum.EMPLOYEE) {
            limitUser.add(currentUserId);
        } else if (currentUserRole == RoleTypeEnum.GROUP_MANAGER) {
            limitUser.add(currentUserId);
            List<ProjectUserModel> allProjectUserByManagerId = iProjectService.getAllProjectUserByManagerId(currentUserId);
            for (ProjectUserModel projectUserModel : allProjectUserByManagerId) {
                limitUser.add(projectUserModel.getUserId());
            }
        } else if (currentUserRole == RoleTypeEnum.AREA_MANAGER) {
            limitUser.add(currentUserId);
            List<ProjectUserModel> allProjectUserByAreaId = iProjectService.getAllProjectUserByAreaId(currentUserAreaId);
            for (ProjectUserModel projectUserModel : allProjectUserByAreaId) {
                limitUser.add(projectUserModel.getUserId());
            }
        } else if (currentUserRole == RoleTypeEnum.SUPER_ADMIN) {
            List<UserModel> allUsers = iUserService.getAllUsers();
            for (UserModel allUser : allUsers) {
                limitUser.add(allUser.getId());
            }
        } else {
            limitUser = null;
        }

        Set<Integer> limitSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(limitUser)) {
            for (Integer integer : limitUser) {
                limitSet.add(integer);
            }
        }


        if (projectId != null || userId != null) {
            List<Integer> resUserList = new ArrayList<>();


            if (projectId != null) {
                List<ProjectUserModel> projectUsers = iProjectService.getAllProjectUserById(projectId);
                for (ProjectUserModel projectUser : projectUsers) {
                    Integer uId = projectUser.getUserId();
                    if (limitSet.contains(uId)) {
                        resUserList.add(uId);
                    }
                }
            }

            if (userId != null) {
                if (limitSet.contains(userId)) {
                    if (!CollectionUtils.isEmpty(resUserList)) {
                        List<Integer> ids = new ArrayList<>();
                        for (Integer integer : resUserList) {
                            if (userId.equals( integer)) {
                                ids.add(userId);
                                return ids;
                            }
                        }
                        return new ArrayList<>();
                    }else{
                        resUserList.add(userId);
                        return resUserList;
                    }
                }else{
                    return new ArrayList<>();
                }
            }

            return resUserList;
        } else {
            return limitUser;
        }
    }


    private List<Integer> searchLimitProject() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();

        List<Integer> limitProject = new ArrayList<>();
        List<ProjectAdvanceModel> list = new ArrayList<>();
        if (currentUserRole == RoleTypeEnum.EMPLOYEE || currentUserRole == RoleTypeEnum.GROUP_MANAGER) {
            list = iProjectService.searchAllProjectListByUserIdAndAreaId(currentUserId, null, false);
        } else if (currentUserRole == RoleTypeEnum.AREA_MANAGER) {
            list = iProjectService.searchAllProjectListByUserIdAndAreaId(currentUserId, currentUserAreaId, false);
        } else if (currentUserRole == RoleTypeEnum.SUPER_ADMIN) {
            list = iProjectService.searchAllProjectListByUserIdAndAreaId(currentUserId, currentUserAreaId, true);
        }

        if(!CollectionUtils.isEmpty(list)){
            for (ProjectAdvanceModel projectAdvanceModel : list) {
                limitProject.add(projectAdvanceModel.getId());
            }
        }

        return limitProject;
    }
}
