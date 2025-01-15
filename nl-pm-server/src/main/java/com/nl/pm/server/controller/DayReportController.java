package com.nl.pm.server.controller;

import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.ProjectUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.AreaEnum;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.DynamicServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.service.*;
import com.nl.pm.server.service.model.*;
import com.nl.pm.server.service.param.DayReportSearchModelParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(value = "日报管理", tags = {"日报管理"})
@RequestMapping("/dayReport")
public class DayReportController {
    @Autowired
    private IDayReportService iDayReportService;

    @Autowired
    private IProjectService iProjectService;
    @Autowired
    private IHolidayService iHolidayService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Autowired
    private ISystemVariableService iSystemVariableService;

    @ApiOperation(value = "创建日报", notes = "创建日报")
    @PostMapping("/create")
    public void createDayReport(@RequestBody ReqCreateDayReportVO reqVO) throws Exception {
        if (reqVO.getDate() == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_DATE_NULL_ERROR);
        }

        ReqSearchDayReportVO vo = new ReqSearchDayReportVO();
        //默认时间2021-09-01
        vo.setStartDate(1630425600000L);
        Calendar    rightNow    =    Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        String today = year+"-"+month+"-"+day;
        Date date = DateUtils.convertStrToDate(today);

        if(reqVO.getDate() >= date.getTime()) {
            vo.setEndDate(date.getTime() - 1);
            vo.setUserId(securityContextUtils.getCurrentUserId());
            vo.setCurrentPage(1);
            vo.setPageSize(9999999);
            BasePagesDomain<ResNoneReportVO> resNoneReportVOBasePagesDomain = searchNoneDayReportList(vo);
            if (resNoneReportVOBasePagesDomain.getTotal() > 0) {
                throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_OLD_NULL_ERROR);
            }
        }


        if (reqVO.getDate() > new Date().getTime()) {
            ReqLeaveVO leaveVO = reqVO.getLeaveVO();
            if (leaveVO == null || leaveVO.getLeaveHours() == null || leaveVO.getLeaveHours() != 8.0) {
                throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_FUTURE_ERROR);
            }
            List<ReqDayReportVO> dayReportList = reqVO.getDayReportList();
            if (!CollectionUtils.isEmpty(dayReportList)) {
                for (ReqDayReportVO reqDayReportVO : dayReportList) {
                    if (reqDayReportVO != null) {
                        Integer projectId = reqDayReportVO.getProjectId();
                        Double hours1 = reqDayReportVO.getHours();
                        if (projectId != null || hours1 != null) {
                            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_FUTURE_ERROR);
                        }
                    }
                }
            }
        }

        Double hours = 0.0;

        List<Integer> projectIdsByName = iProjectService.getProjectIdsByName(ProjectUtils.PROJECT_OTHER_ACTION);
        Set<Integer> otherProjSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(projectIdsByName)){
            for (Integer integer : projectIdsByName) {
                otherProjSet.add(integer);
            }

        }

        List<ProjectModel> forceProjectList = iProjectService.searchAllForceDescProject();
        Set<Integer> forceProjectSet = new HashSet<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(forceProjectList)){
            for (ProjectModel projectModel : forceProjectList) {
                forceProjectSet.add(projectModel.getId());
            }
        }

        Set<Integer> projectCheckSet = new HashSet<>();
        List<DayReportTaskModel> taskModelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(reqVO.getDayReportList())) {
            for (ReqDayReportVO dayReportTaskVO : reqVO.getDayReportList()) {
                Integer projectId = dayReportTaskVO.getProjectId();
                Double hours1 = dayReportTaskVO.getHours();
                if(projectId == null && hours1 == null){
                    continue;
                }

                if(projectId==null){
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_PROJECT_ID_NULL_ERROR);
                }
                if(hours1==null){
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_PROJECT_TIME_NULL_ERROR);
                }

                if (projectCheckSet.contains(projectId)) {
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_PROJECT_REPEAT_ERROR);
                } else {
                    projectCheckSet.add(projectId);
                }
                String desc = dayReportTaskVO.getDesc();
