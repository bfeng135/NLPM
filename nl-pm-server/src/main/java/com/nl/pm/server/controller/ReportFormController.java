package com.nl.pm.server.controller;

import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IDayReportRegistry;
import com.nl.pm.server.registry.IProjectRegistry;
import com.nl.pm.server.registry.IUserRegistry;
import com.nl.pm.server.registry.entity.ProjectEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.ResEveryUserTotalTimeEntityParam;
import com.nl.pm.server.registry.param.ResEveryWorkTotalTimeEntityParam;
import com.nl.pm.server.registry.param.ResReportFormEntityParam;
import com.nl.pm.server.service.IDayReportService;
import com.nl.pm.server.service.IProjectService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.ProjectAdvanceModel;
import com.nl.pm.server.service.model.UserModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(value = "报表管理",tags = {"报表管理"})
@RequestMapping("/reportForm")
public class ReportFormController {
    private static final Logger log = LoggerFactory.getLogger(ReportFormController.class);

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

    @ApiOperation(value = "测试暂不使用---根据项目查询报表",notes = "测试暂不使用---根据项目查询报表")
    @PostMapping("/getReportFormByProjectTest")
    public ResReportFromProjectVO getReportFormByProjectVOTest(@RequestBody ReqReportFormByProjectVOTest vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
        || RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
        || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
        || RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            vo.setAreaId(currentUserModel.getAreaId());
        }

//        List<UserModel> list = userService.queryUserByProjectIdAndAreaId(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())),
//                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getProjectId(), vo.getAreaId());

        //根据区域和项目、时间，查询所有日报
        List<Date> list = dayReportService.queryReportByProjectIdAndAreaId(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())),
                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getProjectId(), vo.getAreaId());

        //竖行时间，List<Map<String,Double>>>每天每人工作时间
        //List<Map<String, List<Map<UserEntity,Double>>>> mapList = new ArrayList<>();
        List<Map<String, List<UserEntity>>> mapList = new ArrayList<>();

        //每个人的工作总时长
        List<UserEntity> userWorkTotalTime = new ArrayList<>();
        if(list.size() > 0){
            for(Date date : list){
                //List<Map<UserEntity, Double>> maps = dayReportService.queryReportTimeByTimeStr(DateUtils.convertDateToStr(date), vo.getProjectId(), vo.getAreaId());
                List<UserEntity> userEntityList = dayReportService.queryReportTimeByTimeStr(DateUtils.convertDateToStr(date), vo.getProjectId(), vo.getAreaId());

                if(userEntityList.size() > 0){
                    Map<String, List<UserEntity>> map = new HashMap<>();
                    map.put(DateUtils.convertDateToStr(date),userEntityList);
                    mapList.add(map);
                }
            }
            if(mapList.size() > 0){
                //遍历天数
                for(int i = 0; i< mapList.size(); i++){
                    Map<String, List<UserEntity>> map = mapList.get(i);
                    //每天所有的人
                    for (Map.Entry<String, List<UserEntity>> stringListEntry : map.entrySet()) {
                        //每天所有的人
                        //List<UserEntity> value = stringListEntry.getValue();
                        if(userWorkTotalTime.size() == 0){
                            for(UserEntity userEntity : stringListEntry.getValue()){
                                userEntity.setTotalTime(userEntity.getEveryDayTime());
                                userWorkTotalTime.add(userEntity);
                            }
                            //userWorkTotalTime = stringListEntry.getValue().stream().map(userEntity -> userEntity).collect(Collectors.toList());
                        }else {
                            //遍历每天的用户
                            for (int j = 0; j < stringListEntry.getValue().size(); j++) {
                                Boolean flg = false;
                                if(!flg){
                                    //遍历数组中的用户
                                    for(int j2 =0;j2 < userWorkTotalTime.size(); j2++){
                                        if(userWorkTotalTime.get(j2).getUsername().equals(stringListEntry.getValue().get(j).getUsername())){
                                            userWorkTotalTime.get(j2).setTotalTime(userWorkTotalTime.get(j2).getTotalTime() + stringListEntry.getValue().get(j).getEveryDayTime());
                                            flg = true;
                                            break;
                                        }
                                    }
                                }
                                if(flg){
                                    continue;
                                }
                                //userWorkTotalTime没有数据则添加
                                UserEntity userEntity = stringListEntry.getValue().get(j);
                                userEntity.setTotalTime(userEntity.getEveryDayTime());
                                userWorkTotalTime.add(userEntity);
                            }
                        }
                    }
                }
            }
        }
        //项目名字
        ProjectAdvanceModel projectAdvanceModel = projectService.fetchProject(vo.getProjectId());
        String projectName = projectAdvanceModel.getName();

        //项目总时长
        Double sumWorkTime = projectService.sumWorkTotal(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime())),
                DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())), vo.getProjectId());

        //项目总人天
        Double sumWorkDay = null;
        if(sumWorkTime != null){
            sumWorkDay = sumWorkTime / 8;

        }

        //数据写入Excel
        ResReportFromProjectVO resReportFromProjectVO = new ResReportFromProjectVO();
        resReportFromProjectVO.setProjectName(projectName);
        resReportFromProjectVO.setSumWorkTime(sumWorkTime);
        resReportFromProjectVO.setSumWorkDay(sumWorkDay);
        resReportFromProjectVO.setUserWorkTotalTime(toResShowReportFormByProjectVO(userWorkTotalTime));
        resReportFromProjectVO.setMapList(toMapResShowReportFormByProjectVO(mapList));

        return resReportFromProjectVO;
    }

    @ApiOperation(value = "测试暂不使用---复杂查询报表",notes = "测试暂不使用---复杂查询报表")
    @PostMapping("/getComplexReportFormTest")
    public List<ResReportFormComplexVO> getComplexReportFormTest(@RequestBody ReqComplexReportFormVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.EMPLOYEE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.FINANCE == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.HR == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
        }
        if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            vo.setAreaId(null);
            Integer[] areaId = new Integer[]{currentUserModel.getAreaId()};
            vo.setAreaId(areaId);
        }

        List<String> projectNameList = projectRegistry.queryProjectAndDistinct(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());

        List<ResReportFormEntityParam> params = projectRegistry.queryEveryUserTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());

        List<ResReportFormComplexVO> list = new ArrayList<>();
        if(projectNameList.size() > 0){
            for (String projectName:projectNameList){
                ResReportFormComplexVO vo2 = new ResReportFormComplexVO();
                vo2.setProjectName(projectName);
                List<ResReportFormComplexUserVO> list2 = new ArrayList<>();
                if(params.size() > 0){
                    for(ResReportFormEntityParam param:params){
                        ResReportFormComplexUserVO vo3 = new ResReportFormComplexUserVO();
                        vo3.setNickname(param.getNickname());
                        if(projectName.equals(param.getProjectName())){
                            vo3.setTotalTime(param.getTotalTime());
                        }else {
                            vo3.setTotalTime(new Integer(0).doubleValue());
                        }
                        list2.add(vo3);
                    }
                }
                vo2.setResReportFormComplexUserVO(list2);
                list.add(vo2);
            }
        }

        return list;


