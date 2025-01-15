package com.nl.pm.server.controller;

import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IAreaRegistry;
import com.nl.pm.server.registry.IDayReportRegistry;
import com.nl.pm.server.registry.IProjectRegistry;
import com.nl.pm.server.registry.IUserRegistry;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.ProjectEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.ResEveryUserHolidayEntityParam;
import com.nl.pm.server.registry.param.ResEveryUserTotalTimeEntityParam;
import com.nl.pm.server.registry.param.ResEveryWorkTotalTimeEntityParam;
import com.nl.pm.server.registry.param.ResReportFormEntityParam;
import com.nl.pm.server.service.IDayReportService;
import com.nl.pm.server.service.IHolidayService;
import com.nl.pm.server.service.IProjectService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.HolidayModel;
import com.nl.pm.server.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

@RestController
@Api(value = "报表下载",tags = {"报表下载"})
@RequestMapping("/downloadReportForm")
public class DownloadReportFormController {
    private static final Logger log = LoggerFactory.getLogger(DownloadReportFormController.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private SecurityContextUtils securityContextUtils;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IDayReportService dayReportService;
    @Autowired
    private IDayReportRegistry dayReportRegistry;
    @Autowired
    private IProjectRegistry projectRegistry;
    @Autowired
    private IUserRegistry userRegistry;
    @Autowired
    private IHolidayService holidayService;
//    @Autowired
//    private IAreaService areaService;
    @Autowired
    private IAreaRegistry areaRegistry;


    @ApiOperation(value = "单报表详情下载",notes = "单报表详情下载")
    @PostMapping("/downReportFormDetails")
    public void downReportFormDetails(@RequestBody ReqReportFormByProjectVO vo, HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("utf-8");
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            Integer[] areaId = new Integer[]{securityContextUtils.getCurrentUserAreaId()};
            vo.setAreaId(areaId);
        }

        List<ReqReportFromByUserVO> list = toReqReportFromByUserVO(vo);

        if(list.size() == 0){
            throw new BaseServiceException(ServiceErrorCodeEnum.QUERY_NOT_DATA);
        }

        StringBuilder areaNameTar = null;
        List<AreaEntity> allAreaByIds = null;
        if(vo.getAreaId() != null && vo.getAreaId().length > 0){
            allAreaByIds = areaRegistry.getAllAreaByIds(vo.getAreaId());
        }
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(allAreaByIds)){
            areaNameTar = new StringBuilder();
            areaNameTar.append("区域：");
            for(AreaEntity model:allAreaByIds){
                areaNameTar.append(model.getName()).append("、");
            }
            areaNameTar.deleteCharAt(areaNameTar.length() - 1);
        }

