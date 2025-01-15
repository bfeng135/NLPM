package com.nl.pm.server.common.cron;

import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.enums.AreaEnum;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.*;
import com.nl.pm.server.registry.entity.*;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.UserComeLeaveModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.utils.MailUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.mail.MessagingException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
public class SendEmailCron {

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
    private IUserService iUserService;

    //每5秒执行一次
    //@Scheduled(cron = "0/5 * * * * ?")

    //每天九点
    //@Scheduled(cron = "0 0 9 * * ?")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void sendEmailToAllUser() throws MessagingException, GeneralSecurityException, BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, ParseException {
        List<UserModel> allUsers = iUserService.getAllUsers();
        Map<Integer, UserModel> userAllInfoMap = new HashMap<>();
        for (UserModel userModel : allUsers) {
            userAllInfoMap.put(userModel.getId(),userModel);
        }



        //设定人员入职离职的时间间隔生效的天数
        Map<Integer,Set<String>> userComeLeaveBetweenDaysMap = new HashMap<>();
        List<UserComeLeaveModel> userComeLeaveModels = iUserService.searchUserComeLeaveList(null);
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


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        HolidayEntity holidayEntity = holidayRegistry.queryHolidayByDate(format.format(c.getTime()));
        if(holidayEntity != null){
            return;
        }
        SystemInfoEntity systemInfoEntity = systemRegistry.querySystemInfo();
        if(systemInfoEntity == null){
            return;
        }
        //c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        List<UserEntity> notWriteDaily = userRegistry.queryAllUserNotWriteDaily(format.format(c.getTime()));
        List<UserEntity> currentNotWriteDaily = new ArrayList<>();
        if (notWriteDaily.size() > 0){
            for(UserEntity entity :notWriteDaily){
                String areaName = entity.getAreaName();
                if(AreaEnum.公司管理大区.getCode().equals(areaName)){
                    continue;
                }
                if(entity.getCreateTime().getTime()>(c.getTimeInMillis()+57600000)){
                    continue;
                }

                if(entity.getEmailNotice()){
                    Set<String> betweenDaySet = userComeLeaveBetweenDaysMap.get(entity.getId());
                    if(betweenDaySet.contains(format.format(c.getTime()))) {
                        currentNotWriteDaily.add(entity);
                    }
                }
            }
        }

        if(currentNotWriteDaily.size() == 0){
            return;
        }


        SystemEmailEntity entity = systemEmailRegistry.queryEmailIsSend();