//                if(otherProjSet.contains(projectId)){
//                    if(StringUtils.isEmpty(desc)){
//                        throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_OTHER_NULL_ERROR);
//                    }
//                }

                if(forceProjectSet.contains(projectId)){
                    if(StringUtils.isEmpty(desc)){
                        ProjectAdvanceModel projectAdvanceModel = iProjectService.fetchProject(projectId);
                        throw new DynamicServiceException("【"+projectAdvanceModel.getName()+"】项目已被负责人设置为强制描述，请填写描述内容");
                    }
                }

                DayReportTaskModel dayReportTaskModel = new DayReportTaskModel();
                dayReportTaskModel.setProjectId(projectId);
                dayReportTaskModel.setHours(hours1);
                dayReportTaskModel.setDesc(desc);
                taskModelList.add(dayReportTaskModel);
                hours = hours + hours1;
            }
        }

        ReqLeaveVO leaveVO = reqVO.getLeaveVO();
        DayExchangeModel dayExchangeModel = new DayExchangeModel();

        boolean holidayFlag = iHolidayService.checkHoliday(DateUtils.convertLongToDate(reqVO.getDate()));

        if (leaveVO != null) {
            Double leaveHours = leaveVO.getLeaveHours();
            if (leaveHours != null) {
                if(holidayFlag){
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_HOLIDAY_LEAVE_ERROR);
                }
                if (leaveHours > 8.0 || leaveHours <= 0.0) {
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_LEAVE_HOUR_8_ERROR);
                } else {
                    hours = hours + leaveHours;
                }
            }
            dayExchangeModel.setLeaveHour(leaveHours);
            dayExchangeModel.setDesc(leaveVO.getDesc());
        }
        if (hours > 24.0) {
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_ALL_HOUR_24_ERROR);
        }
        if (hours < 8.0) {
            if(!holidayFlag){
                throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_ALL_HOUR_8_ERROR);
            }
            if(holidayFlag){
                if(hours <= 0.0){
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_HOLIDAY_WORK_HOUR_NULL_ERROR);
                }
            }
        }

        DayReportAdvanceModel dayReportAdvanceModel = new DayReportAdvanceModel();
        dayReportAdvanceModel.setDayReportList(taskModelList);
        dayReportAdvanceModel.setLeaveModel(dayExchangeModel);
        dayReportAdvanceModel.setDate(DateUtils.convertLongToDate(reqVO.getDate()));
        dayReportAdvanceModel.setUserId(securityContextUtils.getCurrentUserId());

        iDayReportService.createDayReport(dayReportAdvanceModel);

    }

    @ApiOperation(value = "查询日报列表", notes = "查询日报列表")
    @PostMapping("/list")
    public BasePagesDomain<ResDayReportVO> searchDayReport(@RequestBody ReqSearchDayReportVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();

        //查询范围限制人员列表
        List<Integer> limitUser = searchLimitUser(currentUserRole,currentUserAreaId,currentUserId,reqVO.getProjectId(), reqVO.getUserId());
        List<Integer> limitProject = searchLimitProject(currentUserRole,currentUserAreaId,currentUserId);

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

            SystemVariableModel newDataInfo = iSystemVariableService.getNewDataInfo();
            if(ObjectUtils.isNotEmpty(newDataInfo)){
                LocalDateTime deadlineTime = LocalDateTime.of(LocalDate.parse(newDataInfo.getDeadline()), LocalTime.MIDNIGHT);
                resVO.setIsEdit(deadlineTime.isBefore(model.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
            }

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

        BasePagesDomain<ResDayReportVO> res = new BasePagesDomain<>(resModel.getCurrentPage(), resModel.getPageSize(), resModel.getTotalPage(), resModel.getTotal());
        res.setTotalList(resRecords);
        return res;
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
    private List<Integer> searchLimitUser(RoleTypeEnum currentUserRole,Integer currentUserAreaId,Integer currentUserId,Integer projectId, Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

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
            List<UserModel> allUsers = iUserService.getAllEnableUsers();
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
                            if (userId == integer) {
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


    private List<Integer> searchLimitProject(RoleTypeEnum currentUserRole,Integer currentUserAreaId,Integer currentUserId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

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

    @ApiOperation(value = "查询未写日报列表", notes = "查询未写日报列表")
    @PostMapping("/NoneDayReport")
    public BasePagesDomain<ResNoneReportVO> searchNoneDayReportList(@RequestBody ReqSearchDayReportVO reqVO) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, ParseException {

        Long startDate = reqVO.getStartDate();
        Long endDate = reqVO.getEndDate();
        if (startDate == null || endDate == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_START_END_DATE_NULL_ERROR);
        }
        Date now = new Date();

        if(endDate> now.getTime()){
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_DAY_OVER_ERROR);
        }

        int currentPage = reqVO.getCurrentPage();
        if (currentPage == 0) {
            currentPage = 1;
        }
        int pageSize = reqVO.getPageSize();
        if (pageSize == 0) {
            pageSize = 10;
        }

        List<UserModel> allUsers = iUserService.getAllUsers();
        Map<Integer,Long> userCreateMap = new HashMap<>();
        Map<Integer, String> userMap = new HashMap<>();
        Map<Integer,UserModel> userAllInfoMap = new HashMap<>();
        for (UserModel userModel : allUsers) {
            userMap.put(userModel.getId(), userModel.getNickname());
            userCreateMap.put(userModel.getId(), userModel.getCreateTime().getTime());
            userAllInfoMap.put(userModel.getId(),userModel);
        }


        RoleTypeEnum currentUserRole = null;
        Integer currentUserAreaId = null;
        Integer currentUserId = null;

        if(reqVO.getMap().isEmpty()){
            currentUserRole = securityContextUtils.getCurrentUserRole();
            currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
            currentUserId = securityContextUtils.getCurrentUserId();
        }else {
            currentUserRole = (RoleTypeEnum)reqVO.getMap().get("roleCode");
            currentUserAreaId = (Integer) reqVO.getMap().get("areaId");
            currentUserId = (Integer) reqVO.getMap().get("userId");
        }


        //查询范围限制人员列表
        List<Integer> limitUser = searchLimitUser(currentUserRole,currentUserAreaId,currentUserId,reqVO.getProjectId(), reqVO.getUserId());

        //设定人员入职离职的时间间隔生效的天数
        Map<Integer,Set<String>> userComeLeaveBetweenDaysMap = new HashMap<>();
        List<UserComeLeaveModel> userComeLeaveModels = iUserService.searchUserComeLeaveList(limitUser);
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(userComeLeaveModels)){
            for (UserComeLeaveModel userComeLeaveModel : userComeLeaveModels) {
                Integer userId = userComeLeaveModel.getUserId();
                Date comeDate = userComeLeaveModel.getComeDate();
                Date leaveDate = userComeLeaveModel.getLeaveDate();
                if(leaveDate == null){
                    leaveDate = new Date();
                }

                List<String> betweenDays = DateUtils.getBetweenDays(DateUtils.convertDateToStr(comeDate), DateUtils.convertDateToStr(leaveDate));
                Set<String> betweenSet;
                if(userComeLeaveBetweenDaysMap.containsKey(userId)){
                    betweenSet = userComeLeaveBetweenDaysMap.get(userId);
                }else{
                    betweenSet = new HashSet<>();
                }
                for (String betweenDay : betweenDays) {
                    betweenSet.add(betweenDay);
                }
                userComeLeaveBetweenDaysMap.put(userId,betweenSet);
            }

        }


        List<DayReportModel> dayReportModels = iDayReportService.searchDayReportByLimitUser(limitUser,DateUtils.convertLongToDate(startDate),DateUtils.convertLongToDate(endDate),reqVO.getUserId());

        Map<Integer, HashSet<Date>> userReportDateMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(dayReportModels)) {
            for (DayReportModel dayReportAdvanceModel : dayReportModels) {
                Integer userId = dayReportAdvanceModel.getUserId();
                Date date = dayReportAdvanceModel.getDate();
                HashSet<Date> dates;
                if (userReportDateMap.containsKey(userId)) {
                    dates = userReportDateMap.get(userId);
                } else {
                    dates = new HashSet<>();
                }
                dates.add(date);
                userReportDateMap.put(userId, dates);
            }
        }
        String startDateStr = DateUtils.convertDateToStr(DateUtils.convertLongToDate(startDate));
        String endDateStr = DateUtils.convertDateToStr(DateUtils.convertLongToDate(endDate));

        List<String> betweenDays = DateUtils.getBetweenDays(startDateStr, endDateStr);

        Set<String> holidaySet = new HashSet<>();
        List<HolidayModel> holidayModels = iHolidayService.searchHolidayByDate(startDateStr, endDateStr);
        for (HolidayModel holidayModel : holidayModels) {
            String dateStr = holidayModel.getDateStr();
            holidaySet.add(dateStr);
        }

        List<String> workDays = new ArrayList<>();
        for (String betweenDay : betweenDays) {
            if (!holidaySet.contains(betweenDay)) {
                workDays.add(betweenDay);
            }
        }

        Map<Date, Map<Integer, String>> dateUserMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(limitUser)) {
            for (Integer user : limitUser) {
                UserModel userModel = userAllInfoMap.get(user);
                String areaName = userModel.getAreaName();
                if(AreaEnum.公司管理大区.getCode().equals(areaName)){
                    continue;
                }
                if (userReportDateMap.containsKey(user)) {
                    HashSet<Date> dates = userReportDateMap.get(user);
                    for (String workDay : workDays) {
                        Date date = DateUtils.convertStrToDate(workDay);
                        if (!dates.contains(date)) {
                            if(userCreateMap.get(user) < (date.getTime()+57600000)) {
                                Set<String> betweenDaySet = userComeLeaveBetweenDaysMap.get(user);
                                if(betweenDaySet.contains(workDay)) {
                                    Map<Integer, String> resNoneReportUserVOS;
                                    if (dateUserMap.containsKey(date)) {
                                        resNoneReportUserVOS = dateUserMap.get(date);
                                    } else {
                                        resNoneReportUserVOS = new HashMap<>();
                                    }
                                    resNoneReportUserVOS.put(user, userMap.get(user));
                                    dateUserMap.put(date, resNoneReportUserVOS);
                                }
                            }
                        }
                    }
                } else {
                    for (String workDay : workDays) {
                        Date date = DateUtils.convertStrToDate(workDay);
                        if(userCreateMap.get(user) < (date.getTime()+57600000)) {
                            Set<String> betweenDaySet = userComeLeaveBetweenDaysMap.get(user);
                            if(betweenDaySet.contains(workDay)) {
                                Map<Integer, String> resNoneReportUserVOS;
                                if (dateUserMap.containsKey(date)) {
                                    resNoneReportUserVOS = dateUserMap.get(date);
                                } else {
                                    resNoneReportUserVOS = new HashMap<>();
                                }
                                resNoneReportUserVOS.put(user, userMap.get(user));
                                dateUserMap.put(date, resNoneReportUserVOS);
                            }
                        }
                    }
                }
            }
        }
        List<ResNoneReportVO> resList = new ArrayList<>();

        for (Map.Entry<Date, Map<Integer, String>> dateListEntry : dateUserMap.entrySet()) {
            ResNoneReportVO vo = new ResNoneReportVO();
            vo.setDate(DateUtils.convertDateToLong(dateListEntry.getKey()));
            List<ResNoneReportUserVO> colList = dateListEntry.getValue().entrySet().stream().map(mp -> {
                ResNoneReportUserVO v = new ResNoneReportUserVO();
                v.setUserId(mp.getKey());
                v.setNickname(mp.getValue());
                return v;
            }).collect(Collectors.toList());
            vo.setUserList(colList);
            resList.add(vo);
        }


        //按照日期排序
        List<ResNoneReportVO> orderList = resList.stream().sorted(Comparator.comparing(ResNoneReportVO::getDate)).collect(Collectors.toList());
        //降序
        Collections.reverse(orderList);

        List<ResNoneReportVO> collect = orderList.stream().skip((currentPage - 1)*pageSize).limit(pageSize).collect(Collectors.toList());

        double total = new Double(orderList.size());
        double size = new Double(pageSize);
        double currPage = new Double(currentPage);

        double totalPage = Math.ceil(total / size);
        if (currPage > totalPage) {
            currPage = totalPage;
        }


        BasePagesDomain<ResNoneReportVO> resNoneReportVOBasePagesDomain = new BasePagesDomain<ResNoneReportVO>();
        resNoneReportVOBasePagesDomain.setTotal(new Double(total).intValue());
        resNoneReportVOBasePagesDomain.setPageSize(pageSize);
        resNoneReportVOBasePagesDomain.setCurrentPage(new Double(currPage).intValue());
        resNoneReportVOBasePagesDomain.setTotalPage(new Double(totalPage).intValue());
        resNoneReportVOBasePagesDomain.setTotalList(collect);

        return resNoneReportVOBasePagesDomain;
    }
    @ApiOperation(value = "查询日报详情", notes = "查询日报详情")
    @GetMapping("/detail/{id}")
    public ResDayReportVO fetchDetail(@PathVariable("id") Integer id) throws Exception {
        DayReportAdvanceModel dayReportAdvanceModel = iDayReportService.fetchDayReportDetail(id);
        if (dayReportAdvanceModel == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_NOT_EXIST_ERROR);
        }


        List<DayReportTaskModel> dayReportList = dayReportAdvanceModel.getDayReportList();

        List<ReqDayReportVO> reqDayReportVOList = new ArrayList<>();
        for (DayReportTaskModel dayReportTaskModel : dayReportList) {
            ReqDayReportVO reqDayReportVO = new ReqDayReportVO();
            reqDayReportVO.setProjectId(dayReportTaskModel.getProjectId());
            reqDayReportVO.setHours(dayReportTaskModel.getHours());
            reqDayReportVO.setDesc(dayReportTaskModel.getDesc());
            reqDayReportVOList.add(reqDayReportVO);
        }


        DayExchangeModel leaveModel = dayReportAdvanceModel.getLeaveModel();

        ReqLeaveVO reqLeaveVO = new ReqLeaveVO();
        if (leaveModel != null) {
            reqLeaveVO.setDesc(leaveModel.getDesc());
            reqLeaveVO.setLeaveHours(leaveModel.getLeaveHour());
        }

        ResDayReportVO resDayReportVO = new ResDayReportVO();
        resDayReportVO.setDayReportList(reqDayReportVOList);
        resDayReportVO.setLeaveVO(reqLeaveVO);
        resDayReportVO.setDate(DateUtils.convertDateToLong(dayReportAdvanceModel.getDate()));
        resDayReportVO.setUserId(dayReportAdvanceModel.getUserId());
        resDayReportVO.setNickname(dayReportAdvanceModel.getNickname());
        resDayReportVO.setId(dayReportAdvanceModel.getId());
        return resDayReportVO;
    }

    @ApiOperation(value = "编辑日报", notes = "查询日报详情")
    @PutMapping("/update")
    public void update(@RequestBody ReqUpdateDayReportVO reqVO) throws Exception {

        if (reqVO.getId() == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_ID_NULL_ERROR);
        }

        if (reqVO.getDate() == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_DATE_NULL_ERROR);
        }

        SystemVariableModel newDataInfo = iSystemVariableService.getNewDataInfo();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(reqVO.getDate()), ZoneId.systemDefault());
        if(ObjectUtils.isNotEmpty(newDataInfo) &&  LocalDateTime.of(LocalDate.parse(newDataInfo.getDeadline()), LocalTime.MIDNIGHT).isAfter(localDateTime)){
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_NOT_MODIFIED);
        }

        ReqSearchDayReportVO vo = new ReqSearchDayReportVO();
        //默认时间2021-09-01
        vo.setStartDate(1630425600000L);
        Calendar    rightNow    =    Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        String today = year+"-"+month+"-"+day;
        Date date = DateUtils.convertStrToDate(today);

        if(reqVO.getDate() >= date.getTime()) {
            vo.setEndDate(date.getTime() - 1);
            vo.setUserId(securityContextUtils.getCurrentUserId());
            vo.setCurrentPage(1);
            vo.setPageSize(9999999);
            BasePagesDomain<ResNoneReportVO> resNoneReportVOBasePagesDomain = searchNoneDayReportList(vo);
            if (resNoneReportVOBasePagesDomain.getTotal() > 0) {
                throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_OLD_NULL_ERROR);
            }
        }


        if (reqVO.getDate() > new Date().getTime()) {
            ReqLeaveVO leaveVO = reqVO.getLeaveVO();
            if (leaveVO == null || leaveVO.getLeaveHours() == null || leaveVO.getLeaveHours() != 8.0) {
                throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_FUTURE_ERROR);
            }
            List<ReqDayReportVO> dayReportList = reqVO.getDayReportList();
            if (!CollectionUtils.isEmpty(dayReportList)) {
                for (ReqDayReportVO reqDayReportVO : dayReportList) {
                    if (reqDayReportVO != null) {
                        Integer projectId = reqDayReportVO.getProjectId();
                        Double hours1 = reqDayReportVO.getHours();
                        if (projectId != null || hours1 != null) {
                            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_FUTURE_ERROR);
                        }
                    }
                }
            }
        }


        List<Integer> projectIdsByName = iProjectService.getProjectIdsByName(ProjectUtils.PROJECT_OTHER_ACTION);
        Set<Integer> otherProjSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(projectIdsByName)){
            for (Integer integer : projectIdsByName) {
                otherProjSet.add(integer);
            }

        }

        List<ProjectModel> forceProjectList = iProjectService.searchAllForceDescProject();
        Set<Integer> forceProjectSet = new HashSet<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(forceProjectList)){
            for (ProjectModel projectModel : forceProjectList) {
                forceProjectSet.add(projectModel.getId());
            }
        }


        Double hours = 0.0;

        Set<Integer> projectCheckSet = new HashSet<>();
        List<DayReportTaskModel> taskModelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(reqVO.getDayReportList())) {
            for (ReqDayReportVO dayReportTaskVO : reqVO.getDayReportList()) {

                Integer projectId = dayReportTaskVO.getProjectId();
                Double hours1 = dayReportTaskVO.getHours();

                if(projectId == null && hours1 == null){
                    continue;
                }
                if(projectId==null){
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_PROJECT_ID_NULL_ERROR);
                }
                if(hours1==null){
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_PROJECT_TIME_NULL_ERROR);
                }


                if (projectCheckSet.contains(projectId)) {
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_PROJECT_REPEAT_ERROR);
                } else {
                    projectCheckSet.add(projectId);
                }

                String desc = dayReportTaskVO.getDesc();
