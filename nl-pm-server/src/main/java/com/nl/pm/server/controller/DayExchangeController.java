package com.nl.pm.server.controller;

import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IAreaRegistry;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.service.IDayExchangeService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.DayExchangeAdvanceModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.SearchDayExchangeModelParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Api(value = "倒休管理",tags = {"倒休管理"})
@RequestMapping("/dayExchange")
public class DayExchangeController {
    @Autowired
    private SecurityContextUtils securityContextUtils;
    @Autowired
    private IDayExchangeService dayExchangeService;

    @Autowired
    private IUserService userService;
    @Autowired
    private IAreaRegistry areaRegistry;

    @ApiOperation(value = "查询人员调休列表", notes = "查询人员调休列表")
    @PostMapping("/list")
    public BasePagesDomain<ResDayExchangeVO> searchDayExchangeList(@RequestBody ReqSearchDayExchangeVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        SearchDayExchangeModelParam param = new SearchDayExchangeModelParam();

        if(currentUserRole == RoleTypeEnum.AREA_MANAGER){
            if(reqVO.getAreaId()!=null && !reqVO.getAreaId().equals(currentUserAreaId)){
                throw new BaseServiceException(ServiceErrorCodeEnum.EXCHANGE_AREA_ERROR);
            }else {
                reqVO.setAreaId(currentUserAreaId);
            }
        }

        if(currentUserRole == RoleTypeEnum.GROUP_MANAGER || currentUserRole == RoleTypeEnum.EMPLOYEE){
            param.setUserId(currentUserId);
        }

        param.setCurrentPage(reqVO.getCurrentPage());
        param.setPageSize(reqVO.getPageSize());
        param.setAreaId(reqVO.getAreaId());
        param.setNickname(reqVO.getNickname());
        param.setSearchVal(reqVO.getSearchVal());
        BasePagesDomain<DayExchangeAdvanceModel> resPages = dayExchangeService.searchDayExchangeList(param);

        List<ResDayExchangeVO> listVO = new ArrayList<>();
        List<DayExchangeAdvanceModel> totalList = resPages.getTotalList();
        BasePagesDomain<ResDayExchangeVO> resVO = new BasePagesDomain<>(resPages.getCurrentPage(),resPages.getPageSize(),resPages.getTotalPage(),resPages.getTotal());
        if(!CollectionUtils.isEmpty(totalList)){
            for (DayExchangeAdvanceModel model : totalList) {
                ResDayExchangeVO resDayExchangeVO = new ResDayExchangeVO();
                resDayExchangeVO.setAreaId(model.getAreaId());
                resDayExchangeVO.setAreaName(model.getAreaName());
                resDayExchangeVO.setUserId(model.getUserId());
                resDayExchangeVO.setNickname(model.getNickname());
                resDayExchangeVO.setLeaveHour(model.getLeaveHour());
                resDayExchangeVO.setWorkHour(model.getWorkHour());
                resDayExchangeVO.setOverHour(model.getOverHour());
                resDayExchangeVO.setExchangeHour(model.getExchangeHour());
                listVO.add(resDayExchangeVO);

            }

        }
        resVO.setTotalList(listVO);
        return resVO;

    }

    @ApiOperation(value = "查询单个人员调休详细内容", notes = "查询单个人员调休详细内容")
    @GetMapping("/detail/{id}")
    public List<ResDayExchangeDetailVO> searchDetail(@PathVariable("id") Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();

        if(currentUserRole == RoleTypeEnum.GROUP_MANAGER || currentUserRole == RoleTypeEnum.EMPLOYEE){
            if(!currentUserId.equals(userId)){
                throw new BaseServiceException(ServiceErrorCodeEnum.EXCHANGE_OTHER_ERROR);
            }
        }

        if(currentUserRole == RoleTypeEnum.AREA_MANAGER){
            UserModel userModel = userService.queryUserInfoById(userId.longValue());
            if(userModel == null ){
                throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_EXIST);
            }
            Integer areaId = userModel.getAreaId();
            if(!currentUserAreaId.equals(areaId)){
                throw new BaseServiceException(ServiceErrorCodeEnum.EXCHANGE_AREA_ERROR);
            }
        }

        List<ResDayExchangeDetailVO> resVOList =new ArrayList<>();