        //查询每天工作时长
        List<ResEveryWorkTotalTimeEntityParam> listEveryDateWorkTotalTime = dayReportRegistry.queryEveryDateWorkTotalTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectName()[0]);

        //查询所有节假日
        List<HolidayModel> allHoliday = holidayService.findAllHoliday();

        //list和listEveryDateWorkTotalTime写入表格

        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");

        //设置样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        DecimalFormat strFormat = new DecimalFormat("0.00");

        //设置headStyle样式
        HSSFCellStyle headStyle = wb.createCellStyle();
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
        headStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);//前景填充色

        //设置bodyStyle样式
        HSSFCellStyle bodyStyle = wb.createCellStyle();
        bodyStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
        bodyStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);//前景填充色

        //冻结窗口设置
        sheet.createFreezePane(0,6,0,6);

        StringBuilder stringBuilder = new StringBuilder();
        //第一行
        HSSFCell cell = sheet.createRow(0).createCell(0);
        if(areaNameTar != null){
//            cell.setCellValue(vo.getProjectName()[0] + "时长统计" + " （" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())) + " --- "+
//                    DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，"+ areaNameTar +")");
            stringBuilder.append(vo.getProjectName()[0]).append("时长统计(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append(areaNameTar).append(")");
            cell.setCellValue(stringBuilder.toString());
        }else {
//            cell.setCellValue(vo.getProjectName()[0] + "时长统计" + " （" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())) + " --- "+
//                    DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，"+ "全区域" +")");
            stringBuilder.append(vo.getProjectName()[0]).append("时长统计(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，全区域").append(")");
            cell.setCellValue(stringBuilder.toString());
        }

        cell.setCellStyle(style);

        CellRangeAddress region = null;
        //设置起止行和列
        if(list.size() < 4){
            region = new CellRangeAddress(0, 0, 0, 5);
        }else {
            region = new CellRangeAddress(0, 0, 0, list.size() + 1);
        }

        sheet.addMergedRegion(region);

        //创建2-5行，并先给第一列赋值，并赋值样式headStyle
        HSSFRow row2 = sheet.createRow(1);
        HSSFRow row3 = sheet.createRow(2);
        HSSFRow row4 = sheet.createRow(3);
        HSSFRow row5 = sheet.createRow(4);
        row2.createCell(0).setCellValue("总人天");
        row3.createCell(0).setCellValue("总时长");
        row4.createCell(0).setCellValue("人天");
        row5.createCell(0).setCellValue("时长");
        row2.getCell(0).setCellStyle(headStyle);
        row3.getCell(0).setCellStyle(headStyle);
        row4.getCell(0).setCellStyle(headStyle);
        row5.getCell(0).setCellStyle(headStyle);

        //创建第六行，将名字赋值到第六行中
        HSSFRow row6 = sheet.createRow(5);
        row6.createCell(0).setCellValue("日期/姓名");
        row6.getCell(0).setCellStyle(headStyle);
        int lineNum = 1;
        int tag = list.size() + 1;
        int shortTag = 0;
        for(int i = 0; i < tag;i++){
            shortTag ++;
            if(shortTag == tag ){
                row6.createCell(lineNum++).setCellValue("小计");
                row6.getCell(lineNum-1).setCellStyle(headStyle);
            }else {
                row6.createCell(lineNum++).setCellValue(list.get(i).getNickname());
                row6.getCell(lineNum-1).setCellStyle(headStyle);
            }
        }


        //行
        int rowNum = 6;
        lineNum = 1;
        //写数据
        //多少行
        int size = list.get(0).getResReportFormByTimeVOS().size();
        for (int i = 0; i < size; i++) {
            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(list.get(0).getResReportFormByTimeVOS().get(i).getWorkDay());
            if(allHoliday.size() > 0){
                for (int j = 0; j < allHoliday.size(); j++) {
                    if(list.get(0).getResReportFormByTimeVOS().get(i).getWorkDay().equals(allHoliday.get(j).getDateStr())){
                        row.getCell(0).setCellStyle(headStyle);
                    }
                }
            }
            //写小计
            for (int j = 0; j < listEveryDateWorkTotalTime.size(); j++) {
                if(list.get(0).getResReportFormByTimeVOS().get(i).getWorkDay().equals(DateUtils.convertDateToStr(listEveryDateWorkTotalTime.get(j).getEveryDate()))){
//                    row.createCell(list.get(0).getResReportFormByTimeVOS().size()).setCellValue(listEveryDateWorkTotalTime.get(j).getEveryWorkTotalTime());
//                    row.getCell(list.get(0).getResReportFormByTimeVOS().size()).setCellStyle(headStyle);
                    row.createCell(list.size() + 1).setCellValue(listEveryDateWorkTotalTime.get(j).getEveryWorkTotalTime());
                    row.getCell(list.size() + 1).setCellStyle(headStyle);
                }
            }

            for (int j = 0; j < list.size(); j++) {
                row.createCell(lineNum).setCellValue(list.get(j).getResReportFormByTimeVOS().get(i).getWorkTime());
                if(allHoliday.size() > 0){
                    for (int a = 0; a < allHoliday.size(); a++) {
                        if(list.get(0).getResReportFormByTimeVOS().get(i).getWorkDay().equals(allHoliday.get(a).getDateStr())){
                            row.getCell(lineNum).setCellStyle(headStyle);
                        }
                    }
                }
                lineNum++;
            }
            lineNum = 1;
        }
        HSSFRow row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("总计");
        row.getCell(0).setCellStyle(headStyle);
        int lastCol = 1;
        for (int i = 0; i < list.size(); i++) {
            row.createCell(lastCol++).setCellValue(list.get(i).getUserTotalTime());
            row.getCell(lastCol-1).setCellStyle(headStyle);
            //lastCol++;
        }
        double total = 0.0;
        for(ReqReportFromByUserVO userVO:list){
            total += userVO.getUserTotalTime();
        }
        row.createCell(lastCol++).setCellValue(total);


        lineNum = 1;
        Double itemSumHours = 0.0;
        double itemSumPersonDay = 0.0;
        for(ReqReportFromByUserVO userVO :list){
            row5.createCell(lineNum++).setCellValue(userVO.getUserTotalTime());
            row4.createCell(lineNum-1).setCellValue(Double.parseDouble(String.format("%.2f",userVO.getUserTotalTime()/8)));
            itemSumHours += userVO.getUserTotalTime();
            itemSumPersonDay += userVO.getUserTotalTime()/8;
        }
        //给第二第三行，总人长和总时长赋值
        row2.createCell(1).setCellValue(Double.parseDouble(String.format("%.2f",itemSumPersonDay)));
        row3.createCell(1).setCellValue(itemSumHours);
        System.out.println("执行完成");
        // 设置response的Header

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        wb.write(outByteStream);

        StringBuilder builder = new StringBuilder();
        String fileName = null;
        if(areaNameTar != null){
//            fileName = vo.getProjectName()[0] + "("+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                    + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，" + areaNameTar + ")";
            fileName = builder.append(vo.getProjectName()[0]).append("(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append(areaNameTar).append(")").toString();
        }else {
//            fileName = vo.getProjectName()[0] + "("+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                    + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，" + "全区域" + ")";
            fileName = builder.append(vo.getProjectName()[0]).append("(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append("全区域").append(")").toString();
        }