//                if(otherProjSet.contains(projectId)){
//                    if(StringUtils.isEmpty(desc)){
//                        throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_OTHER_NULL_ERROR);
//                    }
//                }

                if(forceProjectSet.contains(projectId)){
                    if(StringUtils.isEmpty(desc)){
                        ProjectAdvanceModel projectAdvanceModel = iProjectService.fetchProject(projectId);
                        throw new DynamicServiceException("【"+projectAdvanceModel.getName()+"】项目已被负责人设置为强制描述，请填写描述内容");
                    }
                }

                DayReportTaskModel dayReportTaskModel = new DayReportTaskModel();
                dayReportTaskModel.setProjectId(dayReportTaskVO.getProjectId());
                dayReportTaskModel.setHours(dayReportTaskVO.getHours());
                dayReportTaskModel.setDesc(dayReportTaskVO.getDesc());
                taskModelList.add(dayReportTaskModel);
                if(hours1 == null){
                    hours1 = 0.0;
                }
                hours = hours + hours1;
            }
        }

        ReqLeaveVO leaveVO = reqVO.getLeaveVO();
        DayExchangeModel dayExchangeModel = new DayExchangeModel();
        boolean holidayFlag = iHolidayService.checkHoliday(DateUtils.convertLongToDate(reqVO.getDate()));

        if (leaveVO != null) {
            Double leaveHours = leaveVO.getLeaveHours();
            if (leaveHours != null) {
                if(holidayFlag){
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_HOLIDAY_LEAVE_ERROR);
                }
                if (leaveHours > 8.0 || leaveHours <= 0.0) {
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_LEAVE_HOUR_8_ERROR);
                } else {
                    hours = hours + leaveHours;
                }
            }
            dayExchangeModel.setLeaveHour(leaveHours);
            dayExchangeModel.setDesc(leaveVO.getDesc());
        }
        if (hours > 24.0) {
            throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_ALL_HOUR_24_ERROR);
        }
        if (hours < 8.0) {
            if(!holidayFlag) {
                throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_ALL_HOUR_8_ERROR);
            }
            if(holidayFlag){
                if(hours <= 0.0){
                    throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_HOLIDAY_WORK_HOUR_NULL_ERROR);
                }
            }
        }

        DayReportAdvanceModel dayReportAdvanceModel = new DayReportAdvanceModel();
        dayReportAdvanceModel.setDayReportList(taskModelList);
        dayReportAdvanceModel.setLeaveModel(dayExchangeModel);
        dayReportAdvanceModel.setDate(DateUtils.convertLongToDate(reqVO.getDate()));
        dayReportAdvanceModel.setUserId(securityContextUtils.getCurrentUserId());
        dayReportAdvanceModel.setId(reqVO.getId());

        iDayReportService.updateDayReport(dayReportAdvanceModel);

    }

    @ApiOperation(value = "删除日报", notes = "删除日报")
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") Integer id) throws Exception {
        DayReportAdvanceModel dayReportAdvanceModel = iDayReportService.fetchDayReportDetail(id);
        Integer userId = dayReportAdvanceModel.getUserId();
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new BaseAuthException(AuthErrorCodeEnum.DAY_REPORT_NOT_MYSELF_ERROR);
        }
        iDayReportService.deleteDayReport(id);
    }

    @ApiOperation(value = "查看日报存稿", notes = "查看日报存稿")
    @GetMapping("/viewDraft")
    public ResDayReportVO viewDraft() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        UserModel currentUserModel = iUserService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        ResDayReportVO resDayReportVO = new ResDayReportVO();
        resDayReportVO.setUserId(currentUserModel.getId());
        resDayReportVO.setAreaName(currentUserModel.getAreaName());
        resDayReportVO.setNickname(currentUserModel.getNickname());
        //日报字表
        List<ReqDayReportVO> reqDayReportVOS = new ArrayList<>();
        //假期表
        ReqLeaveVO reqLeaveVO = new ReqLeaveVO();

        //查主表数据
        DraftDayReportModel draftDayReportModel = iDayReportService.queryDraftDayReport(currentUserModel.getId());
        if(draftDayReportModel == null){
            resDayReportVO.setDate(null);
            resDayReportVO.setDayReportList(reqDayReportVOS);
            resDayReportVO.setLeaveVO(reqLeaveVO);
            return resDayReportVO;
        }
        resDayReportVO.setDate(DateUtils.convertDateToLong(draftDayReportModel.getDate()));
        //日报子表
        List<DraftDayReportTaskModel> draftDayReportTaskModels = iDayReportService.queryDraftDayReportTask(currentUserModel.getId());
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(draftDayReportTaskModels)){
            for(DraftDayReportTaskModel model:draftDayReportTaskModels){
                ReqDayReportVO reqDayReportVO = new ReqDayReportVO();
                reqDayReportVO.setProjectId(model.getProjectId());
                reqDayReportVO.setHours(model.getHours());
                reqDayReportVO.setDesc(model.getDesc());
                reqDayReportVOS.add(reqDayReportVO);
            }
        }

        //假期表
        DraftDayExchangeModel draftDayExchangeModel = iDayReportService.queryDraftDayExchange(currentUserModel.getId());
        if(draftDayExchangeModel != null){
            reqLeaveVO.setLeaveHours(draftDayExchangeModel.getLeaveHour());
            reqLeaveVO.setDesc(draftDayExchangeModel.getDesc() == ""?null:draftDayExchangeModel.getDesc());
        }

        resDayReportVO.setDayReportList(reqDayReportVOS);
        resDayReportVO.setLeaveVO(reqLeaveVO);
        return resDayReportVO;
    }

    @ApiOperation(value = "日报存稿", notes = "日报存稿")
    @PostMapping("/saveDraft")
    @Transactional
    public ReqCreateDayReportVO saveDraft(@RequestBody ReqCreateDayReportVO reqVO) throws Exception {
        UserModel currentUserModel = iUserService.queryUserByUserName(securityContextUtils.getCurrentUsername());

        //日报主表
        DraftDayReportModel draftDayReportModel = new DraftDayReportModel();
        draftDayReportModel.setUserId(currentUserModel.getId());
        draftDayReportModel.setDate(DateUtils.convertLongToDate(reqVO.getDate()));

        //日报子表
        List<DraftDayReportTaskModel> draftDayReportTaskModels = null;
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(reqVO.getDayReportList())){
            Integer temp = 0;
            for (int i = 0; i < reqVO.getDayReportList().size(); i++) {
                temp = reqVO.getDayReportList().get(i).getProjectId();
                if(temp == null){
                    continue;
                }
                for (int j = i + 1; j < reqVO.getDayReportList().size(); j++) {
                    if(temp.equals(reqVO.getDayReportList().get(j).getProjectId())){
                        throw new BaseServiceException(ServiceErrorCodeEnum.DAY_REPORT_PROJECT_REPEAT_ERROR);
                    }
                }
            }
            draftDayReportTaskModels = new ArrayList<>();
            for (ReqDayReportVO reqDayReportVO: reqVO.getDayReportList()){
                DraftDayReportTaskModel dayReportTaskModel = new DraftDayReportTaskModel();
                dayReportTaskModel.setUserId(currentUserModel.getId());
                dayReportTaskModel.setProjectId(reqDayReportVO.getProjectId());
                dayReportTaskModel.setHours(reqDayReportVO.getHours());
                dayReportTaskModel.setDesc(reqDayReportVO.getDesc() != null ?reqDayReportVO.getDesc(): StringUtils.EMPTY);
                draftDayReportTaskModels.add(dayReportTaskModel);
            }
        }

        //请假
        DraftDayExchangeModel draftDayExchangeModel = null;
        if(reqVO.getLeaveVO() != null){
            draftDayExchangeModel = new DraftDayExchangeModel();
            draftDayExchangeModel.setUserId(currentUserModel.getId());
            draftDayExchangeModel.setLeaveHour(reqVO.getLeaveVO().getLeaveHours());
            //draftDayExchangeModel.setDesc(reqVO.getLeaveVO().getDesc() != null ? reqVO.getLeaveVO().getDesc() : StringUtils.EMPTY);
            draftDayExchangeModel.setDesc(reqVO.getLeaveVO().getDesc());
        }
        iDayReportService.delDraft(currentUserModel.getId());
        iDayReportService.saveDraft(draftDayReportModel,draftDayReportTaskModels,draftDayExchangeModel);
        return reqVO;
    }
}