        System.out.println("------准备发邮件----");
        mailUtil.sendEmailToAllUser("true",systemInfoEntity.getHost(),
                systemInfoEntity.getEmail(), systemInfoEntity.getPassword(),
                "日报提醒消息","您" + " " + format.format(c.getTime()) + " "+ "没有填写日报，请尽快补写日报",false,null,true,currentNotWriteDaily);
    }

    //每周一10点
    //@Scheduled(cron = "0 0 10 ? * 1")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void sendEmailToAreaAndGroup() throws MessagingException, GeneralSecurityException, BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, ParseException {

        List<UserModel> allUsers = iUserService.getAllUsers();
        Map<Integer, UserModel> userAllInfoMap = new HashMap<>();
        for (UserModel userModel : allUsers) {
            userAllInfoMap.put(userModel.getId(),userModel);
        }



        //设定人员入职离职的时间间隔生效的天数
        Map<Integer,Set<String>> userComeLeaveBetweenDaysMap = new HashMap<>();
        List<UserComeLeaveModel> userComeLeaveModels = iUserService.searchUserComeLeaveList(null);
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







        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Calendar c3 = Calendar.getInstance();
        Calendar c4 = Calendar.getInstance();
        Calendar c5 = Calendar.getInstance();
        Calendar c6 = Calendar.getInstance();
        Calendar c7 = Calendar.getInstance();
        //系统邮箱
        SystemInfoEntity systemInfoEntity = systemRegistry.querySystemInfo();

        //初始化一周时间

        //周一
        c.add(Calendar.DATE, -7);
        //周日
        c2.add(Calendar.DATE, -1);
        //初始化一周的时间
        List<String> listTime = new ArrayList<>();
        listTime.add(format.format(c.getTime()));
        listTime.add(format.format(c2.getTime()));
        //周二
        c3.add(Calendar.DATE, -6);
        listTime.add(format.format(c3.getTime()));
        //周三
        c4.add(Calendar.DATE, -5);
        listTime.add(format.format(c4.getTime()));
        //周四
        c5.add(Calendar.DATE, -4);
        listTime.add(format.format(c5.getTime()));
        //周五
        c6.add(Calendar.DATE, -3);
        listTime.add(format.format(c6.getTime()));
        //周六
        c7.add(Calendar.DATE, -2);
        listTime.add(format.format(c7.getTime()));

        //查询非工作日
        List<HolidayEntity> holidayEntities = holidayRegistry.queryHolidayByWeek(format.format(c.getTime()), format.format(c2.getTime()));


        if (holidayEntities.size() == 0) {
            //本周全上班
            //查询所有角色
            List<RoleEntity> roleEntities = roleRegistry.queryAll();
            if (roleEntities.size() < 0) {
                return;
            }
            //组长角色标记
            Boolean areaRoleFlg = false;
            Boolean groupRoleFlg = false;
            Integer areaRoleId = null;
            Integer groupRoleId = null;
            for (RoleEntity roleEntity : roleEntities) {
                //找到区长，分组查询
                if (!areaRoleFlg) {
                    if (RoleTypeEnum.checkAreaManager(roleEntity.getCode())) {
                        areaRoleId = roleEntity.getId();
                        //break;
                        areaRoleFlg = true;
                    }
                }

                if (!areaRoleFlg) {
                    if (RoleTypeEnum.checkGroupManager(roleEntity.getCode())) {
                        groupRoleId = roleEntity.getId();
                        //break;
                        groupRoleFlg = true;
                    }
                }
            }
            //按天分组给区长发邮件
            if (areaRoleId != null) {
                //查询所有区长
                List<UserEntity> userEntityLists = userRegistry.queryAllUserIsAreaManager(areaRoleId);

                List<UserEntity> userEntityList = new ArrayList<>();
                if (userEntityLists.size() > 0) {
                    for (UserEntity entity : userEntityLists) {
                        if (entity.getEmailNotice()) {
                            userEntityList.add(entity);
                        }
                    }
                }


                if (userEntityList.size() > 0) {
                    for (UserEntity userEntity : userEntityList) {
                        List<Map<String, List<UserEntity>>> list = new ArrayList<>();
                        //周一
                        String startTime = listTime.get(0);
                        //周日
                        String lastTime = listTime.get(1);

                        for (String time : listTime) {
                            //根据区域ID和当前时间查询所有没有写日志的用户
                            Map<String, List<UserEntity>> map = new HashMap<>();
                            List<UserEntity> userEntities = userRegistry.queryGroupAllUserNotWriteDailyByTime(userEntity.getAreaId(), time);
                            List<UserEntity> noReportUsers = new ArrayList<>();
                            if(CollectionUtils.isNotEmpty(userEntities)){
                                for (UserEntity entity : userEntities) {
                                    Set<String> betweenDaySet = userComeLeaveBetweenDaysMap.get(entity.getId());
                                    if(betweenDaySet.contains(time)) {
                                        noReportUsers.add(entity);
                                    }
                                }

                            }
                            map.put(time, noReportUsers);
                            list.add(map);
                        }
                        //发邮件
                        List<UserEntity> receiveUser = new ArrayList<>();
                        receiveUser.add(userEntity);
                        mailUtil.sendEmailToAreaOrGroup("true", systemInfoEntity.getHost(),
                                systemInfoEntity.getEmail(), systemInfoEntity.getPassword(),
                                startTime + "至" + lastTime + "未写日报数据提醒", list, false, null, true, receiveUser);

                    }
                }
            }

            //查询所有项目负责人id
//            List<UserDistinctEntityParam> userDistinct = userRegistry.queryUserDistinct();
//
//            if(userDistinct.size() > 0){
//                //遍历项目负责人
//                for(UserDistinctEntityParam userDistinctEntityParam:userDistinct){
//                    //去重查询，属于在当前负责人下负责项目的所有员工
//                    List<UserDistinctEntityParam> userDistinct2 = userRegistry.queryUserDistinctByGroupId(userDistinctEntityParam.getUserId());
//                    if(userDistinct2.size() > 0){
//                        //遍历当前负责人下的员工
//                        for(UserDistinctEntityParam userDistinctEntityParam2:userDistinct2){
//                            //遍历时间
//                            for(String time:listTime){
//                                //根据时间和用户ID，查询所有没写日报的用户
//                                List<UserEntity> userEntities = userRegistry.queryAllUserNotWriteDailyByTimeAndUserId(time,userDistinctEntityParam2.getUserId());
//                            }
//                        }
//                    }
//                }
//            }


            //查询所有组长
            if (groupRoleId != null) {
                //List<UserEntity> userEntityList2 = userRegistry.queryAllUserIsAreaManager(groupRoleId);
                //查询所有项目负责人
                List<UserEntity> userEntityLists = userRegistry.queryUserIsProjectManager();

                List<UserEntity> userEntityList2 = new ArrayList<>();
                if (userEntityLists.size() > 0) {
                    for (UserEntity entity : userEntityLists) {
                        if (entity.getEmailNotice()) {
                            userEntityList2.add(entity);
                        }
                    }
                }

                if (userEntityList2.size() > 0) {
                    //遍历组长
                    for (UserEntity userEntity : userEntityList2) {
                        List<Map<String, List<UserEntity>>> list = new ArrayList<>();
                        String startTime = listTime.get(0);
                        String lastTime = listTime.get(1);
                        for (String time : listTime) {
                            //根据区域ID和当前时间及项目查询所有没有写日志的用户
                            Map<String, List<UserEntity>> map = new HashMap<>();
                            List<UserEntity> userEntities = userRegistry.queryGroupAllUserNotWriteDailyByTimeAndProject(userEntity.getId(), userEntity.getAreaId(), time);
                            List<UserEntity> noReportUsers = new ArrayList<>();
                            if(CollectionUtils.isNotEmpty(userEntities)){
                                for (UserEntity entity : userEntities) {
                                    Set<String> betweenDaySet = userComeLeaveBetweenDaysMap.get(entity.getId());
                                    if(betweenDaySet.contains(time)) {
                                        noReportUsers.add(entity);
                                    }
                                }

                            }

                            map.put(time, noReportUsers);
                            list.add(map);
                        }
                        //发邮件
                        List<UserEntity> receiveUser = new ArrayList<>();
                        receiveUser.add(userEntity);
                        mailUtil.sendEmailToAreaOrGroup("true", systemInfoEntity.getHost(),
                                systemInfoEntity.getEmail(), systemInfoEntity.getPassword(),
                                startTime + "至" + lastTime + "未写日报数据提醒", list, false, null, true, receiveUser);

                    }
                }
                //}
            }
            return;
        }


        //本周有不上班的日期
        //检查上班的时间
        for (HolidayEntity holidayEntity : holidayEntities) {
            for (int i = 0; i < listTime.size(); i++) {
                if (holidayEntity.getDateStr().equals(listTime.get(i))) {
                    listTime.remove(i);
                }
            }
        }
        //剩工作日
        //给组长发邮件
        //给区长发邮件
        //查询所有角色
        List<RoleEntity> roleEntities = roleRegistry.queryAll();
        if (roleEntities.size() < 0) {
            return;
        }
        //组长角色标记
        Boolean areaRoleFlg = false;
        Boolean groupRoleFlg = false;
        Integer areaRoleId = null;
        Integer groupRoleId = null;
        for (RoleEntity roleEntity : roleEntities) {
            //找到区长，分组查询
            if (!areaRoleFlg) {
                if (RoleTypeEnum.checkAreaManager(roleEntity.getCode())) {
                    areaRoleId = roleEntity.getId();
                    //break;
                    areaRoleFlg = true;
                }
            }

            if (!areaRoleFlg) {
                if (RoleTypeEnum.checkGroupManager(roleEntity.getCode())) {
                    groupRoleId = roleEntity.getId();
                    //break;
                    groupRoleFlg = true;
                }
            }
        }
        //按天分组给区长发邮件
        if (areaRoleId != null) {
            //查询所有区长
            List<UserEntity> userEntityLists = userRegistry.queryAllUserIsAreaManager(areaRoleId);

            List<UserEntity> userEntityList = new ArrayList<>();
            if (userEntityLists.size() > 0) {
                for (UserEntity entity : userEntityLists) {
                    if (entity.getEmailNotice()) {
                        userEntityList.add(entity);
                    }
                }
            }

            if (userEntityList.size() > 0) {
                for (UserEntity userEntity : userEntityList) {
                    List<Map<String, List<UserEntity>>> list = new ArrayList<>();
                    String startTime = listTime.get(0);
                    String lastTime = listTime.get(1);
                    for (String time : listTime) {
                        //根据区域ID和当前时间查询所有没有写日志的用户
                        Map<String, List<UserEntity>> map = new HashMap<>();
                        List<UserEntity> userEntities = userRegistry.queryGroupAllUserNotWriteDailyByTime(userEntity.getAreaId(), time);
                        List<UserEntity> noReportUsers = new ArrayList<>();
                        if(CollectionUtils.isNotEmpty(userEntities)){
                            for (UserEntity entity : userEntities) {
                                Set<String> betweenDaySet = userComeLeaveBetweenDaysMap.get(entity.getId());
                                if(betweenDaySet.contains(time)) {
                                    noReportUsers.add(entity);
                                }
                            }

                        }

                        map.put(time, noReportUsers);
                        list.add(map);
                    }
                    //发邮件
                    List<UserEntity> receiveUser = new ArrayList<>();
                    receiveUser.add(userEntity);
                    mailUtil.sendEmailToAreaOrGroup("true", systemInfoEntity.getHost(),
                            systemInfoEntity.getEmail(), systemInfoEntity.getPassword(),
                            startTime + "至" + lastTime + "未写日报数据提醒", list, false, null, true, receiveUser);

                }
            }
        }

        //查询所有组长
        if (groupRoleId != null) {
            //List<UserEntity> userEntityList2 = userRegistry.queryAllUserIsAreaManager(groupRoleId);
            //查询所有项目负责人
            List<UserEntity> userEntityLists = userRegistry.queryUserIsProjectManager();

            List<UserEntity> userEntityList2 = new ArrayList<>();
            if (userEntityLists.size() > 0) {
                for (UserEntity entity : userEntityLists) {
                    if (entity.getEmailNotice()) {
                        userEntityList2.add(entity);
                    }
                }
            }

            if (userEntityList2.size() > 0) {
                for (UserEntity userEntity : userEntityList2) {
                    List<Map<String, List<UserEntity>>> list = new ArrayList<>();
                    String startTime = listTime.get(0);
                    String lastTime = listTime.get(1);
                    for (String time : listTime) {
                        //根据区域ID和当前时间及项目查询所有没有写日志的用户
                        Map<String, List<UserEntity>> map = new HashMap<>();
                        List<UserEntity> userEntities = userRegistry.queryGroupAllUserNotWriteDailyByTimeAndProject(userEntity.getId(), userEntity.getAreaId(), time);
                        List<UserEntity> noReportUsers = new ArrayList<>();
                        if(CollectionUtils.isNotEmpty(userEntities)){
                            for (UserEntity entity : userEntities) {
                                Set<String> betweenDaySet = userComeLeaveBetweenDaysMap.get(entity.getId());
                                if(betweenDaySet.contains(time)) {
                                    noReportUsers.add(entity);
                                }
                            }

                        }

                        map.put(time, noReportUsers);
                        list.add(map);
                    }
                    //发邮件
                    List<UserEntity> receiveUser = new ArrayList<>();
                    receiveUser.add(userEntity);
                    mailUtil.sendEmailToAreaOrGroup("true", systemInfoEntity.getHost(),
                            systemInfoEntity.getEmail(), systemInfoEntity.getPassword(),
                            startTime + "至" + lastTime + "未写日报数据提醒", list, false, null, true, receiveUser);

                }
            }
            //}

        }


//    //每天九点
//    @Scheduled(cron = "0 0 9 * * ?")
//    private void sendEmail() throws MessagingException {
//        SystemInfoEntity systemInfoEntity = null;
//        List<UserEntity> userEntities = null;
//        if(CacheEmailUtil.systemInfoEntities.size() < 1){
//            systemInfoEntity = systemRegistry.querySystemInfo();
//            synchronized (CacheEmailUtil.systemInfoEntities){
//                CacheEmailUtil.systemInfoEntities.add(systemInfoEntity);
//            }
//        }
//        if(CacheUserUtil.userEntities.size() < 0){
//            userEntities = userRegistry.queryAllUser();
//            synchronized (CacheUserUtil.userEntities){
//                for(UserEntity userEntity:userEntities){
//                    CacheUserUtil.userEntities.add(userEntity);
//                }
//            }
//        }
//        mailUtil.sendEmailToAllUser("true",CacheEmailUtil.systemInfoEntities.get(0).getHost(),
//                CacheEmailUtil.systemInfoEntities.get(0).getEmail(),
//                CacheEmailUtil.systemInfoEntities.get(0).getPassword(),
//                "日报提醒消息","没有填写日报，请尽快补写日报",false,null,true,null);
//    }
    }
}
