package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;
import com.nl.pm.server.registry.entity.ProjectEntity;
import com.nl.pm.server.registry.entity.ProjectUserEntity;
import com.nl.pm.server.registry.param.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProjectDao extends BaseMapper<ProjectEntity> {
    void updateProjectByProjectId(@Param("entity") ProjectEntity projectEntity);
    Double queryTotalTime(@Param("areaId") Integer[] areaId,@Param("projectName") String projectName);
    List<DownProjectReportFormEntityParam> queryProjectAreaUsers(@Param("entityList") List<ProjectAreaEntityParam> projectAreaEntityParams);
    void delRelevanceAreaAndProject(@Param("userId") Integer userId);
    Integer queryCountManager(@Param("userId") Integer userId);
    public void createProject(@Param("entity") ProjectEntity entity);
    public IPage<ProjectAdvanceEntity> searchProject(@Param("page")Page<ProjectSearchEntityParam> page, @Param("param")ProjectSearchEntityParam param);
    public ProjectAdvanceEntity fetchProject(@Param("id")Integer id);
    public List<ProjectUserEntity> getAllProjectUserById(@Param("id")Integer id);
    public Integer checkProjectNameExisted(@Param("areaId")Integer areaId,@Param("name")String name);
    public void updateProject(@Param("entity")ProjectEntity entity);
    public void closeAllRelativeProjectBySystemProjectId(@Param("systemProjectId")Integer systemProjectId);
    public ProjectEntity fetchProjectBySystemProjectIdAndAreaId(@Param("systemProjectId")Integer systemProjectId,@Param("areaId") Integer areaId);
    public void openOrClose(@Param("id")Integer id,@Param("enable")boolean enable);
    public void assignEmployees(@Param("projectId")Integer projectId , @Param("userIdList")List<Integer> userIdList);
    public Integer checkDayReportByProjectId(@Param("projectId")Integer projectId);
    public void deleteProject(@Param("projectId")Integer projectId);
    public void updateManagerByNameAndArea(@Param("areaId")Integer areaId,@Param("nameList")List<String> nameList,@Param("newManagerId")Integer newManagerId);
    public void addUserToProject(@Param("projectId")Integer projectId,@Param("userId")Integer userId);
    public List<ProjectEntity> searchProjectByNameAndArea(@Param("areaId")Integer areaId,@Param("nameList")List<String> nameList);
    public List<ProjectUserEntity> getAllProjectUserByAreaId(@Param("areaId")Integer areaId);
    public List<ProjectUserEntity> getAllProjectUserByManagerId(@Param("managerId")Integer managerId);

    /**
     * 项目总工作时长
     * @param startTime
     * @param endTime
     * @param projectId
     * @return
     */
    Double sumWorkTotal(@Param("startTime") String startTime, @Param("endTime")String endTime, @Param("projectId")Integer projectId);

    /**
     * 根据区域和项目查询所有项目
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<ProjectEntity> queryProjectByIdOrAreaId(@Param("startTime") String startTime,@Param("endTime") String endTime,
                                                 @Param("areaId")Integer[] areaId,@Param("projectId")Integer[] projectId);

    /**
     * 去重查询所有项目名
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     */
    List<String> queryProjectAndDistinct(@Param("startTime") String startTime,@Param("endTime") String endTime,
                                         @Param("areaId")Integer[] areaId,@Param("projectId")Integer[] projectId);

    List<String> queryProjectIsNullAndDistinct(@Param("startTime") String startTime,@Param("endTime") String endTime,
                                         @Param("areaId")Integer[] areaId,@Param("projectId")Integer[] projectId);

    /**
     * 查询每个人在每个项目所用工时
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<ResReportFormEntityParam> queryEveryUserTime(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                           @Param("areaId")Integer[] areaId, @Param("projectId")Integer[] projectId);

    IPage<ProjectAdvanceEntity> searchProjectListByAssignUser(@Param("page")Page<ProjectAdvanceEntity> page,@Param("userId") Integer userId,@Param("areaId") Integer areaId);


    List<ProjectAdvanceEntity> searchAllProjectListByUserIdAndAreaId(@Param("userId") Integer userId,@Param("areaId") Integer areaId,@Param("superFlag") boolean superFlag);

    List<ProjectEntity>queryProjectByName(@Param("areaId") Integer[] areaId,@Param("projectName") String[] projectName);

    Integer queryProjectCountBySystemId(@Param("systemProjectId") Integer systemProjectId);

    void initUserAssProject(@Param("userId") Integer userId,@Param("areaId") Integer areaId);

    void addUserWithProject(@Param("userId")Integer userId,@Param("projectId")Integer projectId);

    List<Integer> getProjectIdsByName(@Param("name")String name);

    List<ProjectAdvanceEntity> searchBoardProjectListTop6(@Param("userId")Integer userId, @Param("areaId")Integer areaId, @Param("roleCode")String roleCode,@Param("pjNumber") Integer pjNumber);

    Double searchBoardProjectListAll(@Param("userId")Integer userId,@Param("areaId")Integer areaId,@Param("roleCode")String roleCode);

    Integer searchBoardProjectUserAll(@Param("userId")Integer userId, @Param("areaId")Integer areaId, @Param("roleCode")String roleCode);

    List<ProjectUserCountEntityParam> searchBoardProjectUserTop6(@Param("userId")Integer userId, @Param("areaId")Integer areaId, @Param("roleCode")String roleCode);

    List<ProjectEntity> searchProjectBySystemProjectId(@Param("systemProjectId")Integer systemProjectId);

    List<ProjectEntity> searchAllForceDescProject();

    List<ProjectAdvanceEntity> searchAssociatedProjectAreaList(@Param("mainAreaId")Integer mainAreaId, @Param("managerId")Integer managerId);
    List<ProjectAdvanceEntity> searchAssociatedProjectUserHoursList(@Param("mainAreaId")Integer mainAreaId, @Param("managerId")Integer managerId);


}