//        String fileName = DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()));
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = res.getOutputStream();
        // 将文件输出
        wb.write(outputStream);
        outputStream.close();

        // 对文件名编码，防止中文文件乱码
//        fileName = getFilename(req, fileName);
//        // 设置响应头
//        HttpHeaders headers = new HttpHeaders();
//        // 通知浏览器以下载的方式打开文件
//        headers.setContentDispositionFormData("attachment", fileName);
//
//        // 定义以流的形式下载返回文件数据
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//
//        // 使用Spring MVC框架的ResponseEntity对象封装返回下载数据
//        return new ResponseEntity<byte[]>(outByteStream.toByteArray(), headers, HttpStatus.OK); // HttpStatus.OK 是200

    }


    @ApiOperation(value = "复杂查询下载",notes = "复杂查询下载")
    @PostMapping("/downComplexReportForm")
    public void downComplexReportForm(@RequestBody ReqComplexReportFormVO vo, HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("utf-8");
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            vo.setAreaId(null);
            Integer[] areaId = new Integer[]{securityContextUtils.getCurrentUserAreaId()};
            vo.setAreaId(areaId);
        }

        if(vo.getProjectId() != null){
            if(vo.getProjectId().length > 0){
                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_PROJECT_ERROR);
            }
        }


        List<ProjectEntity> modelList = projectRegistry.queryProjectByName(vo.getAreaId(),vo.getProjectName());
        if(modelList.size() > 0){
            Integer[] areaIds = new Integer[modelList.size()];
            for(int i = 0; i< modelList.size(); i++){
                areaIds[i] = modelList.get(i).getId();
            }
            vo.setProjectId(areaIds);
        }

        if(vo.getStartTime() == null){
            vo.setStartTime(DateUtils.convertDateToLong(DateUtils.convertStrToDate("1970-01-01")));
        }
        if(vo.getEndTime() == null){
            vo.setEndTime(DateUtils.convertDateToLong(DateUtils.convertStrToDate("9999-12-12")));
        }

        StringBuilder areaNameTar = null;
        List<AreaEntity> allAreaByIds = null;
        if(vo.getAreaId() != null && vo.getAreaId().length > 0){
            allAreaByIds = areaRegistry.getAllAreaByIds(vo.getAreaId());
        }
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(allAreaByIds)){
            areaNameTar = new StringBuilder();
            areaNameTar.append("区域：");
            for(AreaEntity model:allAreaByIds){
                areaNameTar.append(model.getName()).append("、");
            }
            areaNameTar.deleteCharAt(areaNameTar.length() - 1);
        }

        //查询所有项目名
        List<String> projectNameList = projectRegistry.queryProjectAndDistinct(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());

        //去重查询所有项目名为空的项目名
        List<String> projectIsNulltNameList = projectRegistry.queryProjectIsNullAndDistinct(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());
        if(CollectionUtils.isNotEmpty(projectIsNulltNameList)){
            for (String str : projectIsNulltNameList){
                projectNameList.remove(str);
            }
        }

        //查询每个人在每个项目所用的时间
        List<ResReportFormEntityParam> params = projectRegistry.queryEveryUserTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());


        //去重查询在项目的所有人
        List<UserEntity> userEntityList = userRegistry.queryUserProjectAndDistinct(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());

        //查询每个人总工时
        List<ResEveryUserTotalTimeEntityParam> listEveryUserTotalTime = userRegistry.queryEveryUserTotalTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());

        //过滤离开人员
        if(false == vo.getContainAwayUserFlag()){
            Map<Integer, ResEveryUserTotalTimeEntityParam> listEveryUserTotalTimeMap = new HashMap<>();
            for (ResEveryUserTotalTimeEntityParam resEveryUserTotalTimeEntityParam : listEveryUserTotalTime) {
                listEveryUserTotalTimeMap.put(resEveryUserTotalTimeEntityParam.getId(),resEveryUserTotalTimeEntityParam);
            }

            List<UserEntity> removeUserEntityList = new ArrayList<>();
            for (UserEntity userEntity : userEntityList) {
                Integer userId = userEntity.getId();
                if(!listEveryUserTotalTimeMap.containsKey(userId)){
                    if(userEntity.getStatus()==false){
                        removeUserEntityList.add(userEntity);
                    }
                }
            }
            userEntityList.removeAll(removeUserEntityList);
        }

        //每个人的假期
        List<ResEveryUserHolidayEntityParam> listEveryUserHoliday = userRegistry.queryEveryUserHoliday(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());
        if(listEveryUserHoliday.size() > 0){
            for(ResEveryUserHolidayEntityParam entityParam:listEveryUserHoliday){
                for (int i = 0; i < listEveryUserTotalTime.size(); i++) {
                    if(entityParam.getId().equals( listEveryUserTotalTime.get(i).getId())){
                        if(entityParam.getHolidayTime() == null){
                            entityParam.setHolidayTime(0.0);
                        }
                        listEveryUserTotalTime.get(i).setTotalWorkTime(listEveryUserTotalTime.get(i).getTotalWorkTime() + entityParam.getHolidayTime());
                    }
                }
            }
        }

        List<ResReportFormComplexVO> list = new ArrayList<>();
        if(projectNameList.size() > 0){
            //遍历项目
            for(String projectName:projectNameList){
                ResReportFormComplexVO vo2 = new ResReportFormComplexVO();
                vo2.setProjectName(projectName);
                //展示对象第一下进来
                List<ResReportFormComplexUserVO> list2 = new ArrayList<>();
                if(userEntityList.size() > 0){
                    //在项目的所有人
                    for(UserEntity userEntity:userEntityList){
                        ResReportFormComplexUserVO vo3 = new ResReportFormComplexUserVO();
                        vo3.setId(userEntity.getId());
                        vo3.setNickname(userEntity.getNickname());
                        if(params.size() > 0){
                            Integer count = 0;
                            //每个项目所用的时间
                            for(ResReportFormEntityParam param:params){
                                if (param != null){
                                    if(userEntity.getId().equals( param.getId())){
                                        if(projectName.equals(param.getProjectName())){
                                            vo3.setTotalTime(param.getTotalTime());
                                            list2.add(vo3);
                                            break;
                                        }
                                        count ++;
                                        continue;
                                    }
                                    count ++;
                                    continue;
                                }
                            }
                            if(count == params.size()){
                                vo3.setTotalTime(new Integer(0).doubleValue());
                                list2.add(vo3);
                            }
                        }
                    }

                }
                vo2.setResReportFormComplexUserVO(list2);
                list.add(vo2);
            }
        }

        for(int i = 0; i < list.size();i++){
            ResReportFormComplexVO resReportFormComplexVO = list.get(i);
            List<ResReportFormComplexUserVO> resReportFormComplexUserVO = resReportFormComplexVO.getResReportFormComplexUserVO();
            if(resReportFormComplexUserVO.size() == 0){
                list.get(i).setTotalTime(new Integer(0).doubleValue());
            }else {
                for(ResReportFormComplexUserVO userVO:resReportFormComplexUserVO){
                    if (list.get(i).getTotalTime() == null){
                        list.get(i).setTotalTime(new Integer(0).doubleValue());
                        list.get(i).setTotalTime(list.get(i).getTotalTime() + userVO.getTotalTime());
                    }else {
                        list.get(i).setTotalTime(list.get(i).getTotalTime() + userVO.getTotalTime());
                    }
                }
            }
        }

        if(list.size() == 0){
            throw new BaseServiceException(ServiceErrorCodeEnum.QUERY_NOT_DATA);
        }

    //导出 list   listEveryUserTotalTime  listEveryUserHoliday
        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");

        //冻结窗口设置
        sheet.createFreezePane(0,4,0,4);

        //第一行
        HSSFCell cell = sheet.createRow(0).createCell(0);
        if("1970-01-01".equals(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())))
        || "9999-12-12".equals(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())))){
            if(areaNameTar != null){
                cell.setCellValue("全时段项目工时统计(" + areaNameTar + ")");
            }else {
                cell.setCellValue("全时段项目工时统计(" + "全区域" + ")");
            }

        }else {
            StringBuilder stringBuilder = new StringBuilder();
            if(areaNameTar != null){
//                cell.setCellValue("全时段项目工时统计( 单位：小时，" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())) + " --- "+
//                        DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，" + areaNameTar+ ")");
                stringBuilder.append("全时段项目工时统计(单位：小时，").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                        .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append(areaNameTar).append(")");
                cell.setCellValue(stringBuilder.toString());
            }else {
//                cell.setCellValue("全时段项目工时统计( 单位：小时，" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())) + " --- "+
//                        DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，" + "全区域"+ ")");
                stringBuilder.append("全时段项目工时统计(单位：小时，").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                        .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append("全区域").append(")");
                cell.setCellValue(stringBuilder.toString());
            }

        }

        //设置起止行和列
        CellRangeAddress region = null;
        if(list.size() < 4){
            region = new CellRangeAddress(0, 0, 0, 5);
        }else {
            region = new CellRangeAddress(0, 0, 0, list.size() + 1);
        }
        sheet.addMergedRegion(region);

        //创建2-3行，并先给第一列赋值，并赋值样式headStyle
        HSSFRow row2 = sheet.createRow(1);
        HSSFRow row3 = sheet.createRow(2);

        row2.createCell(0).setCellValue(" ");
        row3.createCell(0).setCellValue("项目工时统计（小时）");

        //创建第4行，将名字赋值到第4行中
        HSSFRow row4 = sheet.createRow(3);
        row4.createCell(0).setCellValue("姓名/项目");

        int lineNum = 1;
        int tag = list.size() +1;
        int shortTag = 0;
        for (int i = 0; i < tag; i++) {
            shortTag ++;
            if(shortTag == tag ){
                row4.createCell(lineNum++).setCellValue("休假");
                shortTag ++;
            }
            if(shortTag > tag){
                row4.createCell(lineNum++).setCellValue("小计");
            }
            else {
                row4.createCell(lineNum++).setCellValue(list.get(i).getProjectName());
            }
        }

        //行
        int rowNum = 4;
        lineNum = 1;
        Double tagTotalTime = 0.0;
        Double holidayTotalTime = 0.0;
        Double totalTime = 0.0;
        //写数据
        if(userEntityList.size() > 0){
            for (int i = 0; i < userEntityList.size(); i++) {
                HSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(userEntityList.get(i).getNickname());
                //遍历项目找人
                for (int j = 0; j < list.size(); j++) {
                    if(list.get(j).getResReportFormComplexUserVO().size() == 0){
                        row.createCell(lineNum++).setCellValue(0.0);
                    }
                    else {
                        int count = 0;
                        for (int k = 0; k < list.get(j).getResReportFormComplexUserVO().size(); k++) {
                            if(userEntityList.get(i).getId().equals( list.get(j).getResReportFormComplexUserVO().get(k).getId())){
                                row.createCell(lineNum++).setCellValue(list.get(j).getResReportFormComplexUserVO().get(k).getTotalTime());
                                tagTotalTime += list.get(j).getResReportFormComplexUserVO().get(k).getTotalTime();
                                //totalTime += list.get(j).getResReportFormComplexUserVO().get(k).getTotalTime();
                                break;
                            }
                            count ++;
                        }
                        if(count == list.get(j).getResReportFormComplexUserVO().size()){
                            row.createCell(lineNum++).setCellValue(0.0);
                        }
                    }
                }

                //写休假
                if(listEveryUserHoliday.size() == 0){
                    row.createCell(lineNum++).setCellValue(0.0);
                }else {
                    int count = 0;
                    for (int j = 0; j < listEveryUserHoliday.size(); j++) {
                        if(userEntityList.get(i).getId().equals( listEveryUserHoliday.get(j).getId())){
                            if(listEveryUserHoliday.get(j).getHolidayTime() == null){
                                listEveryUserHoliday.get(j).setHolidayTime(0.0);
                            }
                            row.createCell(lineNum++).setCellValue(listEveryUserHoliday.get(j).getHolidayTime());
                            tagTotalTime += listEveryUserHoliday.get(j).getHolidayTime();
                            holidayTotalTime += listEveryUserHoliday.get(j).getHolidayTime();
                            break;
                        }
                        count ++;
                    }
                    if(count == listEveryUserHoliday.size()){
                        row.createCell(lineNum++).setCellValue(0.0);
                    }
                }

                //写小计
                row.createCell(lineNum++).setCellValue(tagTotalTime);
                tagTotalTime = 0.0;
                lineNum = 1;
            }

        }


        //总计
        HSSFRow rowLast = sheet.createRow(rowNum++);
        rowLast.createCell(0).setCellValue("总计");
        for (int i = 0; i < list.size(); i++) {
            rowLast.createCell(lineNum++).setCellValue(list.get(i).getTotalTime());
            totalTime += list.get(i).getTotalTime();
        }
        rowLast.createCell(lineNum++).setCellValue(holidayTotalTime);
        rowLast.createCell(lineNum++).setCellValue(totalTime + holidayTotalTime);


        System.out.println("执行完成");

        // 设置response的Header
        String fileName = null;

        if("1970-01-01".equals(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())))
                || "9999-12-12".equals(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())))){
            if(areaNameTar != null){
                fileName = "全时段项目工时统计(小时---"+ areaNameTar + ")";
            }else {
                fileName = "全时段项目工时统计(小时---"+ "全区域" + ")";
            }
        }else {
            StringBuilder stringBuilder = new StringBuilder();
            if(areaNameTar != null){
//                fileName = DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                        + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "(" +areaNameTar+ ")";
                fileName = stringBuilder.append("全时段项目工时统计(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                        .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append(areaNameTar).append(")").toString();
            }else {
//                fileName = DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                        + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "(" +"全区域"+ ")";
                fileName = stringBuilder.append("全时段项目工时统计(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                        .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append("全区域").append(")").toString();
            }
        }

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = res.getOutputStream();
        // 将文件输出
        wb.write(outputStream);
        outputStream.close();


