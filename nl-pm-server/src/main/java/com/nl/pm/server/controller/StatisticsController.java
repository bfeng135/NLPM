package com.nl.pm.server.controller;

import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.DownloadUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.CostTypeEnum;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.param.AreaProjectCostDetailEntityParam;
import com.nl.pm.server.registry.param.AreaProjectCostEntityParam;
import com.nl.pm.server.service.IAreaService;
import com.nl.pm.server.service.IHolidayService;
import com.nl.pm.server.service.IStatisticsService;
import com.nl.pm.server.service.model.AreaModel;
import com.nl.pm.server.service.model.DayReportTaskAdvanceModel;
import com.nl.pm.server.service.model.HolidayModel;
import com.nl.pm.server.service.model.ProjectAdvanceModel;
import com.nl.pm.server.service.param.AreaUsersProjectTimeModelParam;
import com.nl.pm.server.service.param.UserTimeModelParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.*;

@RestController
@Api(value = "统计管理", tags = {"统计管理"})
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private SecurityContextUtils securityContextUtils;
    @Autowired
    private IStatisticsService iStatisticsService;
    @Autowired
    private IAreaService iAreaService;

    @Autowired
    private IHolidayService iHolidayService;

    @ApiOperation(value = "查询人员项目工时图表", notes = "查询人员项目工时图表")
    @PostMapping("/queryUserProjectTimeList")
    public List<AreaUsersProjectTimeModelParam> queryUserProjectTimeList(@RequestBody ReqUserProjectTimeVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer areaId = null;
        if (RoleTypeEnum.SUPER_ADMIN == currentUserRole || RoleTypeEnum.MANAGEMENT == currentUserRole || RoleTypeEnum.FINANCE == currentUserRole) {
            areaId = reqVO.getAreaId();
        } else if (RoleTypeEnum.AREA_MANAGER == currentUserRole) {
            areaId = currentUserAreaId;
        } else {
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }
        Boolean statusFlag = reqVO.getStatusFlag();
        Long startTime = reqVO.getStartTime();
        Long endTime = reqVO.getEndTime();
        Date startDate = DateUtils.convertLongToDate(startTime);
        Date endDate = DateUtils.convertLongToDate(endTime);

        List<AreaUsersProjectTimeModelParam> modelList = iStatisticsService.queryUserProjectTimeList(areaId, statusFlag, startDate, endDate);
        return modelList;

    }

    @ApiOperation(value = "查询人员工作休假占比图表", notes = "查询人员工作休假占比图表")
    @PostMapping("/queryUserWorkLeaveHours")
    public List<UserTimeModelParam> queryUserWorkLeaveHours(@RequestBody ReqUserProjectTimeVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer areaId = null;
        if (RoleTypeEnum.SUPER_ADMIN == currentUserRole || RoleTypeEnum.MANAGEMENT == currentUserRole || RoleTypeEnum.FINANCE == currentUserRole) {
            areaId = reqVO.getAreaId();
        } else if (RoleTypeEnum.AREA_MANAGER == currentUserRole) {
            areaId = currentUserAreaId;
        } else {
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }
        Boolean statusFlag = reqVO.getStatusFlag();
        Long startTime = reqVO.getStartTime();
        Long endTime = reqVO.getEndTime();
        Date startDate = DateUtils.convertLongToDate(startTime);
        Date endDate = DateUtils.convertLongToDate(endTime);

        List<UserTimeModelParam> modelList = iStatisticsService.queryUserWorkLeaveHours(areaId, statusFlag, startDate, endDate);
        return modelList;

    }


    @ApiOperation(value = "查询人员项目工时图表", notes = "查询人员项目工时图表")
    @PostMapping("/queryOneSelfProjectTimeList")
    public List<ResProjectTimeVO> queryOneSelfProjectTimeList(@RequestBody ReqOneSelfProjectTimeVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {

        Integer currentUserId = securityContextUtils.getCurrentUserId();
        Long startTime = reqVO.getStartTime();
        Long endTime = reqVO.getEndTime();
        Date startDate = DateUtils.convertLongToDate(startTime);
        Date endDate = DateUtils.convertLongToDate(endTime);
        List<ResProjectTimeVO> voList = new ArrayList<>();
        List<ProjectAdvanceModel> projectAdvanceModels = iStatisticsService.queryOneSelfProjectTimeList(currentUserId, startDate, endDate);
        List<Integer> projectIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(projectAdvanceModels)) {
            for (ProjectAdvanceModel projectAdvanceModel : projectAdvanceModels) {
                projectIds.add(projectAdvanceModel.getId());
            }

        }
        List<DayReportTaskAdvanceModel> dayReportTaskAdvanceModels = iStatisticsService.queryDayReportDetailListByProjectIdsAndTimes(projectIds, startDate, endDate);
        Map<Integer, List<ResReportDetailVO>> reportMap = new HashMap<>();


        if (CollectionUtils.isNotEmpty(dayReportTaskAdvanceModels)) {
            for (DayReportTaskAdvanceModel dayReportTaskAdvanceModel : dayReportTaskAdvanceModels) {
                Integer projectId = dayReportTaskAdvanceModel.getProjectId();
                List<ResReportDetailVO> resReportDetailVOS = new ArrayList<>();
                if (reportMap.containsKey(projectId)) {
                    resReportDetailVOS = reportMap.get(projectId);
                }
                ResReportDetailVO detailVO = new ResReportDetailVO();
                detailVO.setHours(dayReportTaskAdvanceModel.getHours());
                detailVO.setDesc(dayReportTaskAdvanceModel.getDesc());
                detailVO.setReportDate(DateUtils.convertDateToLong(dayReportTaskAdvanceModel.getReportDate()));
                resReportDetailVOS.add(detailVO);
                reportMap.put(projectId, resReportDetailVOS);
            }
        }


        List<ProjectAdvanceModel> projectAllTimes = iStatisticsService.queryOneSelfProjectTimeList(currentUserId, null, null);
        Map<Integer, Double> projectAllTimeMap = new HashMap();
        if (CollectionUtils.isNotEmpty(projectAllTimes)) {
            for (ProjectAdvanceModel projectAllTime : projectAllTimes) {
                projectAllTimeMap.put(projectAllTime.getId(), projectAllTime.getHours());
            }
        }

        if (CollectionUtils.isNotEmpty(projectAdvanceModels)) {
            for (ProjectAdvanceModel model : projectAdvanceModels) {
                ResProjectTimeVO vo = new ResProjectTimeVO();
                vo.setProjectId(model.getId());
                vo.setProjectName(model.getName());
                vo.setAreaId(model.getAreaId());
                vo.setAreaName(model.getAreaName());
                vo.setHours(model.getHours());
                vo.setAllHours(projectAllTimeMap.get(model.getId()));
                vo.setResReportDetailVOS(reportMap.get(model.getId()));
                voList.add(vo);
            }

        }

        return voList;

    }


    @ApiOperation(value = "查询区域项目工时消耗统计", notes = "查询区域项目工时消耗统计")
    @PostMapping("/area/cost")
    public ResAreaCostVO queryAreaCost(@RequestBody ReqAreaCostVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        Integer areaId = reqVO.getAreaId();
        if (areaId == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.STATISTICS_AREAID_NULL_ERROR);
        }

        String costType = reqVO.getCostTypeEnum().getCode();
        if (costType == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.STATISTICS_COST_TYPE_NULL_ERROR);
        }

        if(reqVO.getStartTime() == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.STATISTICS_START_TIME_NULL_ERROR);
        }

        if(reqVO.getEndTime() == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.STATISTICS_END_TIME_NULL_ERROR);
        }


        Date startTime = DateUtils.convertLongToDate(reqVO.getStartTime());
        Date endTime = DateUtils.convertLongToDate(reqVO.getEndTime());

        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        if (!RoleTypeEnum.AREA_MANAGER.equals(currentUserRole) &&
                !RoleTypeEnum.SUPER_ADMIN.equals(currentUserRole) &&
                !RoleTypeEnum.FINANCE.equals(currentUserRole)) {
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }

        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        if (RoleTypeEnum.AREA_MANAGER.equals(currentUserRole)) {
            if (!areaId.equals(currentUserAreaId)) {
                throw new BaseServiceException(ServiceErrorCodeEnum.STATISTICS_AREA_OUT_ERROR);
            }
        }


        List<AreaProjectCostEntityParam> areaProjectCostEntityParams = iStatisticsService.queryAreaProjectCostList(areaId, costType, startTime, endTime);
        Double allHours = 0.0;
        if (CollectionUtils.isNotEmpty(areaProjectCostEntityParams)) {
            for (AreaProjectCostEntityParam param : areaProjectCostEntityParams) {
                allHours = allHours + param.getProjectHours();

            }

        }
        ResAreaCostVO resVO = new ResAreaCostVO();
        resVO.setProjectCostVOList(areaProjectCostEntityParams);
        resVO.setAllHours(allHours);
        return resVO;
    }


    @ApiOperation(value = "下载区域项目工时消耗统计表", notes = "下载区域项目工时消耗统计表")
    @PostMapping("/area/cost/download")
    public void downloadAreaCost(@RequestBody ReqAreaCostVO reqVO, HttpServletRequest req, HttpServletResponse res) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException, IOException, EncoderException {
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();

        //设置样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        //添加下边框样式
        HSSFCellStyle boderBottomStyle = wb.createCellStyle();
        boderBottomStyle.setWrapText(true);
        boderBottomStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        boderBottomStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        boderBottomStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);

        //添加右边框样式
        HSSFCellStyle boderRightStyle = wb.createCellStyle();
        boderRightStyle.setWrapText(true);
        boderRightStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        boderRightStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        boderRightStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);

        //添加下和右边框样式
        HSSFCellStyle boderBottomRightStyle = wb.createCellStyle();
        boderBottomRightStyle.setWrapText(true);
        boderBottomRightStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        boderBottomRightStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        boderBottomRightStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        boderBottomRightStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);

        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("区域协作工时统计");
        //定义表头
        HSSFRow row2 = sheet.createRow(1);
        HSSFCell cell2 = row2.createCell(0);
        cell2.setCellValue("项目工时合计");
        cell2.setCellStyle(style);
        HSSFRow row3 = sheet.createRow(2);
        HSSFCell cell3 = row3.createCell(0);
        cell3.setCellValue("项目所属区域");
        cell3.setCellStyle(style);
        HSSFRow row4 = sheet.createRow(3);
        HSSFCell cell4 = row4.createCell(0);
        cell4.setCellValue("姓名\\项目");
        cell4.setCellStyle(style);

        Integer reqAreaId = reqVO.getAreaId();
        //从第5行开始写入数据
        int maxEnableRowNum = 4;
        Integer mainMaxColnum=null;
        Integer otherMaxColnum=null;

        int colnum = 1;
        int startColnum = 1;

        //本区合计所在行号
        Integer myAreaSumLocationRowNum = null;

        // Map< userId , 位置行号 >
        Map<Integer, Integer> userLocationMap = new HashMap<>();

        List<String> typeList = new ArrayList<>();
        typeList.add(CostTypeEnum.MY_MAIN_COST.getCode());
        typeList.add(CostTypeEnum.MY_EARN.getCode());

        String tip = "";
        String sumTip = "";
        AreaModel areaInfo = iAreaService.getAreaInfo(reqVO.getAreaId());
        boolean myAreaSumFlag = false;


        //冻结窗口设置
        sheet.createFreezePane(1, 4, 1, 4);
        //第一行
        HSSFRow row1 = sheet.createRow(0);

        CostTypeEnum typeEnum = null;
        //开始写数据
        for (String type : typeList) {
            StringBuilder title = new StringBuilder();

            switch (type) {
                case "MY_MAIN_COST":
                    typeEnum = CostTypeEnum.MY_MAIN_COST;
                    tip = "主负责项目统计";
                    sumTip = "本区合计";
                    startColnum = 1;
                    colnum = 1;
                    title.append(areaInfo.getName() + "-" + tip)
                            .append("（")
                            .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getStartTime())))
                            .append("---")
                            .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getEndTime())))
                            .append("）");
                    break;
                case "MY_EARN":
                    typeEnum = CostTypeEnum.MY_EARN;
                    mainMaxColnum = colnum;
                    tip = "其他工时统计";
                    sumTip = "其他合计";
                    colnum = colnum + 1;
                    startColnum = colnum;
                    title.append(tip);

                    break;
            }


            reqVO.setCostTypeEnum(CostTypeEnum.convertToEnum(type));
            ResAreaCostVO resAreaCostVO = this.queryAreaCost(reqVO);
            List<AreaProjectCostEntityParam> projectCostList = resAreaCostVO.getProjectCostVOList();


            //有几个项目就有几列
            int projectNum = projectCostList.size();


            //写标题
            HSSFCell cell = row1.createCell(colnum);
            cell.setCellValue(title.toString());

            try {
                //设置起止行和列,合并单元格
                CellRangeAddress region = new CellRangeAddress(0, 0, colnum, colnum + projectNum);
                sheet.addMergedRegion(region);
            }catch (Exception e){
                System.out.println("无需合并单元格");
            }

            cell.setCellStyle(boderRightStyle);

            if (CollectionUtils.isNotEmpty(projectCostList)) {
                for (AreaProjectCostEntityParam project : projectCostList) {

                    //如果是查询主负责区，需要判断是否切换到其他区帮忙本区做的数据写入，
                    // 如果已经开始写其他区帮忙的数据，需要先插入一条本区合计，并记录第几行
                    Integer projectAreaId = project.getAreaId();
                    if (!reqAreaId.equals(projectAreaId) && myAreaSumFlag == false) {
                        HSSFRow userNameRow = sheet.createRow(maxEnableRowNum);
                        HSSFCell userNameCell = userNameRow.createCell(0);
                        userNameCell.setCellValue("本区合计");
                        userNameCell.setCellStyle(boderBottomStyle);
                        //新增人员到map中
                        myAreaSumLocationRowNum = maxEnableRowNum;
                        //最大可写入行数更新
                        maxEnableRowNum = maxEnableRowNum + 1;
                        myAreaSumFlag = true;
                    }

                    //开始写入表格
                    HSSFCell projectCell = row4.createCell(colnum);
                    projectCell.setCellStyle(style);
                    projectCell.setCellValue(project.getProjectName());

                    HSSFCell areaCell = row3.createCell(colnum);
                    areaCell.setCellStyle(style);
                    areaCell.setCellValue(project.getAreaName());

                    HSSFCell timeCell = row2.createCell(colnum);
                    timeCell.setCellStyle(style);
                    timeCell.setCellValue(project.getProjectHours());


                    List<AreaProjectCostDetailEntityParam> detailList = project.getDetailList();

                    Double oldTime = 0.0;
                    Integer oldUserId = 0;

                    if (CollectionUtils.isNotEmpty(detailList)) {
                        for (int i = 0; i < detailList.size(); i++) {
                            AreaProjectCostDetailEntityParam detail = detailList.get(i);
                            Integer currentUserId = detail.getUserId();
                            Double hours = detail.getHours();
                            String nickname = detail.getNickname();

                            //如果这个人没录入过，先录入人
                            if (!userLocationMap.containsKey(currentUserId)) {
                                HSSFRow userNameRow = sheet.createRow(maxEnableRowNum);
                                HSSFCell userNameCell = userNameRow.createCell(0);
                                userNameCell.setCellValue(nickname);
                                userNameCell.setCellStyle(style);
                                //新增人员到map中
                                userLocationMap.put(currentUserId, maxEnableRowNum);
                                //最大可写入行数更新
                                maxEnableRowNum = maxEnableRowNum + 1;
                            }

                            //如果是最后一条
                            if (i == detailList.size() - 1) {
                                //判断和上一个人一样不一样
                                if (!oldUserId.equals(currentUserId) && !oldUserId.equals(0)) {
                                    //不一样，并且不是第一条，先把上一个写了
                                    Integer userLocal = userLocationMap.get(oldUserId);
                                    HSSFCell userTimeCell = sheet.getRow(userLocal).createCell(colnum);
                                    userTimeCell.setCellValue(oldTime);
                                    userTimeCell.setCellStyle(style);

                                    //重置缓存时间
                                    oldTime = 0.0;
                                    oldUserId = currentUserId;
                                }
                                //再写当前这个
                                oldTime = oldTime + hours;
                                Integer userLocal = userLocationMap.get(currentUserId);
                                HSSFCell userTimeCell = sheet.getRow(userLocal).createCell(colnum);
                                userTimeCell.setCellValue(oldTime);
                                userTimeCell.setCellStyle(style);
                                //重置初始数据
                                oldUserId = 0;
                                oldTime = 0.0;

                            } else {
                                //不是最后一条
                                //判断是不是第一条
                                if (i == 0) {
                                    //是第一条
                                    //将当前人员相关数据进行暂存
                                    Integer userLocal = userLocationMap.get(currentUserId);
                                    oldTime = oldTime + hours;
                                    oldUserId = currentUserId;

                                } else {
                                    //不是第一条
                                    //判断如果换人了
                                    if (!oldUserId.equals(currentUserId)) {
                                        //如果换人了，则将上一个人的数据写到对应的单元格中
                                        Integer userLocal = userLocationMap.get(oldUserId);
                                        HSSFCell userTimeCell = sheet.getRow(userLocal).createCell(colnum);
                                        userTimeCell.setCellValue(oldTime);
                                        userTimeCell.setCellStyle(style);
                                        //重置初始数据为当前人
                                        oldUserId = currentUserId;
                                        oldTime = hours;
                                    } else {
                                        //叠加时间
                                        oldTime = oldTime + hours;
                                    }

                                }
                            }

                        }

                    }
                    colnum++;
                }

                //如果没出现其他区帮忙的工时，需要手动插入本区合计
                if(myAreaSumFlag == false) {
                    HSSFRow userNameRow = sheet.createRow(maxEnableRowNum);
                    HSSFCell userNameCell = userNameRow.createCell(0);
                    userNameCell.setCellValue("本区合计");
                    userNameCell.setCellStyle(boderBottomStyle);
                    //新增人员到map中
                    myAreaSumLocationRowNum = maxEnableRowNum;
                    //最大可写入行数更新
                    maxEnableRowNum = maxEnableRowNum + 1;
                    myAreaSumFlag = true;
                }


                //对本区全部负责项目进行合计
                //先写入全工时
                HSSFCell myAreaHoursCell = row3.createCell(colnum);
                myAreaHoursCell.setCellValue(sumTip);
                myAreaHoursCell.setCellStyle(boderRightStyle);

                HSSFCell myAreaBorderCell4 = row4.createCell(colnum);
                myAreaBorderCell4.setCellStyle(boderRightStyle);

                Double allHours = resAreaCostVO.getAllHours();
                HSSFCell allHoursCell = row2.createCell(colnum);
                allHoursCell.setCellValue(allHours);
                allHoursCell.setCellStyle(boderRightStyle);

                //开始计算并插入本区项目工时合计
                Double myAreaUserProjectAllHours = 0.0;
                //循环列
                for (int col = startColnum; col < colnum; col++) {
                    Double myPrjHour = 0.0;
                    //循环行
                    for (int row = 4; row < myAreaSumLocationRowNum; row++) {
                        HSSFCell tempCell = sheet.getRow(row).getCell(col);
                        double numericCellValue;
                        if (tempCell == null) {
                            numericCellValue = 0.0;
                        } else {
                            numericCellValue = tempCell.getNumericCellValue();
                        }
                        myPrjHour = myPrjHour + numericCellValue;
                    }
                    HSSFRow myAreaPrjSumRow = sheet.getRow(myAreaSumLocationRowNum);
                    if(myAreaPrjSumRow==null){
                        myAreaPrjSumRow = sheet.createRow(myAreaSumLocationRowNum);
                    }
                    HSSFCell myAreaPrjSumCell = myAreaPrjSumRow.createCell(col);
                    myAreaPrjSumCell.setCellValue(myPrjHour);
                    //添加下边框
                    myAreaPrjSumCell.setCellStyle(boderBottomStyle);

                    //累计每个项目的合计
                    myAreaUserProjectAllHours = myAreaUserProjectAllHours + myPrjHour;
                }
                HSSFCell myAreaUserProjectAllHoursCell = sheet.getRow(myAreaSumLocationRowNum).createCell(colnum);
                myAreaUserProjectAllHoursCell.setCellValue(myAreaUserProjectAllHours);
                //添加下和右边框样式
                myAreaUserProjectAllHoursCell.setCellStyle(boderBottomRightStyle);


                //开始计算并插入本区人员工时合计
                //循环行
                Integer maxRowSumNum = null;
                if(CostTypeEnum.MY_MAIN_COST.equals(typeEnum)){
                    maxRowSumNum = maxEnableRowNum;
                }else if(CostTypeEnum.MY_EARN.equals(typeEnum)){
                    maxRowSumNum = myAreaSumLocationRowNum+1;
                }else{
                    maxRowSumNum = null;
                }
                for (int row = 4; row < maxRowSumNum; row++) {
                    Double myUserHour = 0.0;
                    //循环列
                    double numericCellValue;
                    for (int col = startColnum; col < colnum; col++) {
                        HSSFCell tempCell = sheet.getRow(row).getCell(col);
                        if (tempCell == null) {
                            numericCellValue = 0.0;
                        } else {
                            numericCellValue = tempCell.getNumericCellValue();
                        }
                        myUserHour = myUserHour + numericCellValue;
                    }
                    HSSFCell myAreaUserSumCell = sheet.getRow(row).createCell(colnum);
                    myAreaUserSumCell.setCellValue(myUserHour);
                    if (row == myAreaSumLocationRowNum) {
                        //添加右边框
                        myAreaUserSumCell.setCellStyle(boderBottomRightStyle);
                    } else {
                        //添加右边框
                        myAreaUserSumCell.setCellStyle(boderRightStyle);
                    }
                }
            }

        }

        //结束循环，将休假的内容添加到右边

        otherMaxColnum = colnum;
        int leaveColnum = otherMaxColnum+1;

        sheet.getRow(1).createCell(leaveColnum).setCellStyle(boderRightStyle);
        sheet.getRow(2).createCell(leaveColnum).setCellStyle(boderRightStyle);
        HSSFCell leaveTitleCell = sheet.getRow(3).createCell(leaveColnum);
        leaveTitleCell.setCellValue("休假");
        leaveTitleCell.setCellStyle(boderRightStyle);




        ReqUserProjectTimeVO vo = new ReqUserProjectTimeVO();
        vo.setStartTime(reqVO.getStartTime());
        vo.setEndTime(reqVO.getEndTime());
        vo.setAreaId(reqVO.getAreaId());


        List<UserTimeModelParam> userTimeModelParams = this.queryUserWorkLeaveHours(vo);
        if(CollectionUtils.isNotEmpty(userTimeModelParams)){
            for (UserTimeModelParam param : userTimeModelParams) {
                Integer userId = param.getUserId();
                Double leaveHours = param.getLeaveHours();
                if(userLocationMap.containsKey(userId)){
                    Integer rowNum = userLocationMap.get(userId);
                    if(rowNum < myAreaSumLocationRowNum) {
                        HSSFCell leaveCell = sheet.getRow(rowNum).createCell(leaveColnum);
                        leaveCell.setCellValue(leaveHours);
                        leaveCell.setCellStyle(boderRightStyle);
                    }
                }
            }

            //加入本区休假合计
            Double leaveSumAllHours = 0.0;
            for (int row = 4;row < myAreaSumLocationRowNum ;row++){
                HSSFRow leaveRow = sheet.getRow(row);
                HSSFCell cell = leaveRow.getCell(leaveColnum);
                double numericCellValue;
                if(cell==null){
                    cell=leaveRow.createCell(leaveColnum);
                    cell.setCellValue(0.0);
                    cell.setCellStyle(boderRightStyle);
                }
                numericCellValue= cell.getNumericCellValue();
                leaveSumAllHours=leaveSumAllHours+numericCellValue;

            }
            HSSFCell leaveSumCell = sheet.getRow(myAreaSumLocationRowNum).createCell(leaveColnum);
            leaveSumCell.setCellValue(leaveSumAllHours);
            leaveSumCell.setCellStyle(boderBottomRightStyle);

        }





        //最后写总计
        int allUserSumCol = leaveColnum+1;




        //计算查询时间间隔
        Long startTime = reqVO.getStartTime();
        Long endTime = reqVO.getEndTime();
        long l = endTime - startTime;
        Date startDate = DateUtils.convertLongToDate(startTime);
        String startStr = DateUtils.convertDateToStr(startDate);
        Date endDate = DateUtils.convertLongToDate(endTime);
        String endStr = DateUtils.convertDateToStr(endDate);

        long daysBetween=(endDate.getTime()-startDate.getTime()+1000000)/(60*60*24*1000);

        //添加查询时间段内的工作日标准小时数
        List<HolidayModel> holidayModels = iHolidayService.searchHolidayByDate(startStr, endStr);
        int size = holidayModels.size();

        //标准工作时间
        Double standardHours = Double.valueOf((daysBetween-size)*8);


        tip = "总计（不低于工作日工时）";
        HSSFCell lastCell = sheet.getRow(0).createCell(allUserSumCol);
        lastCell.setCellValue(tip);
        lastCell.setCellStyle(style);

        HSSFCell standardCell = sheet.getRow(1).createCell(allUserSumCol);
        standardCell.setCellValue("参考值\n\r"+standardHours);
        standardCell.setCellStyle(style);


        for(int i=4;i<=myAreaSumLocationRowNum;i++){
            HSSFCell mainSumCell = sheet.getRow(i).getCell(mainMaxColnum);
            Double mainSumHour = mainSumCell==null?0.0:mainSumCell.getNumericCellValue();
            HSSFCell otherSumCell = sheet.getRow(i).getCell(otherMaxColnum);
            Double otherSumHour = otherSumCell==null?0.0:otherSumCell.getNumericCellValue();
            HSSFCell leaveSumCell = sheet.getRow(i).getCell(leaveColnum);
            Double leaveSumHour ;
            if(leaveSumCell==null){
                leaveSumHour = 0.0;
            }else {
                leaveSumHour=leaveSumCell.getNumericCellValue();
            }
            HSSFCell cell = sheet.getRow(i).createCell(allUserSumCol);
            cell.setCellValue(mainSumHour+otherSumHour+leaveSumHour);
            cell.setCellStyle(style);
        }

        //最后补上其他区帮助本区的合计
        Double otherHelpHours = 0.0;
        for(int i=myAreaSumLocationRowNum+1;i<maxEnableRowNum;i++){
            HSSFRow mainSumRow = sheet.getRow(i);
            HSSFCell mainSumCell = mainSumRow.getCell(mainMaxColnum);
            Double mainSumHour;
            if(mainSumCell==null){
                mainSumRow.createCell(mainMaxColnum).setCellStyle(boderRightStyle);
                mainSumHour = 0.0;
            }else{
                mainSumHour = mainSumCell.getNumericCellValue();
            }
            otherHelpHours = otherHelpHours + mainSumHour;
        }
        HSSFRow maxRow = sheet.createRow(maxEnableRowNum);

        HSSFCell maxCell0 = maxRow.createCell(0);
        maxCell0.setCellValue("其他区帮本区工时合计");
        maxCell0.setCellStyle(style);

        HSSFCell maxCellMainSum = maxRow.createCell(mainMaxColnum);
        maxCellMainSum.setCellValue(otherHelpHours);
        maxCellMainSum.setCellStyle(boderRightStyle);







        //调用下载公用方法
        StringBuilder sb = new StringBuilder();
        sb.append("区域项目工时消耗统计表").append("-").append(areaInfo.getName()).append("（").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getStartTime()))).append("---")
                .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(reqVO.getEndTime()))).append("）");
        String fileName = sb.toString();

        //调用公共下载excel方法
        DownloadUtils.excelDownload(res, wb, fileName);

    }


}