        List<DayExchangeAdvanceModel> modelList = dayExchangeService.searchDetail(userId);
        if(!CollectionUtils.isEmpty(modelList)){
            for (DayExchangeAdvanceModel model : modelList) {
                ResDayExchangeDetailVO vo = new ResDayExchangeDetailVO();
                vo.setDate(DateUtils.convertDateToLong(model.getDate()));
                vo.setLeaveHour(model.getLeaveHour());
                vo.setDesc(model.getDesc());
                resVOList.add(vo);
            }
        }
        return resVOList;
    }

    @ApiOperation(value = "休假报表下载",notes = "休假报表下载")
    @PostMapping("/downDayExchangeForm")
    public void downDayExchangeForm(@RequestBody ReqDownDayExchangeVO vo, HttpServletRequest req, HttpServletResponse res) throws IOException, BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        req.setCharacterEncoding("utf-8");
        res.setCharacterEncoding("utf-8");

        StringBuilder areaName = null;
        List<AreaEntity> allAreaByIds = null;
        if(vo.getAreaId() != null && vo.getAreaId().size() > 0){
            allAreaByIds = areaRegistry.getAllAreaByIds(vo.getAreaId().stream().toArray(Integer[]::new));
        }
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(allAreaByIds)){
            areaName = new StringBuilder();
            areaName.append("区域：");
            for(AreaEntity model:allAreaByIds){
                areaName.append(model.getName()).append("、");
            }
            areaName.deleteCharAt(areaName.length() - 1);
        }

        if(vo.getStartTime() == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_START_TIME_NULL_ERROR);
        }
        if(vo.getEndTime() == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_END_TIME_NULL_ERROR);
        }
        List<ResDownDayExchangeVO> list = new ArrayList<>();

        //查询时间
        List<Date> dateList = dayExchangeService.queryDistinctTime(vo.getAreaId(), vo.getUserId(),
                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())),
                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())));
        if(CollectionUtils.isEmpty(dateList)){
            throw new BaseServiceException(ServiceErrorCodeEnum.QUERY_NOT_DATA);
        }
        List<String> dateStr = new ArrayList<>();
        for (Date date:dateList){
            dateStr.add(DateUtils.convertDateToStr(date));
        }
        //查询人
        List<UserModel> userModelList = dayExchangeService.queryDistinctUser(vo.getAreaId(), vo.getUserId(),
                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())),
                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())));

        //查询所有人与时间的请假结果
        List<UserModel> userModelListByTime = dayExchangeService.queryDistinctUserByTime(vo.getAreaId(), vo.getUserId(),
                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())),
                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())));

        //组合
        for (int i = 0; i < userModelList.size(); i++) {
            ResDownDayExchangeVO resVO = new ResDownDayExchangeVO();
            resVO.setUserId(userModelList.get(i).getId());
            resVO.setNickname(userModelList.get(i).getNickname());
            List<ResDownDayExchangeDateVO> resDownDayExchangeDateVOS = new ArrayList<>();
            for (int j = 0; j < dateStr.size(); j++) {
                ResDownDayExchangeDateVO resDownDayExchangeDateVO = new ResDownDayExchangeDateVO();
                resDownDayExchangeDateVO.setDate(dateStr.get(j));
                int cycleNum = 0;
                int size = userModelListByTime.size();
                Boolean flg = true;
                for (int k = 0; k < size; k++) {
                    cycleNum ++;
                    if(dateStr.get(j).equals(DateUtils.convertDateToStr(userModelListByTime.get(k).getEveryLeaveTime()))
                    && userModelList.get(i).getId().equals(userModelListByTime.get(k).getId())){
                        resDownDayExchangeDateVO.setExchange(userModelListByTime.get(k).getEveryLeaveHour());
                        flg = false;
                        break;
                    }
                }
                if(flg && cycleNum == size){
                    resDownDayExchangeDateVO.setExchange(0.0);
                }
                resDownDayExchangeDateVOS.add(resDownDayExchangeDateVO);
            }
            resVO.setDateVOList(resDownDayExchangeDateVOS);
            list.add(resVO);

        }

        int size = list.size();
        for (int i = 0; i < size; i++) {
            Double totalHours = 0.0;
            for (int j = 0; j < list.get(i).getDateVOList().size(); j++) {
                totalHours += list.get(i).getDateVOList().get(j).getExchange();
            }
            list.get(i).setTotalExchange(totalHours);
        }

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
        sheet.createFreezePane(1,2,1,2);

        //第一行
        HSSFCell cell = sheet.createRow(0).createCell(0);
        StringBuilder stringBuilder = new StringBuilder();
        if(areaName != null){
//            cell.setCellValue( "休假报表" + " （" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())) + " --- "+
//                    DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) +  "，" + areaName + ")");
            stringBuilder.append("休假报表").append("（").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，").append(areaName).append(")");
            cell.setCellValue(stringBuilder.toString());
        }else {
//            cell.setCellValue( "休假报表" + " （" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())) + " --- "+
//                    DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) + "，全区域" + ")");
            stringBuilder.append("休假报表").append("（").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append("，全区域").append(")");
            cell.setCellValue(stringBuilder.toString());
        }

        cell.setCellStyle(style);

        //设置起止行和列
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, list.size());
        sheet.addMergedRegion(region);

        HSSFRow row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("日期/姓名");
        row2.getCell(0).setCellStyle(headStyle);

        int lineNum = 1;
        for (int i = 0; i < size; i++) {
            row2.createCell(lineNum++).setCellValue(list.get(i).getNickname());
            row2.getCell(lineNum-1).setCellStyle(headStyle);
        }
        //行
        int rowNum = 2;
        lineNum = 1;
        //多少行
        int rowSize = list.get(0).getDateVOList().size();
        for (int i = 0; i < rowSize; i++) {
            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(list.get(0).getDateVOList().get(i).getDate());
            for (int j = 0; j < size; j++) {
                row.createCell(lineNum++).setCellValue(list.get(j).getDateVOList().get(i).getExchange());
            }
            lineNum = 1;
        }

        HSSFRow row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue("总计");
        row.getCell(0).setCellStyle(headStyle);
        int lastCol = 1;
        Double total = 0.0;
        for (int i = 0; i < size; i++) {
            total += list.get(i).getTotalExchange();
            row.createCell(lastCol++).setCellValue(list.get(i).getTotalExchange());
            row.getCell(lastCol-1).setCellStyle(headStyle);
        }
        row.createCell(lastCol++).setCellValue(total);

        System.out.println("执行完成");
        // 设置response的Header

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        wb.write(outByteStream);

        String fileName = null;
        StringBuilder builder = new StringBuilder();

        if(areaName != null){
//            fileName ="休假报表" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                    + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))+"(" + areaName +")";
            fileName = builder.append("休假报表(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append(",").append(areaName).append(")").toString();
        }else {
//            fileName ="休假报表" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                    + " --- "+DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())) +"(" + "全区域" +")";
            fileName = builder.append("休假报表(").append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))).append("---")
                    .append(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime()))).append(",全区域").append(")").toString();
        }

        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = res.getOutputStream();
        // 将文件输出
        wb.write(outputStream);
        outputStream.close();

    }

    @ApiOperation(value = "查询人员请假合计列表", notes = "查询人员请假合计列表")
    @PostMapping("/leave/list")
    public BasePagesDomain<ResLeaveVO> searchLeaveList(@RequestBody ReqSearchDayExchangeVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        BasePagesDomain<ResDayExchangeVO> resDayExchangeVOBasePagesDomain = searchDayExchangeList(reqVO);
        List<ResDayExchangeVO> totalList = resDayExchangeVOBasePagesDomain.getTotalList();
        List<ResLeaveVO> voList = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(totalList)){
            for (ResDayExchangeVO resDayExchangeVO : totalList) {
                ResLeaveVO resLeaveVO = new ResLeaveVO();
                resLeaveVO.setUserId(resDayExchangeVO.getUserId());
                resLeaveVO.setAreaId(resDayExchangeVO.getAreaId());
                resLeaveVO.setNickname(resDayExchangeVO.getNickname());
                resLeaveVO.setAreaName(resDayExchangeVO.getAreaName());
                resLeaveVO.setLeaveHour(resDayExchangeVO.getLeaveHour());
                voList.add(resLeaveVO);
            }
        }
        BasePagesDomain<ResLeaveVO> resLeaveVOBasePagesDomain = new BasePagesDomain<ResLeaveVO>();
        resLeaveVOBasePagesDomain.setTotalList(voList);
        resLeaveVOBasePagesDomain.setPageSize(resDayExchangeVOBasePagesDomain.getPageSize());
        resLeaveVOBasePagesDomain.setCurrentPage(resDayExchangeVOBasePagesDomain.getCurrentPage());
        resLeaveVOBasePagesDomain.setTotalPage(resDayExchangeVOBasePagesDomain.getTotalPage());
        resLeaveVOBasePagesDomain.setTotal(resDayExchangeVOBasePagesDomain.getTotal());
        return resLeaveVOBasePagesDomain;
    }

}