//        //查询所有项目
//        List<ProjectEntity> projectEntityList = projectRegistry.queryProjectByIdOrAreaId(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());
//        //查询所有的人
//        List<UserEntity> userEntityList = userRegistry.queryAllUserByProjectIdOrAreaId(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
//                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectId());
//
//        List<UserEntity> resultMap = new ArrayList<>();
//
//        if(projectEntityList.size() > 0){
//            if(userEntityList.size() > 0){
//                //遍历横坐标每一个项目
//                for(ProjectEntity projectEntity: projectEntityList){
//                    //遍历纵坐标每一个人
//                    for(UserEntity userEntity:userEntityList){
//                        //查询这个人在这个项目，这个时间段写的日报集合
//
//                        //遍历日报，汇总
//
//                    }
//                }
//            }
//        }

    }

    @ApiOperation(value = "单报表详细查询",notes = "单报表详细查询表")
    @PostMapping("/getReportFormByProject")
    public List<ReqReportFromByUserVO> getReportFormByProjectVO(@RequestBody ReqReportFormByProjectVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
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
        List<ReqReportFromByUserVO> list = new ArrayList<>();

        if(StringUtils.isEmpty(vo.getCurrentProjectName())){
            return list;
        }
        if(vo.getProjectName() != null ){
            if(vo.getProjectName().length > 0)
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_PROJECT_ERROR);
        }
        String[] currentProjectName = new String[]{vo.getCurrentProjectName()};
        vo.setProjectName(currentProjectName);

