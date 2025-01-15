package com.nl.pm.server.service.impl;

import com.google.gson.Gson;
import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.cache.CacheEmailUtil;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.DayReportController;
import com.nl.pm.server.controller.vo.*;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.DynamicServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.*;
import com.nl.pm.server.registry.entity.HolidayEntity;
import com.nl.pm.server.registry.entity.SystemEmailEntity;
import com.nl.pm.server.registry.entity.SystemInfoEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.service.*;
import com.nl.pm.server.service.model.SystemJobModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.CrmProjectSyncParam;
import com.nl.pm.server.utils.MailUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class SystemJobImpl implements ISystemJob {

    @Value("${system.domainName}")
    public String domainName;

    @Value("${system.crmProjectSyncUrl}")
    public String crmProjectSyncUrl;

    @Value("${system.syncProjectManagerEmail}")
    public String syncProjectManagerEmail;

    @Value("${system.syncProjectSystemSendEmail}")
    public String syncProjectSystemSendEmail;

    @Value("${system.syncProjectSystemSendEmailHost}")
    public String syncProjectSystemSendEmailHost;

    @Value("${system.syncProjectSystemSendEmailPassword}")
    public String syncProjectSystemSendEmailPassword;

    @Value("${system.syncProjectManagerName}")
    public String syncProjectManagerName;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ISystemJobService systemJobService;
    @Autowired
    private ISystemRegistry systemRegistry;
    @Autowired
    private IUserRegistry userRegistry;
    @Autowired
    private MailUtil mailUtil;
    @Autowired
    private IHolidayRegistry holidayRegistry;
    @Autowired
    private IRoleRegistry roleRegistry;
    @Autowired
    private ISystemEmailRegistry systemEmailRegistry;
    @Autowired
    private IProjectService iProjectService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IDayReportService iDayReportService;
    @Autowired
    private IHolidayService iHolidayService;
    @Autowired
    private ISystemProjectService iSystemProjectService;
    @Autowired
    private DayReportController dayReportController;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private volatile SystemEmailEntity systemEmailEntity;

    //需要对它进行初始化
    private static ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    //存储定时任务编号及任务
    private static Map<Integer, ScheduledFuture<?>> map = new HashMap<>();

    //使用静态代码块来初始化ThreadPoolTaskScheduler
    static {
        threadPoolTaskScheduler.initialize();
    }

    @Override
    public void systemJob(Integer jobId) throws Exception {
        SystemJobModel model = systemJobService.querySystemJobById(jobId);
        if(model == null){
            throw  new BaseServiceException(ServiceErrorCodeEnum.ENTITY_ERROR);
        }
        if(model.getEnable()){
            stopJob(model);
            return;
        }
        startJob(model);
    }

    @Override
    public List<SystemJobModel> queryAllSystemJob() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        return  systemJobService.getSystemJob();
    }

    @Override
    public void initSendEmail(SystemJobModel model) throws Exception {
        startJob(model);
    }

    //开启定时任务的方法
    private void startJob(SystemJobModel model) throws Exception {
        //修改数据库任务开启状态
        model.setEnable(true);
        systemJobService.editSystemJob(model);

        //通过使用它的方法来对任务进行开启和关闭，使用Cron表达式，表示时间间隔并重复执行
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(new MyRunnable(model), new CronTrigger(model.getCronExpression()));
        map.put(model.getId(), future);
    }

    private void stopJob(SystemJobModel model) throws Exception {
        //修改数据库任务开启状态
        model.setEnable(false);
        systemJobService.editSystemJob(model);
        ScheduledFuture<?> future = null;
        future = map.get(model.getId());
        if(future!=null){
            future.cancel(true);
        }
    }

    //定时任务线程
    private class MyRunnable implements Runnable {
        SystemJobModel model;

        public MyRunnable(SystemJobModel model) {
            this.model = model;
        }

        @Override
        public void run() {
            if("DAY_EMAIL".equals(model.getType())){
                try {
                    sendEmailToAllUser();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                return;
            }
            if ("WEEK_EMAIL".equals(model.getType())) {
                try {
                    sendEmailToAreaAndGroup();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException | ParseException | BaseServiceException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return;
            }
            if("CRM_PROJECT_SYNC_EMAIL".equals(model.getType())){
                try {
                    syncSystemProjectFromCrmAndSendEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

        }
    }

    @Override
    public void sendEmailToAllUser() throws MessagingException, GeneralSecurityException {

        systemEmailRegistry.initSystemEmail();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        //查看今天的前一天是不是请假
        HolidayEntity holidayEntity = holidayRegistry.queryHolidayByDate(format.format(c.getTime()));
        if(holidayEntity != null){
            return;
        }
        SystemInfoEntity systemInfoEntity = systemRegistry.querySystemInfo();
        if(systemInfoEntity == null){
            return;
        }
        //c.setTime(new Date());

        List<UserEntity> notWriteDaily = userRegistry.queryAllUserNotWriteDaily(format.format(c.getTime()));

        List<UserEntity> currentNotWriteDaily = new ArrayList<>();
        if (notWriteDaily.size() > 0){
            for(UserEntity entity :notWriteDaily){
                if(entity.getEmailNotice() && entity.getStatus()){
                    currentNotWriteDaily.add(entity);
                }
            }
        }

        if(currentNotWriteDaily.size() == 0){
            return;
        }

        int length = currentNotWriteDaily.size();
        int groupSize = 10;
        // 计算可以分成多少组
        int num = ( length + groupSize - 1 )/groupSize ;
        List<List<UserEntity>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i+1) * groupSize < length ? ( i+1 ) * groupSize : length ;
            newList.add(currentNotWriteDaily.subList(fromIndex,toIndex)) ;
        }
        for (int i = 0; i < newList.size(); i++) {
            SystemEmailEntity entity = systemEmailRegistry.queryEmailIsSend();
            if(entity == null){
                return;
            }


            List<UserEntity> toCurrentNotWriteDaily = new ArrayList<>();
            for (int j = 0; j < newList.get(i).size(); j++) {
                toCurrentNotWriteDaily.add(newList.get(i).get(j));
            }

            //更新邮件数据
            entity.setSendNum(entity.getSendNum() + newList.get(i).size());
            systemEmailRegistry.update(entity);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("您" + " " + format.format(c.getTime()) + " "+ "没有填写日报，请尽快补写日报");
            stringBuilder.append(" ").append("\n");
            stringBuilder.append("<a href='"+domainName+"'>【点击补写日报】</a>");


            System.out.println("------准备发邮件----");
            MailUtil.sendEmailToAllUser("true",entity.getHost(),
                    entity.getUsername(), entity.getPassword(),
                    "日报提醒消息",stringBuilder.toString(),
                    false,null,true,toCurrentNotWriteDaily);
        }


//        int sendCount = 0;
//        if(currentNotWriteDaily.size() % 90 == 0){
//            sendCount = currentNotWriteDaily.size() / 90;
//        }else {
//            sendCount = currentNotWriteDaily.size() / 90 + 1;
//        }
//
//        for (int i = 0; i < sendCount; i++) {
//            SystemEmailEntity entity = systemEmailRegistry.queryEmailIsSend();
//            if(entity == null){
//                return;
//            }
//            List<UserEntity> toCurrentNotWriteDaily = new ArrayList<>();
//            for(int j = 0;j < 91;j++){
//                toCurrentNotWriteDaily.add(currentNotWriteDaily.get(j + 91 * i));
//            }
//
//            //更新邮件数据
//            entity.setSendNum(91);
//            systemEmailRegistry.update(entity);
//
//            System.out.println("------准备发邮件----");
//            mailUtil.sendEmailToAllUser("true",entity.getHost(),
//                    entity.getUsername(), entity.getPassword(),
//                    "日报提醒消息","您" + " " + format.format(c.getTime()) + " "+ "没有填写日报，请尽快补写日报",
//                    false,null,true,toCurrentNotWriteDaily);
//        }


//        System.out.println("------准备发邮件----");
//        mailUtil.sendEmailToAllUser("true",systemInfoEntity.getHost(),
//                systemInfoEntity.getEmail(), systemInfoEntity.getPassword(),
//                "日报提醒消息","您" + " " + format.format(c.getTime()) + " "+ "没有填写日报，请尽快补写日报",false,null,true,currentNotWriteDaily);
    }

    private void sendEmailToAreaAndGroup() throws MessagingException, GeneralSecurityException, ParseException, BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        //给区长发日报
        List<UserEntity> userEntityList = userRegistry.queryUserByRoleCode(null);
        if (CollectionUtils.isEmpty(userEntityList)) {
            return;
        }
        List<UserEntity> managerList = new ArrayList<>();
        List<UserEntity> groupList = new ArrayList<>();
        for (UserEntity entity : userEntityList) {
            if (RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(entity.getRoleCode())) {
                managerList.add(entity);
            }
            if (RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(entity.getRoleCode())) {
                groupList.add(entity);
            }
        }
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        // 时
        c.set(Calendar.HOUR_OF_DAY, 0);
        // 分
        c.set(Calendar.MINUTE, 0);
        // 秒
        c.set(Calendar.SECOND, 0);
        // 毫秒
        c.set(Calendar.MILLISECOND, 0);

        // 时
        c2.set(Calendar.HOUR_OF_DAY, 0);
        // 分
        c2.set(Calendar.MINUTE, 0);
        // 秒
        c2.set(Calendar.SECOND, 0);
        // 毫秒
        c2.set(Calendar.MILLISECOND, 0);

        //周一
        c.add(Calendar.DATE, -7);
        //周日
        c2.add(Calendar.DATE, -1);

        Long startDate = c.getTimeInMillis();
        Long endDate = c2.getTimeInMillis();

        List<UserModel> allUsers = iUserService.getAllUsers();
        Map<Integer, String> userMap = new HashMap<>();
        for (UserModel userModel : allUsers) {
            userMap.put(userModel.getId(), userModel.getNickname());
        }

        //区长
        CacheEmailUtil.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (CollectionUtils.isNotEmpty(managerList)) {
                    for (UserEntity entity : managerList) {
                        ReqSearchDayReportVO vo = new ReqSearchDayReportVO();
                        vo.setStartDate(startDate);
                        vo.setEndDate(endDate);
                        Map<String, Object> mapParam = new HashMap<>();
                        mapParam.put("roleCode", RoleTypeEnum.convertToEnum(entity.getRoleCode()));
                        mapParam.put("areaId", entity.getAreaId());
                        mapParam.put("userId", entity.getId());
                        vo.setMap(mapParam);
                        try {
                            BasePagesDomain<ResNoneReportVO> resNoneReportVOBasePagesDomain = dayReportController.searchNoneDayReportList(vo);
                            if (CollectionUtils.isNotEmpty(resNoneReportVOBasePagesDomain.getTotalList())) {
                                List<Map<String, List<UserEntity>>> content = new ArrayList<>();
                                for (int i = 0; i < resNoneReportVOBasePagesDomain.getTotalList().size(); i++) {
                                    Map<String, List<UserEntity>> map = new HashMap<>();
                                    List<UserEntity> list = new ArrayList<>();
                                    if (CollectionUtils.isNotEmpty(resNoneReportVOBasePagesDomain.getTotalList().get(i).getUserList())) {
                                        for (int j = 0; j < resNoneReportVOBasePagesDomain.getTotalList().get(i).getUserList().size(); j++) {
                                            UserEntity userEntity = new UserEntity();
                                            userEntity.setNickname(resNoneReportVOBasePagesDomain.getTotalList().get(i).getUserList().get(j).getNickname());
                                            list.add(userEntity);
                                        }
                                    }
                                    map.put(DateUtils.convertDateToStr(DateUtils.convertLongToDate(resNoneReportVOBasePagesDomain.getTotalList().get(i).getDate())), list);
                                    content.add(map);
                                }
                                systemEmailEntity = systemEmailRegistry.queryEmailIsSend();
                                if (systemEmailEntity == null) {
                                    return;
                                }
                                systemEmailEntity.setSendNum(systemEmailEntity.getSendNum() + 1);
                                systemEmailRegistry.update(systemEmailEntity);

                                //发邮件
                                List<UserEntity> receiveUser = new ArrayList<>();
                                receiveUser.add(entity);
                                MailUtil.sendEmailToAreaOrGroup("true", systemEmailEntity.getHost(),
                                        systemEmailEntity.getUsername(), systemEmailEntity.getPassword(),
                                        DateUtils.convertDateToStr(DateUtils.convertLongToDate(startDate)) + "至" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(endDate)) + "未写日报数据提醒",
                                        content, false, null, true, receiveUser);
                            }
                        } catch (BaseServiceException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        //给组长发日报
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (UserEntity entity : groupList) {
                ReqSearchDayReportVO vo = new ReqSearchDayReportVO();
                vo.setStartDate(startDate);
                vo.setEndDate(endDate);
                Map<String, Object> mapParam = new HashMap<>();
                mapParam.put("roleCode", RoleTypeEnum.convertToEnum(entity.getRoleCode()));
                mapParam.put("areaId", entity.getAreaId());
                mapParam.put("userId", entity.getId());
                vo.setMap(mapParam);
                try {
                    BasePagesDomain<ResNoneReportVO> resNoneReportVOBasePagesDomain = dayReportController.searchNoneDayReportList(vo);
                    if (CollectionUtils.isNotEmpty(resNoneReportVOBasePagesDomain.getTotalList())) {
                        List<Map<String, List<UserEntity>>> content = new ArrayList<>();
                        for (int i = 0; i < resNoneReportVOBasePagesDomain.getTotalList().size(); i++) {
                            Map<String, List<UserEntity>> map = new HashMap<>();
                            List<UserEntity> list = new ArrayList<>();
                            if (CollectionUtils.isNotEmpty(resNoneReportVOBasePagesDomain.getTotalList().get(i).getUserList())) {
                                for (int j = 0; j < resNoneReportVOBasePagesDomain.getTotalList().get(i).getUserList().size(); j++) {
                                    UserEntity userEntity = new UserEntity();
                                    userEntity.setNickname(resNoneReportVOBasePagesDomain.getTotalList().get(i).getUserList().get(j).getNickname());
                                    list.add(userEntity);
                                }
                            }
                            map.put(DateUtils.convertDateToStr(DateUtils.convertLongToDate(resNoneReportVOBasePagesDomain.getTotalList().get(i).getDate())), list);
                            content.add(map);
                        }
                        systemEmailEntity = systemEmailRegistry.queryEmailIsSend();
                        if (systemEmailEntity == null) {
                            return;
                        }
                        systemEmailEntity.setSendNum(systemEmailEntity.getSendNum() + 1);
                        systemEmailRegistry.update(systemEmailEntity);

                        //发邮件
                        List<UserEntity> receiveUser = new ArrayList<>();
                        receiveUser.add(entity);
                        MailUtil.sendEmailToAreaOrGroup("true", systemEmailEntity.getHost(),
                                systemEmailEntity.getUsername(), systemEmailEntity.getPassword(),
                                DateUtils.convertDateToStr(DateUtils.convertLongToDate(startDate)) + "至" + DateUtils.convertDateToStr(DateUtils.convertLongToDate(endDate)) + "未写日报数据提醒",
                                content, false, null, true, receiveUser);
                    }
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }



//        systemEmailRegistry.initSystemEmail();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar c = Calendar.getInstance();
//        Calendar c2 = Calendar.getInstance();
//        Calendar c3 = Calendar.getInstance();
//        Calendar c4 = Calendar.getInstance();
//        Calendar c5 = Calendar.getInstance();
//        Calendar c6 = Calendar.getInstance();
//        Calendar c7 = Calendar.getInstance();
//        //系统邮箱
//        SystemInfoEntity systemInfoEntity = systemRegistry.querySystemInfo();
//
//        //初始化一周时间
//
//        //周一
//        c.add(Calendar.DATE, -7);
//        //周日
//        c2.add(Calendar.DATE, -1);
//        //初始化一周的时间
//        List<String> listTime = new ArrayList<>();
//        listTime.add(format.format(c.getTime()));
//        listTime.add(format.format(c2.getTime()));
//        //周二
//        c3.add(Calendar.DATE, -6);
//        listTime.add(format.format(c3.getTime()));
//        //周三
//        c4.add(Calendar.DATE, -5);
//        listTime.add(format.format(c4.getTime()));
//        //周四
//        c5.add(Calendar.DATE, -4);
//        listTime.add(format.format(c5.getTime()));
//        //周五
//        c6.add(Calendar.DATE, -3);
//        listTime.add(format.format(c6.getTime()));
//        //周六
//        c7.add(Calendar.DATE, -2);
//        listTime.add(format.format(c7.getTime()));
//
//        //查询非工作日
//        List<HolidayEntity> holidayEntities = holidayRegistry.queryHolidayByWeek(format.format(c.getTime()), format.format(c2.getTime()));
//
//
//        if(holidayEntities.size() == 0) {
//            //本周全上班
//            //查询所有角色
//            List<RoleEntity> roleEntities = roleRegistry.queryAll();
//            if(roleEntities.size() < 0){
//                return;
//            }
//            //组长角色标记
//            //Boolean areaRoleFlg = false;
//            //Boolean groupRoleFlg = false;
//            Integer areaRoleId = null;
//            Integer groupRoleId = null;
//            //找到区长，分组查询
//            for (RoleEntity roleEntity:roleEntities){
//                if(RoleTypeEnum.checkAreaManager(roleEntity.getCode())){
//                    areaRoleId = roleEntity.getId();
//                    break;
//                }
//            }
//            for (RoleEntity roleEntity:roleEntities){
//                if(RoleTypeEnum.checkGroupManager(roleEntity.getCode())){
//                    groupRoleId = roleEntity.getId();
//                    break;
//                }
//            }
//
//            systemEmailEntity = systemEmailRegistry.queryEmailIsSend();
//
//            Integer finalAreaRoleId = areaRoleId;
//            CacheEmailUtil.threadPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    //按天分组给区长发邮件
//                    if(finalAreaRoleId != null) {
//                        //查询所有区长
//                        List<UserEntity> userEntityLists = userRegistry.queryAllUserIsAreaManager(finalAreaRoleId);
//
//                        List<UserEntity> userEntityList = new ArrayList<>();
//                        if(userEntityLists.size() > 0){
//                            for(UserEntity entity:userEntityLists){
//                                if(entity.getEmailNotice()){
//                                    userEntityList.add(entity);
//                                }
//                            }
//                        }
//
//
//                        if (userEntityList.size() > 0) {
//                            for (UserEntity userEntity : userEntityList) {
//                                List<Map<String, List<UserEntity>>> list = new ArrayList<>();
//                                //周一
//                                String startTime = listTime.get(0);
//                                //周日
//                                String lastTime = listTime.get(1);
//
//
//                                if(systemEmailEntity == null){
//                                    return;
//                                }
//                                systemEmailEntity.setSendNum(systemEmailEntity.getSendNum() + 1);
//                                if(systemEmailEntity.getSendNum() > 90){
//                                    systemEmailRegistry.update(systemEmailEntity);
//                                    systemEmailEntity = systemEmailRegistry.queryEmailIsSend();
//                                }
//                                if(systemEmailEntity == null){
//                                    return;
//                                }
//
//                                for (String time : listTime) {
//                                    //根据区域ID和当前时间查询所有没有写日志的用户
//                                    Map<String, List<UserEntity>> map = new HashMap<>();
//                                    List<UserEntity> userEntities2 = userRegistry.queryGroupAllUserNotWriteDailyByTime(userEntity.getAreaId(), time);
//                                    List<UserEntity> userEntities = new ArrayList<>();
//                                    if(CollectionUtils.isNotEmpty(userEntities2)){
//                                        for(UserEntity entity:userEntities2){
//                                            if(entity.getEmailNotice() && entity.getStatus()){
//                                                //入职时间根据创建用户时间
//                                                try {
//                                                    if(DateUtils.convertDateToLong(DateUtils.convertStrToDate(time)) > DateUtils.convertDateToLong(entity.getCreateTime())){
//                                                        userEntities.add(entity);
//                                                    }
//                                                } catch (ParseException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//                                    }
//                                    map.put(time, userEntities);
//                                    list.add(map);
//                                }
//                                //发邮件
//                                List<UserEntity> receiveUser = new ArrayList<>();
//                                receiveUser.add(userEntity);
//                                try {
//                                    mailUtil.sendEmailToAreaOrGroup("true", systemEmailEntity.getHost(),
//                                            systemEmailEntity.getUsername(), systemEmailEntity.getPassword(),
//                                            startTime + "至" + lastTime + "未写日报数据提醒", list, false, null, true, receiveUser);
//                                } catch (GeneralSecurityException e) {
//                                    e.printStackTrace();
//                                } catch (MessagingException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }
//                    }
//                }
//            });
//
//            //查询所有项目负责人id
////            List<UserDistinctEntityParam> userDistinct = userRegistry.queryUserDistinct();
////
////            if(userDistinct.size() > 0){
////                //遍历项目负责人
////                for(UserDistinctEntityParam userDistinctEntityParam:userDistinct){
////                    //去重查询，属于在当前负责人下负责项目的所有员工
////                    List<UserDistinctEntityParam> userDistinct2 = userRegistry.queryUserDistinctByGroupId(userDistinctEntityParam.getUserId());
////                    if(userDistinct2.size() > 0){
////                        //遍历当前负责人下的员工
////                        for(UserDistinctEntityParam userDistinctEntityParam2:userDistinct2){
////                            //遍历时间
////                            for(String time:listTime){
////                                //根据时间和用户ID，查询所有没写日报的用户
////                                List<UserEntity> userEntities = userRegistry.queryAllUserNotWriteDailyByTimeAndUserId(time,userDistinctEntityParam2.getUserId());
////                            }
////                        }
////                    }
////                }
////            }
//
//
//            //查询所有组长
//            if(groupRoleId != null){
//            //List<UserEntity> userEntityList2 = userRegistry.queryAllUserIsAreaManager(groupRoleId);
//            //查询所有项目负责人
//            List<UserEntity> userEntityLists = userRegistry.queryUserIsProjectManager();
//
//            List<UserEntity> userEntityList2 = new ArrayList<>();
//            if(userEntityLists.size() > 0){
//                for(UserEntity entity:userEntityLists){
//                    if(entity.getEmailNotice()){
//                        userEntityList2.add(entity);
//                    }
//                }
//            }
//
//            if(userEntityList2.size() > 0){
//                //遍历组长
//                for (UserEntity userEntity:userEntityList2){
//                    List<Map<String,List<UserEntity>>> list = new ArrayList<>();
//                    String startTime = listTime.get(0);
//                    String lastTime = listTime.get(1);
//
//                    if(systemEmailEntity == null){
//                        return;
//                    }
//                    systemEmailEntity.setSendNum(systemEmailEntity.getSendNum() + 1);
//                    if(systemEmailEntity.getSendNum() > 90){
//                        systemEmailRegistry.update(systemEmailEntity);
//                        systemEmailEntity = systemEmailRegistry.queryEmailIsSend();
//                    }
//                    if(systemEmailEntity == null){
//                        return;
//                    }
//
//                    for(String time:listTime){
//                        //根据区域ID和当前时间及项目查询所有没有写日志的用户
//                        Map<String,List<UserEntity>> map = new HashMap<>();
//                        List<UserEntity> userEntities = userRegistry.queryGroupAllUserNotWriteDailyByTimeAndProject(userEntity.getId(),userEntity.getAreaId(),time);
//                        map.put(time,userEntities);
//                        list.add(map);
//                    }
//                    //发邮件
//                    List<UserEntity> receiveUser = new ArrayList<>();
//                    receiveUser.add(userEntity);
//                    mailUtil.sendEmailToAreaOrGroup("true",systemEmailEntity.getHost(),
//                            systemEmailEntity.getUsername(), systemEmailEntity.getPassword(),
//                            startTime + "至" + lastTime + "未写日报数据提醒",list,false,null,true,receiveUser);
//
//                    }
//                }
//            }
//            return;
//        }
//
//
//        //本周有不上班的日期
//        //检查上班的时间
//        for (HolidayEntity holidayEntity:holidayEntities){
//            for(int i =0; i < listTime.size(); i++){
//                if(holidayEntity.getDateStr().equals(listTime.get(i))){
//                    listTime.remove(i);
//                }
//            }
//        }
//        //剩工作日
//        //给组长发邮件
//        //给区长发邮件
//        //查询所有角色
//        List<RoleEntity> roleEntities = roleRegistry.queryAll();
//        if(roleEntities.size() < 0){
//            return;
//        }
//        //组长角色标记
//        //Boolean areaRoleFlg = false;
//        //Boolean groupRoleFlg = false;
//        Integer areaRoleId = null;
//        Integer groupRoleId = null;
//
//        //找到区长，分组查询
//        for (RoleEntity roleEntity:roleEntities){
//            if(RoleTypeEnum.checkAreaManager(roleEntity.getCode())){
//                areaRoleId = roleEntity.getId();
//               break;
//            }
//        }
//        for (RoleEntity roleEntity:roleEntities){
//                if(RoleTypeEnum.checkGroupManager(roleEntity.getCode())){
//                    groupRoleId = roleEntity.getId();
//                    break;
//            }
//        }
//        //可以用的邮箱
//        systemEmailEntity = systemEmailRegistry.queryEmailIsSend();
//
//        Integer finalAreaRoleId = areaRoleId;
//        CacheEmailUtil.threadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                //按天分组给区长发邮件
//                if(finalAreaRoleId != null) {
//                    //查询所有区长
//                    List<UserEntity> userEntityLists = userRegistry.queryAllUserIsAreaManager(finalAreaRoleId);
//
//                    List<UserEntity> userEntityList = new ArrayList<>();
//                    if(userEntityLists.size() > 0){
//                        for(UserEntity entity:userEntityLists){
//                            if(entity.getEmailNotice()){
//                                userEntityList.add(entity);
//                            }
//                        }
//                    }
//
//                    if (userEntityList.size() > 0) {
//                        for (UserEntity userEntity : userEntityList) {
//                            List<Map<String, List<UserEntity>>> list = new ArrayList<>();
//                            String startTime = listTime.get(0);
//                            String lastTime = listTime.get(listTime.size() - 1);
//
//                            if(systemEmailEntity == null){
//                                return;
//                            }
//                            systemEmailEntity.setSendNum(systemEmailEntity.getSendNum() + 1);
//                            systemEmailRegistry.update(systemEmailEntity);
//                            if(systemEmailEntity.getSendNum() > 90){
//                                systemEmailEntity = systemEmailRegistry.queryEmailIsSend();
//                            }
//                            if(systemEmailEntity == null){
//                                return;
//                            }
//
//                            for (String time : listTime) {
//                                //根据区域ID和当前时间查询所有没有写日志的用户
//                                Map<String, List<UserEntity>> map = new HashMap<>();
//                                List<UserEntity> userEntities = userRegistry.queryGroupAllUserNotWriteDailyByTime(userEntity.getAreaId(), time);
//                                map.put(time, userEntities);
//                                list.add(map);
//                            }
//                            //发邮件
//                            List<UserEntity> receiveUser = new ArrayList<>();
//                            receiveUser.add(userEntity);
//                            try {
//                                mailUtil.sendEmailToAreaOrGroup("true", systemEmailEntity.getHost(),
//                                        systemEmailEntity.getUsername(), systemEmailEntity.getPassword(),
//                                        startTime + "至" + lastTime + "未写日报数据提醒", list, false, null, true, receiveUser);
//                            } catch (GeneralSecurityException e) {
//                                e.printStackTrace();
//                            } catch (MessagingException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//                }
//            }
//        });
//
//
//        //查询所有组长
//        if(groupRoleId != null){
//        //List<UserEntity> userEntityList2 = userRegistry.queryAllUserIsAreaManager(groupRoleId);
//        //查询所有项目负责人
//        List<UserEntity> userEntityLists = userRegistry.queryUserIsProjectManager();
//
//        List<UserEntity> userEntityList2 = new ArrayList<>();
//        if(userEntityLists.size() > 0){
//            for(UserEntity entity:userEntityLists){
//                if(entity.getEmailNotice()){
//                    userEntityList2.add(entity);
//                }
//            }
//        }
//
//        if(userEntityList2.size() > 0){
//            for (UserEntity userEntity:userEntityList2){
//                List<Map<String,List<UserEntity>>> list = new ArrayList<>();
//                String startTime = listTime.get(0);
//                String lastTime = listTime.get(1);
//
//                if(systemEmailEntity == null){
//                    return;
//                }
//                systemEmailEntity.setSendNum(systemEmailEntity.getSendNum() + 1);
//                if(systemEmailEntity.getSendNum() > 90){
//                    systemEmailRegistry.update(systemEmailEntity);
//                    systemEmailEntity = systemEmailRegistry.queryEmailIsSend();
//                }
//                if(systemEmailEntity == null){
//                    return;
//                }
//
//                for(String time:listTime){
//                    //根据区域ID和当前时间及项目查询所有没有写日志的用户
//                    Map<String,List<UserEntity>> map = new HashMap<>();
//                    List<UserEntity> userEntities = userRegistry.queryGroupAllUserNotWriteDailyByTimeAndProject(userEntity.getId(),userEntity.getAreaId(),time);
//                    map.put(time,userEntities);
//                    list.add(map);
//                }
//                //发邮件
//                List<UserEntity> receiveUser = new ArrayList<>();
//                receiveUser.add(userEntity);
//                mailUtil.sendEmailToAreaOrGroup("true",systemEmailEntity.getHost(),
//                        systemEmailEntity.getUsername(), systemEmailEntity.getPassword(),
//                        startTime + "至" + lastTime + "未写日报数据提醒",list,false,null,true,receiveUser);
//
//                }
//            }
//        }


    /**
     * 同步crm的项目到项目字典
     * 发邮件通知成败
     */
    @Override
    public ResSyncSystemProjectVO syncSystemProjectFromCrmAndSendEmail() throws DynamicServiceException, MessagingException, GeneralSecurityException {

        System.out.println("————————————开始从CRM同步项目名称—————————————");
        String title="";
        String msg = "";
        String strDetail = "";
        //调用crm的api成功失败标识
        boolean connApiSuccess = false;
        //返回前端成功失败状态标识
        boolean result = true;
        List<CrmProjectSyncParam> res = new ArrayList<>();
        Gson gson = new Gson();
        /*try {
            //调用crm的api返回项目列表
            ResponseEntity<String> results = restTemplate.exchange(crmProjectSyncUrl, HttpMethod.GET, null, String.class);
            String json = results.getBody();
            res = gson.fromJson(json, new TypeToken<List<CrmProjectSyncParam>>() {
            }.getType());
            connApiSuccess = true;
        }catch (Exception e){
            connApiSuccess = false;
            result = false;
            msg = "调用crm同步项目接口失败，请查看邮件详情 " ;
            strDetail = e.getMessage();
        }*/
        try {
            res = jdbcTemplate.query("select distinct '' as customerNameShort, bo.name as projectDecription, bo.code as projectId, bo.stage as saleStageID, ap.name as stageStatus from bu_opportunity bo inner join bu_salessupport bs on bo.id = bs.opportunityId and bo.reportStatus = '00000402' left join ab_processs ap on ap.id = bo.stage",
                    new RowMapper<CrmProjectSyncParam>() {
                        @Override
                        public CrmProjectSyncParam mapRow(ResultSet rs, int rowNum) throws SQLException {
                            CrmProjectSyncParam cpjsp = new CrmProjectSyncParam();
                            cpjsp.setProjectId(rs.getString("projectId"));
                            cpjsp.setCustomerNameShort(rs.getString("customerNameShort"));
                            cpjsp.setProjectdecription(rs.getString("projectDecription"));
                            cpjsp.setStageStatus(rs.getString("stageStatus"));
                            cpjsp.setSaleStageID(rs.getString("saleStageID"));
                            return cpjsp;
                        }
                    });
            connApiSuccess = true;
        } catch (Exception e){
            result = false;
            msg = "调用crm数据库同步失败，请查看邮件详情 " ;
            strDetail = e.getMessage();
        }

        List<ReqSynSystemProjectErrorVO> resList = new ArrayList<>();
        if(connApiSuccess) {
            List<ReqRpaSystemProjectVO> vos = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(res)) {
                for (CrmProjectSyncParam re : res) {
                    ReqRpaSystemProjectVO vo = new ReqRpaSystemProjectVO();
                    vo.setCrmProjectId(re.getProjectId());
                    vo.setName(re.getProjectdecription());
                    vo.setCrmStageId(re.getSaleStageID());
                    vo.setCrmStageName(re.getStageStatus());
                    vo.setCrmCustomerNameShort(re.getCustomerNameShort());
                    vos.add(vo);
                }
            }

            //同步本地的项目字典
            try {
                if(CollectionUtils.isNotEmpty(vos)) {
                    Map<String, Object> resMap = iSystemProjectService.checkCrmProject(vos);
                    resList = resMap.get("resList") == null ? new ArrayList<ReqSynSystemProjectErrorVO>() : (List<ReqSynSystemProjectErrorVO>) resMap.get("resList");
                    strDetail = resMap.get("strDetail") == null ? "" : (String) resMap.get("strDetail");
                }
            }catch (Exception e){
                result = false;
                msg = "同步出现异常，请查看邮件详情 ";
                strDetail = strDetail + e.getMessage();
            }


            if (CollectionUtils.isEmpty(resList) && result == true) {
                msg = "同步成功 ";
                title = "CRM项目名同步【成功】";
            } else {
                result = false;
                title = "CRM项目名同步【异常】";
                msg = "同步出现异常，请查看邮件详情 ";
            }
        }

        String content = CollectionUtils.isEmpty(resList)?"":gson.toJson(resList);


        //指定收件人
        List<UserEntity> receiveUser = new ArrayList<>();
        UserEntity user = new UserEntity();
        user.setEmail(syncProjectManagerEmail);
        user.setNickname(syncProjectManagerName);
        receiveUser.add(user);
        //发送邮件
        MailUtil.sendEmailToSyncProjectManager("true",syncProjectSystemSendEmailHost,
                syncProjectSystemSendEmail, syncProjectSystemSendEmailPassword,
                title,msg+" <br/> " + strDetail + content,
                false,null,true,receiveUser);

        System.out.println("————————————CRM同步项目名称完毕—————————————");



        ResSyncSystemProjectVO resVO = new ResSyncSystemProjectVO();
        resVO.setResult(result);
        resVO.setMessage(msg);
        return resVO;

    }

}