//        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
//        wb.write(outByteStream);
//
//        String fileName = DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//               + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + ".xls";
//
//        // 对文件名编码，防止中文文件乱码
//        fileName = getFilename(req, fileName);
//        // 设置响应头
//        HttpHeaders headers = new HttpHeaders();
//        // 通知浏览器以下载的方式打开文件
//        headers.setContentDispositionFormData("attachment", fileName);
//
//        // 定义以流的形式下载返回文件数据
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//
//        // 使用Spring MVC框架的ResponseEntity对象封装返回下载数据
//        return new ResponseEntity<byte[]>(outByteStream.toByteArray(), headers, HttpStatus.OK); // HttpStatus.OK 是200
    }


    @ApiOperation(value = "通过项目单报表详情下载",notes = "通过项目单报表详情下载")
    @PostMapping("/downReportFormDetailsByProject")
    public void downReportFormDetailsByProject(@RequestBody ReqReportFormByProjectVO vo, HttpServletRequest req, HttpServletResponse res) throws Exception {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("utf-8");

        List<ReqReportFromByUserVO> list = toReqReportFromByUserVO(vo);

        if(list.size() == 0){
            throw new BaseServiceException(ServiceErrorCodeEnum.QUERY_NOT_DATA);
        }
        //查询每天工作时长
        List<ResEveryWorkTotalTimeEntityParam> listEveryDateWorkTotalTime = dayReportRegistry.queryEveryDateWorkTotalTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectName()[0]);

        StringBuilder areaName = null;
        List<AreaEntity> allAreaByIds = null;
        if(vo.getAreaId() != null && vo.getAreaId().length > 0){
            allAreaByIds = areaRegistry.getAllAreaByIds(vo.getAreaId());
        }
        if(CollectionUtils.isNotEmpty(allAreaByIds)){
            areaName = new StringBuilder();
            areaName.append("区域：");
            for(AreaEntity model:allAreaByIds){
                areaName.append(model.getName()).append("、");
            }
            areaName.deleteCharAt(areaName.length() - 1);
        }


        //查询所有节假日
        List<HolidayModel> allHoliday = holidayService.findAllHoliday();

        //list和listEveryDateWorkTotalTime写入表格

        //创建HSSFWorkbook对象
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建HSSFSheet对象
        HSSFSheet sheet = wb.createSheet("sheet0");

        //设置样式
        HSSFCellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        DecimalFormat strFormat = new DecimalFormat("0.00");

        //设置headStyle样式
        HSSFCellStyle headStyle = wb.createCellStyle();
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
        headStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);//前景填充色

        //设置bodyStyle样式
        HSSFCellStyle bodyStyle = wb.createCellStyle();
        bodyStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置前景填充样式
        bodyStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);//前景填充色

        //冻结窗口设置
        sheet.createFreezePane(0,6,0,6);

        //第一行
        HSSFCell cell = sheet.createRow(0).createCell(0);
        StringBuilder stringBuilder = new StringBuilder();
        if(areaName != null){
//            cell.setCellValue(vo.getProjectName()[0] + "时长统计" + " （" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())) + " --- "+
//                    DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，" + areaName + ")");
            stringBuilder.append(vo.getProjectName()[0]).append("时长统计(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append(areaName).append(")");
            cell.setCellValue(stringBuilder.toString());
        }else {
//            cell.setCellValue(vo.getProjectName()[0] + "时长统计" + " （" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())) + " --- "+
//                    DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，全区域" + ")");
            stringBuilder.append(vo.getProjectName()[0]).append("时长统计(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，全区域").append(")");
            cell.setCellValue(stringBuilder.toString());
        }

        cell.setCellStyle(style);

        //设置起止行和列
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, list.get(0).getResReportFormByTimeVOS().size() + 1);
        sheet.addMergedRegion(region);

        //创建2-5行，并先给第一列赋值，并赋值样式headStyle
        HSSFRow row2 = sheet.createRow(1);
        HSSFRow row3 = sheet.createRow(2);
        HSSFRow row4 = sheet.createRow(3);
        HSSFRow row5 = sheet.createRow(4);
        row2.createCell(0).setCellValue("总人天");
        row3.createCell(0).setCellValue("总时长");
        row4.createCell(0).setCellValue("人天");
        row5.createCell(0).setCellValue("时长");
        row2.getCell(0).setCellStyle(headStyle);
        row3.getCell(0).setCellStyle(headStyle);
        row4.getCell(0).setCellStyle(headStyle);
        row5.getCell(0).setCellStyle(headStyle);

        //创建第六行，将名字赋值到第六行中
        HSSFRow row6 = sheet.createRow(5);
        row6.createCell(0).setCellValue("日期/姓名");
        row6.getCell(0).setCellStyle(headStyle);
        int lineNum = 1;
        int tag = list.size() + 1;
        int shortTag = 0;
        for(int i = 0; i < tag;i++){
            shortTag ++;
            if(shortTag == tag ){
                row6.createCell(lineNum++).setCellValue("小计");
                row6.getCell(lineNum-1).setCellStyle(headStyle);
            }else {
                row6.createCell(lineNum++).setCellValue(list.get(i).getNickname());
                row6.getCell(lineNum-1).setCellStyle(headStyle);
            }
        }


        //行
        int rowNum = 6;
        lineNum = 1;
        //写数据
        //多少行
        int size = list.get(0).getResReportFormByTimeVOS().size();
        for (int i = 0; i < size; i++) {
            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(list.get(0).getResReportFormByTimeVOS().get(i).getWorkDay());
            if(allHoliday.size() > 0){
                for (int j = 0; j < allHoliday.size(); j++) {
                    if(list.get(0).getResReportFormByTimeVOS().get(i).getWorkDay().equals(allHoliday.get(j).getDateStr())){
                        row.getCell(0).setCellStyle(headStyle);
                    }
                }
            }
            //写小计
            for (int j = 0; j < listEveryDateWorkTotalTime.size(); j++) {
                if(list.get(0).getResReportFormByTimeVOS().get(i).getWorkDay().equals(DateUtils.convertDateToStr(listEveryDateWorkTotalTime.get(j).getEveryDate()))){
//                    row.createCell(list.get(0).getResReportFormByTimeVOS().size()).setCellValue(listEveryDateWorkTotalTime.get(j).getEveryWorkTotalTime());
//                    row.getCell(list.get(0).getResReportFormByTimeVOS().size()).setCellStyle(headStyle);
                    row.createCell(list.size() + 1).setCellValue(listEveryDateWorkTotalTime.get(j).getEveryWorkTotalTime());
                    row.getCell(list.size() + 1).setCellStyle(headStyle);
                }
            }

            for (int j = 0; j < list.size(); j++) {
                row.createCell(lineNum).setCellValue(list.get(j).getResReportFormByTimeVOS().get(i).getWorkTime());
                if(allHoliday.size() > 0){
                    for (int a = 0; a < allHoliday.size(); a++) {
                        if(list.get(0).getResReportFormByTimeVOS().get(i).getWorkDay().equals(allHoliday.get(a).getDateStr())){
                            row.getCell(lineNum).setCellStyle(headStyle);
                        }
                    }
                }
                lineNum++;
            }
            lineNum = 1;
        }
        HSSFRow row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("总计");
        row.getCell(0).setCellStyle(headStyle);
        int lastCol = 1;
        for (int i = 0; i < list.size(); i++) {
            row.createCell(lastCol++).setCellValue(list.get(i).getUserTotalTime());
            row.getCell(lastCol-1).setCellStyle(headStyle);
            //lastCol++;
        }
        double total = 0.0;
        for(ReqReportFromByUserVO userVO:list){
            total += userVO.getUserTotalTime();
        }
        row.createCell(lastCol++).setCellValue(total);


        lineNum = 1;
        Double itemSumHours = 0.0;
        double itemSumPersonDay = 0.0;
        for(ReqReportFromByUserVO userVO :list){
            row5.createCell(lineNum++).setCellValue(userVO.getUserTotalTime());
            row4.createCell(lineNum-1).setCellValue(Double.parseDouble(String.format("%.2f",userVO.getUserTotalTime()/8)));
            itemSumHours += userVO.getUserTotalTime();
            itemSumPersonDay += userVO.getUserTotalTime()/8;
        }
        //给第二第三行，总人长和总时长赋值
        row2.createCell(1).setCellValue(Double.parseDouble(String.format("%.2f",itemSumPersonDay)));
        row3.createCell(1).setCellValue(itemSumHours);
        System.out.println("执行完成");
        // 设置response的Header

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        wb.write(outByteStream);

        String fileName = null;
        StringBuilder builder = new StringBuilder();

        if(areaName != null){
//            fileName = vo.getProjectName()[0] + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                    + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) +"(" + areaName +")";
            fileName = builder.append(vo.getProjectName()[0]).append("(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append(areaName).append(")").toString();
        }else {
//            fileName = vo.getProjectName()[0] + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
////                    + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) +"(" + "全区域" +")";
            fileName = builder.append(vo.getProjectName()[0]).append("(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，全区域").append(")").toString();
        }
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = res.getOutputStream();
        // 将文件输出
        wb.write(outputStream);
        outputStream.close();

        // 对文件名编码，防止中文文件乱码
