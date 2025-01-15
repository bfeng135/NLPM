package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.enums.UserStatusEnum;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IRoleRegistry;
import com.nl.pm.server.registry.IUserRegistry;
import com.nl.pm.server.registry.dao.AreaDao;
import com.nl.pm.server.registry.dao.ProjectDao;
import com.nl.pm.server.registry.dao.UserDao;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.UserComeLeaveEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.*;
import com.nl.pm.server.security.tools.TokenTools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public class UserRegistryImpl implements IUserRegistry {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private IRoleRegistry roleRegistry;
//    @Autowired
//    private IAreaRegistry areaRegistry;
//    @Autowired
//    private IProjectRegistry projectRegistry;
    @Autowired
    private AreaDao areaDao;


    @Override
    public Double queryTotalTime(Integer[] areaId, String projectName) {
        return projectDao.queryTotalTime(areaId,projectName);
    }

    @Override
    public List<UserEntity> queryUserByRoleCode(String code) {
        return userDao.queryUserByRoleCode(code);
    }

    @Override
    public UserEntity getUserByUsernameAndPassword(String username, String password, Integer areaId) {
        UserEntity entity = userDao.getUserByUsernameAndPassword(username, password, areaId);
        return entity;
    }

    @Override
    public UserEntity loadUserByUsername(String username) {
        Integer currentAreaId = TokenTools.getCurrentAreaId();
        UserEntity entity = userDao.loadUserByUsername(username,currentAreaId);
       return entity;
    }

    @Override
    public void loginTokenUpdate(String username, String token) {
        userDao.loginTokenUpdate(username,token);
    }

    @Override
    public void logout(String username) {
        userDao.logout(username);
    }

    @Override
    public Integer checkTokenExist(String token) {
        Integer count = userDao.checkTokenExist(token);
        return count;
    }

    @Override
    public List<UserEntity> queryAllUserNotWriteDaily(String date) {
        return userDao.queryAllUserNotWriteDaily(date);
    }

    @Override
    public List<UserEntity> queryAllUser() {
        return userDao.queryAllUser();
    }

    @Override
    public List<UserEntity> queryAllEnableUser() {
        return userDao.queryAllEnableUser();
    }

    @Override
    public UserEntity saveUser(UserEntity entity) {
        userDao.insert(entity);
        projectDao.initUserAssProject(entity.getId(),entity.getAreaId());
        return entity;
    }

    @Override
    public UserEntity queryUserByUserName(String userName) {
        return userDao.queryUserByUserName(userName);
    }

    @Override
    public IPage<UserEntity> queryUserPaging(Page<UserEntity> page, String searchVal) {
        return userDao.queryUserPagingTest(page,searchVal);
    }

    @Override
    public IPage<UserEntity> queryUserPagingByRoleIds(Page<UserEntity> page, String searchVal, List<Integer> ids) {
        return userDao.queryUserPagingByRoleIds(page,searchVal,ids);
    }

    @Override
    @Transactional
    public void editUserStatus(Long userId) throws BaseServiceException {
        UserEntity userEntity2 = userDao.selectById(userId);
        UserEntity userEntity = userDao.queryUserByUserName(userEntity2.getUsername());

        if (userEntity == null) {
            new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_EXIST);
        }
        Boolean flag = userEntity.getStatus();
        UserStatusEnum statusEnum;
        if(!flag){
            if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(userEntity.getRoleCode())) {
                //查询该区域下是否有角色
                Integer integer = roleRegistry.queryCountByAreaId(userEntity.getAreaId());
                if (integer != null && integer > 0) {
                    throw new BaseServiceException(ServiceErrorCodeEnum.AREA_EXITS_MANAGER_ERROR);
                }
            }
            userEntity.setStatus(true);
            statusEnum = UserStatusEnum.COME;
        }else {
            if(RoleTypeEnum.AREA_MANAGER == RoleTypeEnum.convertToEnum(userEntity.getRoleCode())
            || RoleTypeEnum.GROUP_MANAGER == RoleTypeEnum.convertToEnum(userEntity.getRoleCode())) {
                //查询区域负责人
                Integer integer = areaDao.queryCountManager(userEntity.getId());

                //查询项目负责人
                Integer integer1 = projectDao.queryCountManager(userEntity.getId());
                if(integer + integer1 > 0){
                    throw new BaseServiceException(ServiceErrorCodeEnum.USER_DEL_EXITS_AREA_PROJECT_MANAGER);
                }

            }
            userEntity.setStatus(false);
            userEntity.setToken(StringUtils.EMPTY);

            //projectDao.delRelevanceAreaAndProject(userEntity.getId());

            statusEnum = UserStatusEnum.LEAVE;
        }
        userDao.updateById(userEntity);

        //给用户增加在职和离开的时间节点
        if(statusEnum == UserStatusEnum.COME){
            userDao.insertUserComeDate(userEntity.getId(),new Date());
        }else if(statusEnum == UserStatusEnum.LEAVE){
            userDao.updateUserLeaveDate(userEntity.getId(),new Date());
        }
    }

    @Override
    public UserEntity queryUserInfoById(Long userId) {
        return userDao.selectUserInfoById(userId);
    }

    @Override
    public void delUserByUserId(Long userId) throws BaseServiceException {
        UserEntity userEntity = userDao.selectUserByProjectOrReport(userId);
        if(userEntity != null){
            throw new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_DELETE);
        }
        userDao.deleteById(userId);
    }

    @Override
    public IPage<UserEntity> queryUserPaging(QueryUserListEntityParam entityParam) {
        Page<UserEntity> page = new Page<>(entityParam.getCurrentPage(), entityParam.getPageSize());
        return userDao.queryUserPaging(page,entityParam);
    }

    @Override
    public List<UserEntity> searchUserByRoleOrArea(SearchUserByRoleOrAreaEntityParam entityParam) {
        return userDao.searchUserByRoleOrArea(entityParam);
    }

    @Override
    public List<UserEntity> searchUserByAss(Integer areaId, List<String> roleCode) {
        return userDao.searchUserByAss(areaId,roleCode);
    }

    @Override
    public void updateUserPassword(UserEntity userEntity) {
        userDao.updateById(userEntity);
    }

    @Override
    public List<UserEntity> searchUserListByIds(List<Integer> userIdList) {
        return userDao.searchUserListByIds(userIdList);
    }

    @Override
    public void editEmailNotice(Integer userId) {
        UserEntity userEntity = userDao.selectById(userId);

        if (userEntity == null) {
            new BaseServiceException(ServiceErrorCodeEnum.USER_NOT_EXIST);
        }
        Boolean flag = userEntity.getEmailNotice();

        if(!flag){
            userEntity.setEmailNotice(true);
        }else {
            userEntity.setEmailNotice(false);
        }
        userDao.updateById(userEntity);
    }

    @Override
    public void initPassword(UserEntity userEntity) {
        userDao.updateById(userEntity);
    }

    @Override
    public void editUserInfo(UserEntity userEntity) {
        userDao.updateById(userEntity);
        projectDao.initUserAssProject(userEntity.getId(),userEntity.getAreaId());
    }

    @Override
    public List<UserEntity> searchUserByProjectAndArea(Integer areaId, Integer userId) {
        return userDao.searchUserByProjectAndArea(areaId,userId);
    }

    @Override
    public List<UserEntity> queryAllUserIsAreaManager(Integer roleId) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id",roleId);
        return userDao.selectList(queryWrapper);
    }

    @Override
    public List<UserEntity> queryGroupAllUserNotWriteDailyByTime(Integer areaId,String time) {
        return userDao.queryGroupAllUserNotWriteDailyByTime(areaId,time);
    }

    @Override
    public List<UserEntity> queryGroupAllUserNotWriteDailyByTimeAndProject(Integer userId,Integer areaId, String time) {
        return userDao.queryGroupAllUserNotWriteDailyByTimeAndProject(userId,areaId,time);
    }

    @Override
    public List<UserEntity> queryUserByProjectIdAndAreaId(String startTime,String endTime,Integer projectId, Integer areaId) {
        return userDao.queryUserByProjectIdAndAreaId(startTime,endTime,projectId, areaId);
    }

    @Override
    public List<UserEntity> queryAllUserByProjectIdOrAreaId(String startTime, String endTime, Integer[] areaId, Integer[] projectId) {
        return userDao.queryAllUserByProjectIdOrAreaId(startTime, endTime, areaId,projectId);
    }

    @Override
    public List<UserDistinctEntityParam> queryUserDistinct() {
        return userDao.queryUserDistinct();
    }

    @Override
    public List<UserDistinctEntityParam> queryUserDistinctByGroupId(Integer userId) {
        return userDao.queryUserDistinctByGroupId(userId);
    }

    @Override
    public List<UserEntity> queryAllUserNotWriteDailyByTimeAndUserId(String time, Integer userId) {
        return userDao.queryAllUserNotWriteDailyByTimeAndUserId(time,userId);
    }

    @Override
    public List<UserEntity> queryUserIsProjectManager() {
        return userDao.queryUserIsProjectManager();
    }

    @Override
    public List<UserEntity> queryUserProjectAndDistinct(String startTime, String endTime,Integer[] areaId,Integer[] projectId) {
        return userDao.queryUserProjectAndDistinct(startTime,endTime,areaId,projectId);
    }

    @Override
    public List<UserEntity> queryUserWorkTime(String startTime, String endTime, Integer[] areaId, String projectName) {
        return userDao.queryUserWorkTime(startTime,endTime,areaId,projectName);
    }

    @Override
    public List<UserEntity> queryUserDistinctByTime(String startTime, String endTime, Integer[] areaId, String projectName) {
        return userDao.queryUserDistinctByTime(startTime,endTime,areaId,projectName);
    }

    @Override
    public List<ResEveryUserTotalTimeEntityParam> queryEveryUserTotalTime(String startTime, String endTime, Integer[] areaId, Integer[] projectId) {
        return userDao.queryEveryUserTotalTime(startTime,endTime,areaId,projectId);
    }

    @Override
    public List<UserEntity> searchUserWhoCanBeAssignToProjectByAreaId(Integer areaId) {
        return userDao.searchUserWhoCanBeAssignToProjectByAreaId(areaId);
    }

    @Override
    public List<ResEveryUserHolidayEntityParam> queryEveryUserHoliday(String startTime, String endTime, Integer[] areaId, Integer[] projectId) {
        return userDao.queryEveryUserHoliday(startTime,endTime,areaId,projectId);
    }

    @Override
    public Integer queryProjectCountByUserId(Integer userId) {
        return userDao.queryProjectCountByUserId(userId);
    }

    @Override
    public Integer queryReportCountByUserId(Integer userId) {
        return userDao.queryReportCountByUserId(userId);
    }

    @Override
    public List<UserEntity> getAllOtherAreaUser(Integer areaId) {
        return userDao.getAllOtherAreaUser(areaId);
    }

    @Override
    public List<AreaUsersEntityParam> searchAreaUserCountList() {
        return userDao.searchAreaUserCountList();
    }

    @Override
    public Integer delProjectUserByUserId(Integer userId) {
        return userDao.delProjectUserByUserId(userId);
    }

    @Override
    public Integer delProjectUserByUserIdAndAreaId(Integer userId, Integer areaId) {
        return userDao.delProjectUserByUserIdAndAreaId(userId,areaId);
    }

    @Override
    public List<AreaEntity> searchAssociatedProjectAreaList(Integer userId) {
        return userDao.searchAssociatedProjectAreaList(userId);
    }

    @Override
    public IPage<UserEntity> searchAssociatedOtherAreaUserList(QueryUserListEntityParam entityParam) {
        Page<UserEntity> page = new Page<>(entityParam.getCurrentPage(), entityParam.getPageSize());
        return userDao.searchAssociatedOtherAreaUserList(page,entityParam);
    }

    @Override
    public void insertUserComeDate(Integer userId, Date comeDate) {
        userDao.insertUserComeDate(userId,comeDate);
    }

    @Override
    public void updateUserLeaveDate(Integer userId, Date leaveDate) {
        userDao.updateUserLeaveDate(userId,leaveDate);
    }

    @Override
    public List<UserComeLeaveEntity> searchUserComeLeaveList(List<Integer> limitUserIds) {
        return userDao.searchUserComeLeaveList(limitUserIds);
    }


}
