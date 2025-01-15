package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.UserComeLeaveEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
    List<UserEntity> queryUserByRoleCode(@Param("code") String code);
    UserEntity getUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password, @Param("areaId") Integer areaId);
    UserEntity loadUserByUsername(@Param("username") String username,@Param("areaId") Integer areaId);
    void loginTokenUpdate(@Param("username")String username,@Param("token")String token);
    void logout(@Param("username")String username);
    Integer checkTokenExist(@Param("token") String token);

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
     * 根据用户名查询用户
     * @param userName
     * @return
     */
    UserEntity queryUserByUserName(@Param("userName")String userName);

    /**
     * 分页检索用户
     */
    IPage<UserEntity> queryUserPagingTest(Page<UserEntity> page, @Param("searchVal") String searchVal);

    /**
     * 通过角色分页检索用户
     */
    IPage<UserEntity> queryUserPagingByRoleIds(Page<UserEntity> page, @Param("searchVal") String searchVal,@Param("ids") List<Integer> ids);

    /**
     * 查询单个用户详细信息
     */
    UserEntity selectUserInfoById(@Param("userId")Long userId);

    /**
     * 根据调休和关联项目查询用户
     */
    UserEntity selectUserByProjectOrReport(@Param("userId")Long userId);

    List<UserEntity> searchUserListByIds(@Param("userIdList")List<Integer> userIdList);

    /**
     * 分页查询用户
     * @param page
     * @return
     */
    IPage<UserEntity> queryUserPaging(Page<UserEntity> page, @Param("entityParam") QueryUserListEntityParam entityParam);

    /**
     * 根据区域ID和角色CODE查找用户
     * @param entityParam
     * @return
     */
    List<UserEntity> searchUserByRoleOrArea(@Param("entityParam")SearchUserByRoleOrAreaEntityParam entityParam);

    List<UserEntity> searchUserByAss(@Param("areaId") Integer areaId,@Param("roleCode")List<String> roleCode);

    /**
     * 根据用户本身角色查询项目关联成员下拉列表
     * @param areaId
     * @param userId
     * @return
     */
    List<UserEntity> searchUserByProjectAndArea(@Param("areaId")Integer areaId,@Param("userId")Integer userId);

    /**
     * 根据区域ID和时间分组查询当天没有写日志的用户信息
     */
    List<UserEntity> queryGroupAllUserNotWriteDailyByTime(@Param("areaId")Integer areaId,@Param("time")String time);

    /**
     * 根据区域ID和时间分及当前项目组查询当天没有写日志的用户信息
     */
    List<UserEntity> queryGroupAllUserNotWriteDailyByTimeAndProject(@Param("userId")Integer userId,@Param("areaId")Integer areaId,@Param("time")String time);

    /**
     * 根据项目ID和区域ID查询所有用户
     */
    List<UserEntity> queryUserByProjectIdAndAreaId(@Param("startTime")String startTime,@Param("endTime")String endTime,
                                                   @Param("projectId")Integer projectId, @Param("areaId")Integer areaId);

    /**
     * 根据项目ID和区域ID查询所有人
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<UserEntity> queryAllUserByProjectIdOrAreaId(@Param("startTime") String startTime, @Param("endTime")String endTime,
                                                     @Param("areaId")Integer[] areaId,@Param("projectId")Integer[] projectId);

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
    List<UserEntity> queryUserProjectAndDistinct(@Param("startTime") String startTime, @Param("endTime")String endTime,
                                                 @Param("areaId")Integer[] areaId,@Param("projectId")Integer[] projectId);

    /**
     * 查询每人每天的详细工时
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectName
     * @return
     */
    List<UserEntity> queryUserWorkTime(@Param("startTime") String startTime, @Param("endTime")String endTime,
                                       @Param("areaId")Integer[] areaId,@Param("projectName")String projectName);

    /**
     * 去重查询这段时间所有的人
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectName
     * @return
     */
    List<UserEntity> queryUserDistinctByTime(@Param("startTime") String startTime, @Param("endTime")String endTime,
                                             @Param("areaId")Integer[] areaId,@Param("projectName")String projectName);

    /**
     * 查询每个人总工时
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<ResEveryUserTotalTimeEntityParam> queryEveryUserTotalTime(@Param("startTime") String startTime, @Param("endTime")String endTime,
                                                                   @Param("areaId")Integer[] areaId,@Param("projectId")Integer[] projectId);


    List<UserEntity> searchUserWhoCanBeAssignToProjectByAreaId(@Param("areaId")Integer areaId);

    /**
     * 每个人的休假时长
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<ResEveryUserHolidayEntityParam> queryEveryUserHoliday(@Param("startTime") String startTime, @Param("endTime")String endTime,
                                                                @Param("areaId")Integer[] areaId,@Param("projectId")Integer[] projectId);

    Integer queryProjectCountByUserId(@Param("userId") Integer userId);

    Integer queryReportCountByUserId(@Param("userId") Integer userId);

    List<UserEntity> getAllOtherAreaUser(@Param("areaId")Integer areaId);

    List<AreaUsersEntityParam> searchAreaUserCountList();

    Integer delProjectUserByUserId(@Param("userId") Integer userId);

    Integer delProjectUserByUserIdAndAreaId(@Param("userId") Integer userId,@Param("areaId")Integer areaId);

    List<AreaEntity> searchAssociatedProjectAreaList(@Param("userId")Integer userId);

    IPage<UserEntity> searchAssociatedOtherAreaUserList(Page<UserEntity> page, @Param("entityParam") QueryUserListEntityParam entityParam);

    void insertUserComeDate(@Param("userId")Integer userId, @Param("comeDate")Date comeDate);
    void updateUserLeaveDate(@Param("userId")Integer userId, @Param("leaveDate")Date leaveDate);
    List<UserComeLeaveEntity> searchUserComeLeaveList(@Param("limitUserIds") List<Integer> limitUserIds);

}
