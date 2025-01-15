package com.nl.pm.server.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.UserUtils;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.IUserRegistry;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.UserComeLeaveEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.AreaUsersEntityParam;
import com.nl.pm.server.registry.param.QueryUserListEntityParam;
import com.nl.pm.server.registry.param.SearchUserByRoleOrAreaEntityParam;
import com.nl.pm.server.security.tools.TokenTools;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.AreaModel;
import com.nl.pm.server.service.model.UserComeLeaveModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements IUserService, UserDetailsService {
    @Autowired
    private IUserRegistry iUserRegistry;
    @Autowired
    private TokenTools tokenTools;
    @Autowired
    private SecurityContextUtils securityContextUtils;


    @Override
    public UserModel getUserByUsernameAndPasswordAndAreaId(String username, String password, Integer areaId) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseServiceException {
        UserEntity entity = iUserRegistry.getUserByUsernameAndPassword(username, password, areaId);
        UserModel model = (UserModel) EntityUtils.fillModelWithEntity(entity);
        return model;
    }

    @Override
    public void loginTokenUpdate(String username, String token) {
        iUserRegistry.loginTokenUpdate(username,token);
    }

    @Override
    public void logout(String username) {
        iUserRegistry.logout(username);
    }

    @Override
    public UserModel saveUser(UserModel userModel) throws Exception {
        userModel.setCreateTime(new Date());
        UserEntity entity = (UserEntity) EntityUtils.convertModelToEntity(userModel);
        UserEntity userEntity = iUserRegistry.saveUser(entity);
        return (UserModel)EntityUtils.fillModelWithEntity(userEntity);
    }

    @Override
    public UserModel queryUserByUserName(String userName) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        UserEntity entity = iUserRegistry.queryUserByUserName(userName);
        UserModel model = (UserModel) EntityUtils.fillModelWithEntity(entity);
        return model;
    }

    @Override
    public BasePagesDomain<UserModel> queryUserPaging(int pageNo, int pageSize, String searchVal) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Page<UserEntity> page = new Page<>(pageNo, pageSize);
        IPage<UserEntity> userEntityIPage = iUserRegistry.queryUserPaging(page, searchVal);
        List<UserModel> resUserInfoVOList = new LinkedList<>();
        if (userEntityIPage != null && CollectionUtils.isNotEmpty(userEntityIPage.getRecords())) {
            for (UserEntity userEntity : userEntityIPage.getRecords()) {
                resUserInfoVOList.add((UserModel) EntityUtils.fillModelWithEntity(userEntity));
            }
        }
        BasePagesDomain<UserModel> pageInfo = new BasePagesDomain(userEntityIPage);
        pageInfo.setTotalList(resUserInfoVOList);
        return pageInfo;
    }

    @Override
    public BasePagesDomain<UserModel> queryUserPagingByRoleIds(int pageNo, int pageSize, String searchVal, List<Integer> ids) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Page<UserEntity> page = new Page<>(pageNo, pageSize);
        IPage<UserEntity> userEntityIPage = iUserRegistry.queryUserPagingByRoleIds(page, searchVal,ids);
        List<UserModel> resUserInfoVOList = new LinkedList<>();
        if (userEntityIPage != null && CollectionUtils.isNotEmpty(userEntityIPage.getRecords())) {
            for (UserEntity userEntity : userEntityIPage.getRecords()) {
                resUserInfoVOList.add((UserModel) EntityUtils.fillModelWithEntity(userEntity));
            }
        }
        BasePagesDomain<UserModel> pageInfo = new BasePagesDomain(userEntityIPage);
        pageInfo.setTotalList(resUserInfoVOList);
        return pageInfo;
    }

    @Override
    public void editUserStatus(Long userId) throws BaseServiceException {
        iUserRegistry.editUserStatus(userId);
    }

    @Override
    public UserModel queryUserInfoById(Long userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        UserEntity userEntity = iUserRegistry.queryUserInfoById(userId);
        UserModel model = (UserModel) EntityUtils.fillModelWithEntity(userEntity);
        return model;
    }

    @Override
    public void delUserByUserId(Long userId) throws BaseServiceException {
        iUserRegistry.delUserByUserId(userId);
    }

    @Override
    public BasePagesDomain<UserModel> queryUserPaging(QueryUserListModelParam modelParam) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        QueryUserListEntityParam entityParam = new QueryUserListEntityParam();
        entityParam.setCurrentPage(modelParam.getCurrentPage());
        entityParam.setPageSize(modelParam.getPageSize());
        entityParam.setSearchVal(modelParam.getSearchVal());
        entityParam.setAreaId(modelParam.getAreaId());
        entityParam.setProjectId(modelParam.getProjectId());
        entityParam.setRoleId(modelParam.getRoleId());
        entityParam.setNickname(modelParam.getNickname());
        IPage<UserEntity> userEntityIPage = iUserRegistry.queryUserPaging(entityParam);
        List<UserModel> resUserInfoVOList = new LinkedList<>();
        if (userEntityIPage != null && CollectionUtils.isNotEmpty(userEntityIPage.getRecords())) {
            for (UserEntity userEntity : userEntityIPage.getRecords()) {
                resUserInfoVOList.add((UserModel) EntityUtils.fillModelWithEntity(userEntity));
            }
        }
        BasePagesDomain<UserModel> pageInfo = new BasePagesDomain(userEntityIPage);
        pageInfo.setTotalList(resUserInfoVOList);
        return pageInfo;
    }

    @Override
    public List<UserModel> searchUserByRoleOrArea(SearchUserByRoleOrAreaModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserModel> list = new ArrayList<>();
        SearchUserByRoleOrAreaEntityParam entityParam = new SearchUserByRoleOrAreaEntityParam();
        entityParam.setAreaId(param.getAreaId());
        entityParam.setListRoleCode(param.getListRoleCode());
        List<UserEntity> entityList = iUserRegistry.searchUserByRoleOrArea(entityParam);
        if(entityList.size() > 0){
            for (UserEntity userEntity : entityList){
                list.add((UserModel) EntityUtils.fillModelWithEntity(userEntity));
            }
        }
        return list;
    }

    @Override
    public List<UserModel> searchUserByAss(Integer areaId, List<String> roleCode) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserModel> list = new ArrayList<>();
        List<UserEntity> userEntityList = iUserRegistry.searchUserByAss(areaId, roleCode);
        if(userEntityList.size() > 0){
            for (UserEntity userEntity : userEntityList){
                list.add((UserModel) EntityUtils.fillModelWithEntity(userEntity));
            }
        }
        return list;
    }

    @Override
    public void updateUserPassword(UpdatePasswordModelParam param) throws Exception {
        //UserEntity userEntity = (UserEntity)EntityUtils.convertModelToEntity(param);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(param.getId());
        userEntity.setPassword(param.getPassword());
        userEntity.setToken(param.getToken());
        iUserRegistry.updateUserPassword(userEntity);
    }

    @Override
    public void initPassword(InitUserPasswordModelParam param) throws Exception {
        //UserEntity userEntity = (UserEntity)EntityUtils.convertModelToEntity(param);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(param.getId());
        userEntity.setPassword(tokenTools.md5Security(UserUtils.INIT_PASSWORD));
        userEntity.setToken(StringUtils.EMPTY);
        iUserRegistry.initPassword(userEntity);
    }

    @Override
    public List<UserModel> searchUserListByIds(List<Integer> userIdList) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserEntity> userEntities = iUserRegistry.searchUserListByIds(userIdList);
        List<UserModel> modelList = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            UserModel model = (UserModel) EntityUtils.fillModelWithEntity(userEntity);
            modelList.add(model);
        }
        return modelList;
    }

    @Override
    public List<UserModel> searchUserByProjectAndArea(Integer areaId, Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserEntity> userEntities = iUserRegistry.searchUserByProjectAndArea(areaId, userId);
        List<UserModel> list = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            UserModel model =(UserModel) EntityUtils.fillModelWithEntity(userEntity);
            list.add(model);
        }

        return list;
    }

    @Override
    public void editEmailNotice(Integer userId) {
        iUserRegistry.editEmailNotice(userId);
    }

    @Override
    public void editUserInfo(UserModel userModel) throws Exception {
        UserEntity userEntity = (UserEntity)EntityUtils.convertModelToEntity(userModel);
        iUserRegistry.editUserInfo(userEntity);
    }

    @Override
    public List<UserModel> queryUserByProjectIdAndAreaId(String startTime,String endTime,Integer projectId, Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserModel> list = new ArrayList<>();
        List<UserEntity> userEntityList = iUserRegistry.queryUserByProjectIdAndAreaId(startTime,endTime,projectId, areaId);
        if(userEntityList.size() > 0){
            for(UserEntity entity:userEntityList)
            list.add((UserModel)EntityUtils.fillModelWithEntity(entity));
        }
        return list;
    }

    @Override
    public List<UserModel> searchUserWhoCanBeAssignToProjectByAreaId(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserModel> modelList = new ArrayList<>();
        List<UserEntity> userEntities = iUserRegistry.searchUserWhoCanBeAssignToProjectByAreaId(areaId);
        if(CollectionUtils.isNotEmpty(userEntities)){
            for (UserEntity userEntity : userEntities) {
                UserModel userModel = (UserModel)EntityUtils.fillModelWithEntity(userEntity);
                modelList.add(userModel);
            }
        }
        return modelList;
    }

    @Override
    public List<UserModel> getAllUsers() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserModel> list = new ArrayList<>();
        List<UserEntity> userEntities = iUserRegistry.queryAllUser();
        if(CollectionUtils.isNotEmpty(userEntities)){
            for (UserEntity userEntity : userEntities) {
                UserModel model = (UserModel)EntityUtils.fillModelWithEntity(userEntity);
                list.add(model);
            }
        }
        return list;
    }

    @Override
    public List<UserModel> getAllEnableUsers() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserModel> list = new ArrayList<>();
        List<UserEntity> userEntities = iUserRegistry.queryAllEnableUser();
        if(CollectionUtils.isNotEmpty(userEntities)){
            for (UserEntity userEntity : userEntities) {
                UserModel model = (UserModel)EntityUtils.fillModelWithEntity(userEntity);
                list.add(model);
            }
        }
        return list;
    }

    @Override
    public Integer queryProjectCountByUserId(Integer userId) {
        return iUserRegistry.queryProjectCountByUserId(userId);
    }

    @Override
    public Integer queryReportCountByUserId(Integer userId) {
        return iUserRegistry.queryReportCountByUserId(userId);
    }

    @Override
    public List<UserModel> getAllOtherAreaUser(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserEntity> allOtherAreaUser = iUserRegistry.getAllOtherAreaUser(areaId);
        List<UserModel> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(allOtherAreaUser)){
            for (UserEntity userEntity : allOtherAreaUser) {
                UserModel model = (UserModel)EntityUtils.fillModelWithEntity(userEntity);
                list.add(model);
            }
        }
        return list;
    }

    @Override
    public List<AreaUsersModelParam> searchAreaUserCountList() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<AreaUsersEntityParam> areaUsersEntityParams = iUserRegistry.searchAreaUserCountList();
        List<AreaUsersModelParam> modelList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(areaUsersEntityParams)){
            for (AreaUsersEntityParam areaUsersEntityParam : areaUsersEntityParams) {
                AreaUsersModelParam model = new AreaUsersModelParam();
                model.setAreaName(areaUsersEntityParam.getAreaName());
                model.setAreaId(areaUsersEntityParam.getAreaId());
                model.setValue(areaUsersEntityParam.getValue());
                modelList.add(model);
            }
        }
        return  modelList;
    }

    @Override
    public Integer delProjectUserByUserId(Integer userId) {
        return iUserRegistry.delProjectUserByUserId(userId);
    }

    @Override
    public Integer delProjectUserByUserIdAndAreaId(Integer userId, Integer areaId) {

        iUserRegistry.delProjectUserByUserIdAndAreaId(userId, areaId);
        return null;
    }

    @Override
    public List<AreaModel> searchAssociatedProjectAreaList(Integer userId) throws IllegalAccessException, InvocationTargetException, InstantiationException, BaseServiceException, NoSuchMethodException, ClassNotFoundException {
        List<AreaEntity> areaEntities = iUserRegistry.searchAssociatedProjectAreaList(userId);
        List<AreaModel> areaModels = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(areaEntities)){
            for (AreaEntity areaEntity : areaEntities) {
                AreaModel model =(AreaModel) EntityUtils.fillModelWithEntity(areaEntity);
                areaModels.add(model);
            }
        }
        return areaModels;
    }

    @Override
    public BasePagesDomain<UserModel> searchAssociatedOtherAreaUserList(int pageNo, int pageSize, Integer currentUserAreaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        QueryUserListEntityParam entityParam = new QueryUserListEntityParam();
        entityParam.setCurrentPage(pageNo);
        entityParam.setPageSize(pageSize);
        entityParam.setAreaId(currentUserAreaId);
        IPage<UserEntity> userEntityIPage = iUserRegistry.searchAssociatedOtherAreaUserList(entityParam);
        List<UserModel> resUserInfoVOList = new LinkedList<>();
        if (userEntityIPage != null && CollectionUtils.isNotEmpty(userEntityIPage.getRecords())) {
            for (UserEntity userEntity : userEntityIPage.getRecords()) {
                resUserInfoVOList.add((UserModel) EntityUtils.fillModelWithEntity(userEntity));
            }
        }
        BasePagesDomain<UserModel> pageInfo = new BasePagesDomain(userEntityIPage);
        pageInfo.setTotalList(resUserInfoVOList);
        return pageInfo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = iUserRegistry.loadUserByUsername(username);
        return userDetails;
    }


    @Override
    public void insertUserComeDate(Integer userId, Date comeDate) {
        iUserRegistry.insertUserComeDate(userId,comeDate);
    }

    @Override
    public void updateUserLeaveDate(Integer userId, Date leaveDate) {
        iUserRegistry.updateUserLeaveDate(userId,leaveDate);
    }

    @Override
    public List<UserComeLeaveModel> searchUserComeLeaveList(List<Integer> limitUserIds) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserComeLeaveEntity> userComeLeaveEntities = iUserRegistry.searchUserComeLeaveList(limitUserIds);
        List<UserComeLeaveModel> userComeLeaveModels = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(userComeLeaveEntities)){
            for (UserComeLeaveEntity userComeLeaveEntity : userComeLeaveEntities) {
                UserComeLeaveModel model = (UserComeLeaveModel)EntityUtils.fillModelWithEntity(userComeLeaveEntity);
                userComeLeaveModels.add(model);
            }
        }
        return userComeLeaveModels;
    }

}
