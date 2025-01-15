package com.nl.pm.server.registry;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.UserComeLeaveEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.*;

import java.util.Date;
import java.util.List;

public interface IUserRegistry {
    Double queryTotalTime(Integer[] areaId,String projectName);
    List<UserEntity> queryUserByRoleCode(String code);
    UserEntity getUserByUsernameAndPassword(String username, String password, Integer areaId);
    UserEntity loadUserByUsername(String username);
    void loginTokenUpdate(String username,String token);
    void logout(String username);
    Integer checkTokenExist(String token);

    /**
     * 查询所有当天没写日志的用户
     * @return
     */
    List<UserEntity> queryAllUserNotWriteDaily(String date);

    /**
     * 查询所有用户
     * @return
     */
    List<UserEntity> queryAllUser();

    /**
     * 查询所有在职用户
     * @return
     */
    List<UserEntity> queryAllEnableUser();

    /**
     * 添加员工
     */
    UserEntity saveUser(UserEntity entity);

    /**
     * 通过用户名检索用户
     * @param userName
     * @return
     */
    UserEntity queryUserByUserName(String userName);

    /**
     * 分页检索用户
     */
    IPage<UserEntity> queryUserPaging(Page<UserEntity> page,String searchVal);

    /**
     * 通过角色分页检索用户
     */
    IPage<UserEntity> queryUserPagingByRoleIds(Page<UserEntity> page, String searchVal, List<Integer> ids);

    /**
     * 修改员工状态
     */
    void editUserStatus(Long userId) throws BaseServiceException;

    /**
     * 查询单个员工详细信息
     */
    UserEntity queryUserInfoById(Long userId);

    /**
     * 根据用户ID删除用户
     * @param userId
     */
    void delUserByUserId(Long userId) throws BaseServiceException;

    /**
     * 分页查询用户
     * @param entityParam
     * @return
     */
    IPage<UserEntity> queryUserPaging(QueryUserListEntityParam entityParam);

    /**
     * 根据区域ID和角色CODE查找用户
     * @param entityParam
     * @return
     */
    List<UserEntity> searchUserByRoleOrArea(SearchUserByRoleOrAreaEntityParam entityParam);

    List<UserEntity> searchUserByAss(Integer areaId,List<String> roleCode);

    /**
     * 更新密码
     */
    void updateUserPassword(UserEntity userEntity);

    List<UserEntity> searchUserListByIds(List<Integer> userIdList);

    /**
     * 修改邮件通知
     */
    void editEmailNotice(Integer userId);

    /**
     * 初始化密码
     */
    void initPassword(UserEntity userEntity);

    /**
     * 修改用户信息
     */
    void editUserInfo(UserEntity userEntity);

    /**
     * 根据用户本身角色查询项目关联成员下拉列表
     * @param areaId
     * @param userId
     * @return
     */
    List<UserEntity> searchUserByProjectAndArea(Integer areaId, Integer userId);

    /**
     * 根据区域Id查询所有区长用户
     * @return
     */
    List<UserEntity> queryAllUserIsAreaManager(Integer roleId);

    /**
     * 根据区域ID和时间分组查询当天没有写日志的用户信息
     */
    List<UserEntity> queryGroupAllUserNotWriteDailyByTime(Integer areaId,String time);

    /**
     * 根据区域ID和时间分及当前项目组查询当天没有写日志的用户信息
     */
    List<UserEntity> queryGroupAllUserNotWriteDailyByTimeAndProject(Integer userId,Integer areaId,String time);

    /**
     * 根据项目ID和区域ID查询所有用户
     */
    List<UserEntity> queryUserByProjectIdAndAreaId(String startTime,String endTime,Integer projectId, Integer areaId);

    /**
     * 根据项目ID和区域ID查询所有人
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<UserEntity> queryAllUserByProjectIdOrAreaId(String startTime, String endTime,Integer[] areaId,Integer[] projectId);

    /**
     * 去重查询项目负责人ID和email
     * @return
     */
    List<UserDistinctEntityParam> queryUserDistinct();

    /**
     * 去重查组员
     * @param userId
     * @return
     */
    List<UserDistinctEntityParam> queryUserDistinctByGroupId(Integer userId);

    /**
     * 根据时间和用户Id查询用户是否填写日报
     * @param time
     * @param userId
     * @return
     */
    List<UserEntity> queryAllUserNotWriteDailyByTimeAndUserId(String time,Integer userId);

    /**
     * 去重查询所有项目负责人用户
     * @return
     */
    List<UserEntity> queryUserIsProjectManager();

    /**
     * 去重查询在项目上的所有的人
     * @return
     */
    List<UserEntity> queryUserProjectAndDistinct(String startTime, String endTime,Integer[] areaId,Integer[] projectId);

    /**
     * 查询每人每天的详细工时
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectName
     * @return
     */
    List<UserEntity> queryUserWorkTime(String startTime, String endTime,Integer[] areaId,String projectName);

    /**
     * 去重查询这段时间所有的人
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectName
     * @return
     */
    List<UserEntity> queryUserDistinctByTime(String startTime, String endTime,Integer[] areaId,String projectName);

    /**
     * 查询每个人总工时
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<ResEveryUserTotalTimeEntityParam> queryEveryUserTotalTime(String startTime, String endTime, Integer[] areaId, Integer[] projectId);

    List<UserEntity> searchUserWhoCanBeAssignToProjectByAreaId(Integer areaId);

    /**
     * 每个人的休假时长
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<ResEveryUserHolidayEntityParam> queryEveryUserHoliday(String startTime, String endTime, Integer[] areaId, Integer[] projectId);

    Integer queryProjectCountByUserId(Integer userId);

    Integer queryReportCountByUserId(Integer userId);

    List<UserEntity> getAllOtherAreaUser(Integer areaId);

    List<AreaUsersEntityParam> searchAreaUserCountList();

    Integer delProjectUserByUserId(Integer userId);

    Integer delProjectUserByUserIdAndAreaId(Integer userId,Integer areaId);

    List<AreaEntity> searchAssociatedProjectAreaList(Integer userId);

    IPage<UserEntity> searchAssociatedOtherAreaUserList(QueryUserListEntityParam entityParam);

    void insertUserComeDate(Integer userId,  Date comeDate);
    void updateUserLeaveDate(Integer userId, Date leaveDate);
    List<UserComeLeaveEntity> searchUserComeLeaveList(List<Integer> limitUserIds);

}