//        fileName = getFilename(req, fileName);
//        // 设置响应头
//        HttpHeaders headers = new HttpHeaders();
//        // 通知浏览器以下载的方式打开文件
//        headers.setContentDispositionFormData("attachment", fileName);
//
//        // 定义以流的形式下载返回文件数据
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//
//        // 使用Spring MVC框架的ResponseEntity对象封装返回下载数据
//        return new ResponseEntity<byte[]>(outByteStream.toByteArray(), headers, HttpStatus.OK); // HttpStatus.OK 是200

    }

    private String getFilename(HttpServletRequest request, String fileName) throws Exception {
        // IE不同版本User-Agent中出现的关键词
        String[] IEBrowserKeyWords = {"MSIE", "Trident", "Edge"};
        // 获取请求头代理信息
        String userAgent = request.getHeader("User-Agent");
        for (String keyWord : IEBrowserKeyWords) {
            if (userAgent.contains(keyWord)) {
                //IE内核浏览器，统一为UTF-8编码显示
                return URLEncoder.encode(fileName, "UTF-8");
            }
        }
        //火狐等其它浏览器统一为ISO-8859-1编码显示
        return new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
    }


    public Boolean checkArrIsRepeat(String[] str){
        String projectName = str[0];
        for(int i = 1; i< str.length; i++){
            if(!projectName.equals(str[i])){
                return false;
            }
        }
        return true;
    }

    private List<ReqReportFromByUserVO> toReqReportFromByUserVO(ReqReportFormByProjectVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ReqReportFromByUserVO> list = new ArrayList<>();

        if(StringUtils.isEmpty(vo.getCurrentProjectName())){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_PROJECT_ERROR);
        }
        if(vo.getProjectName() != null ){
            if(vo.getProjectName().length > 0)
                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_PROJECT_ERROR);
        }
        String[] currentProjectName = new String[]{vo.getCurrentProjectName()};
        vo.setProjectName(currentProjectName);

