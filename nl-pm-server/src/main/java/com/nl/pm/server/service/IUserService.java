package com.nl.pm.server.service;


import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.AreaModel;
import com.nl.pm.server.service.model.UserComeLeaveModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

public interface IUserService {
    UserModel getUserByUsernameAndPasswordAndAreaId(String username, String password, Integer areaId) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseServiceException;
    void loginTokenUpdate(String username,String token);
    void logout(String username);

    /**
     * 添加员工
     */
    UserModel saveUser(UserModel userModel) throws Exception;

    /**
     * 通过用户名检索用户
     * @param userName
     * @return
     */
    UserModel queryUserByUserName(String userName) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 分页检索用户
     */
    BasePagesDomain<UserModel> queryUserPaging(int pageNo, int pageSize, String searchVal) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 分页检索通过角色
     */
    BasePagesDomain<UserModel> queryUserPagingByRoleIds(int pageNo, int pageSize, String searchVal, List<Integer> ids) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 修改员工状态
     */
    void editUserStatus(Long userId) throws BaseServiceException;

    /**
     * 查询单个员工详细信息
     */
    UserModel queryUserInfoById(Long userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 根据员工ID删除用户
     * @param userId
     */
    void delUserByUserId(Long userId) throws BaseServiceException;

    /**
     * 分页检索用户
     */
    BasePagesDomain<UserModel> queryUserPaging(QueryUserListModelParam modelParam) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 根据区域ID和角色CODE查找用户
     * @param param
     * @return
     */
    List<UserModel> searchUserByRoleOrArea(SearchUserByRoleOrAreaModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<UserModel> searchUserByAss(Integer areaId,List<String> roleCode) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    /**
     * 更新用户密码
     */
    void updateUserPassword(UpdatePasswordModelParam param) throws Exception;

    /**
     * 初始化密码
     */
    void initPassword(InitUserPasswordModelParam param) throws Exception;

    List<UserModel> searchUserListByIds(List<Integer> userIdList) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<UserModel> searchUserByProjectAndArea(Integer areaId, Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    /**
     * 修改邮件通知
     */
    void editEmailNotice(Integer userId);

    /**
     * 修改用户信息
     */
    void editUserInfo(UserModel userModel) throws Exception;

    /**
     * 根据项目ID和区域ID查询所有用户
     */
    List<UserModel> queryUserByProjectIdAndAreaId(String startTime,String endTime,Integer projectId,Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<UserModel> searchUserWhoCanBeAssignToProjectByAreaId(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<UserModel> getAllUsers() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<UserModel> getAllEnableUsers() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    Integer queryProjectCountByUserId(Integer userId);

    Integer queryReportCountByUserId(Integer userId);

    List<UserModel> getAllOtherAreaUser(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;


    List<AreaUsersModelParam> searchAreaUserCountList() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    Integer delProjectUserByUserId(Integer userId);

    Integer delProjectUserByUserIdAndAreaId(Integer userId,Integer areaId);

    List<AreaModel> searchAssociatedProjectAreaList(Integer userId) throws IllegalAccessException, InvocationTargetException, InstantiationException, BaseServiceException, NoSuchMethodException, ClassNotFoundException;

    BasePagesDomain<UserModel> searchAssociatedOtherAreaUserList(int pageNo, int pageSize, Integer currentUserAreaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    void insertUserComeDate(Integer userId,  Date comeDate);
    void updateUserLeaveDate(Integer userId, Date leaveDate);
    List<UserComeLeaveModel> searchUserComeLeaveList(List<Integer> limitUserIds) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;


}