//        if(vo.getProjectName().length == 0){
//            return list;
//        }

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

        //查询每天工作时长
        List<ResEveryWorkTotalTimeEntityParam> listEveryDateWorkTotalTime = dayReportRegistry.queryEveryDateWorkTotalTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectName()[0]);


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
                                if(userEntity.getId().equals(userEntity2.getId())){
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

        if(list.size() == 0){
            return list;
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

    @ApiOperation(value = "通过项目单报表详细查询",notes = "通过项目单报表详细查询表")
    @PostMapping("/getReportFormByProjectByProject")
    public BasePagesDomain<ReqReportFromByProjectVO> getReportFormByProjectVOByProject(@RequestBody ReqReportFormByProjectVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {

        BasePagesDomain<ReqReportFromByProjectVO> domain = new BasePagesDomain<>();
        List<ReqReportFromByProjectVO> projectVO = new ArrayList<>();

        List<ReqReportFromByUserVO> list = new ArrayList<>();

        if(vo.getStartTime() == null || vo.getEndTime() == null){
            return domain;
        }

        if(StringUtils.isEmpty(vo.getCurrentProjectName())){
            return domain;
        }
        if(vo.getProjectName() != null ){
            if(vo.getProjectName().length > 0)
                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_PROJECT_ERROR);
        }
        String[] currentProjectName = new String[]{vo.getCurrentProjectName()};
        vo.setProjectName(currentProjectName);

//        if(vo.getProjectName().length == 0){
//            return list;
//        }

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

        //查询每天工作时长
        List<ResEveryWorkTotalTimeEntityParam> listEveryDateWorkTotalTime = dayReportRegistry.queryEveryDateWorkTotalTime(DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getStartTime()))
                ,DateUtils.convertDateToStr(DateUtils.convertLongToDate(vo.getEndTime())),vo.getAreaId(),vo.getProjectName()[0]);


        if(userEntities.size() > 0){
            //遍历人
            for (UserEntity userEntity:userEntities){
                ReqReportFromByUserVO vo2 = new ReqReportFromByUserVO();
                vo2.setId(userEntity.getId());
                vo2.setNickname(userEntity.getNickname());
                vo2.setAreaId(userEntity.getAreaId());
                vo2.setAreaName(userEntity.getAreaName());
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
                                        vo3.setProjectDesc(userEntity2.getProjectDesc());
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

//        if(list.size() == 0){
//            return domain;
//        }

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

        //每页的数据大小
        int pageSize = vo.getPageSize();
        //当前页
        int currentPage = vo.getCurrentPage();
        //总条数
        int total = list.size();
        //分成多少页
        int totalPage = (total + pageSize - 1)/pageSize;

        //int size = list.size();
        ReqReportFromByProjectVO report = new ReqReportFromByProjectVO();

        Double totalTime = userRegistry.queryTotalTime(null, vo.getProjectName()[0]);
        report.setTotalTime(totalTime);

        for (int i = 0; i < total; i++) {
//            list.get(i).setResReportFormByTimeVOS(list.get(i).getResReportFormByTimeVOS().stream()
//                    .skip((currentPage - 1)*pageSize).limit(pageSize).collect(Collectors.toList()));
            report.setCurrentTotalTime(report.getCurrentTotalTime() + list.get(i).getUserTotalTime());
        }

        if(0.0 == report.getCurrentTotalTime()){
            report.setPercent(0.0);
        }else {
            double v = report.getCurrentTotalTime() / report.getTotalTime();
            BigDecimal bg = new BigDecimal(v * 100);
            report.setPercent( bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }

        report.setReqReportFromByUserVOS(list.stream().skip((currentPage - 1)*pageSize).limit(pageSize).collect(Collectors.toList()));
        projectVO.add(report);

        domain = new BasePagesDomain(currentPage,pageSize,totalPage,total);
        domain.setTotalList(projectVO);
        return domain;

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






    @ApiOperation(value = "复杂查询报表",notes = "复杂查询报表")
    @PostMapping("/getComplexReportForm")
    public List<ResReportFormComplexVO> getComplexReportForm(@RequestBody ReqComplexReportFormVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException, ParseException {
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
        if(list.size() == 0){
            return list;
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



        return list;
    }



    private List<ResShowReportFormByProjectVO> toResShowReportFormByProjectVO(List<UserEntity> userWorkTotalTime){
        List<ResShowReportFormByProjectVO> voList = new ArrayList<>();
        if(userWorkTotalTime.size() > 0){
            for(UserEntity userEntity:userWorkTotalTime){
                ResShowReportFormByProjectVO vo = new ResShowReportFormByProjectVO();
                vo.setNickname(userEntity.getNickname());
                vo.setEveryDayTime(userEntity.getEveryDayTime());
                vo.setTotalTime(userEntity.getTotalTime());
                voList.add(vo);
            }
        }
        return voList;
    }

    private List<Map<String, List<ResShowReportFormByProjectVO>>> toMapResShowReportFormByProjectVO(List<Map<String, List<UserEntity>>> mapList){
        List<Map<String, List<ResShowReportFormByProjectVO>>> list = new ArrayList<>();
        if(mapList.size() > 0){
            for (Map<String, List<UserEntity>> map : mapList){
                for (Map.Entry<String, List<UserEntity>> stringListEntry : map.entrySet()) {
                    Map<String, List<ResShowReportFormByProjectVO>> map1 = new HashMap<>();
                    List<ResShowReportFormByProjectVO> list1 = new ArrayList<>();
                    for (UserEntity userEntity:stringListEntry.getValue()){
                        ResShowReportFormByProjectVO vo = new ResShowReportFormByProjectVO();
                        vo.setNickname(userEntity.getNickname());
                        vo.setEveryDayTime(userEntity.getEveryDayTime());
                        vo.setTotalTime(userEntity.getTotalTime());
                        list1.add(vo);
                    }

                    map1.put(stringListEntry.getKey(),list1.stream().map(user -> user).collect(Collectors.toList()));
                    list.add(map1);
                }
            }
        }
        return list;
    }
}