//        if(!checkArrIsRepeat(vo.getProjectName())){
//            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_PROJECT_ERROR);
//        }

        //查询每个人的工作时长
        List<UserEntity> userEntityList = userRegistry.queryUserWorkTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectName()[0]);

        //查询人员
        List<UserEntity> userEntities = userRegistry.queryUserDistinctByTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectName()[0]);

        //查询时间
        List<Date> dateList = dayReportRegistry.queryTimeDistinctByTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectName()[0]);
        Collections.sort(dateList);





        if(userEntities.size() > 0){
            //遍历人
            for (UserEntity userEntity:userEntities){
                ReqReportFromByUserVO vo2 = new ReqReportFromByUserVO();
                vo2.setId(userEntity.getId());
                vo2.setNickname(userEntity.getNickname());
                if(dateList.size() > 0){
                    List<ResReportFormByTimeVO> list2 = new ArrayList<>();
                    //遍历时间
                    for(Date date:dateList){
                        ResReportFormByTimeVO vo3 = new ResReportFormByTimeVO();
                        vo3.setWorkDay(DateUtils.convertDateToStr(date));

                        if(userEntityList.size() > 0){
                            Integer count = 0;
                            //找人赋值
                            for(UserEntity userEntity2:userEntityList){
                                if(userEntity.getId().equals( userEntity2.getId())){
                                    if(DateUtils.convertDateToStr(date).equals(DateUtils.convertDateToStr(userEntity2.getWorkDay()))){
                                        vo3.setWorkTime(userEntity2.getTotalTime());
                                        list2.add(vo3);
                                        break;
                                    }
                                    count ++;
                                    continue;
                                    //break;
                                }
                                count ++;
                                continue;
                                //vo3.setWorkTime(new Integer(0).doubleValue());
                            }
                            if(count == userEntityList.size()){
                                vo3.setWorkTime(new Integer(0).doubleValue());
                                list2.add(vo3);
                            }

                        }

                    }
                    vo2.setResReportFormByTimeVOS(list2);
                }
                list.add(vo2);
            }
        }


        for (int i = 0; i < list.size(); i++) {
            ReqReportFromByUserVO reqReportFromByUserVO = list.get(i);
            list.get(i).setProjectName(vo.getProjectName()[0]);
            List<ResReportFormByTimeVO> resReportFormByTimeVOS = list.get(i).getResReportFormByTimeVOS();
            if(resReportFormByTimeVOS.size() == 0){
                list.get(i).setUserTotalTime(new Integer(0).doubleValue());
            }else {
                for(ResReportFormByTimeVO resReportFormByTimeVO:resReportFormByTimeVOS){
                    if( list.get(i).getUserTotalTime() == null){
                        list.get(i).setUserTotalTime(new Integer(0).doubleValue());
                        list.get(i).setUserTotalTime(list.get(i).getUserTotalTime() + resReportFormByTimeVO.getWorkTime());
                    }else {
                        list.get(i).setUserTotalTime(list.get(i).getUserTotalTime() + resReportFormByTimeVO.getWorkTime());
                    }
                }
            }
        }

        return list;
    }

}
