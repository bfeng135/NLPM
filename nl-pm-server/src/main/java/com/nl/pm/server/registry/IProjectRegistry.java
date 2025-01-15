package com.nl.pm.server.registry;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nl.pm.server.common.pages.BasePagesParam;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;
import com.nl.pm.server.registry.entity.ProjectEntity;
import com.nl.pm.server.registry.entity.ProjectUserEntity;
import com.nl.pm.server.registry.param.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface IProjectRegistry {
    void updateProjectByProjectId(ProjectEntity projectEntity);
    List<DownProjectReportFormEntityParam> queryProjectAreaUsers(List<ProjectAreaEntityParam> projectAreaEntityParams);
    void delRelevanceAreaAndProject(Integer userId);
    Integer queryCountManager(Integer userId);

    public void createProject(ProjectEntity entity);

    public IPage<ProjectAdvanceEntity> searchProject(ProjectSearchEntityParam page) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    public ProjectAdvanceEntity fetchProject(Integer id);

    public List<ProjectUserEntity> getAllProjectUserById(Integer id);

    public Integer checkProjectNameExisted(Integer areaId,String name);

    public void updateProject(ProjectEntity entity);
    public void closeAllRelativeProjectBySystemProjectId(Integer systemProjectId);
    public ProjectEntity fetchProjectBySystemProjectIdAndAreaId(Integer systemProjectId, Integer areaId);
    public void openOrClose(Integer id,boolean enable);

    public void assignEmployees(Integer projectId , List<Integer> userIdList);

    public Integer checkDayReportByProjectId(Integer projectId);

    public void deleteProject(Integer projectId);

    public void updateManagerByNameAndArea(Integer areaId,Integer newManagerId);

    public List<ProjectUserEntity> getAllProjectUserByAreaId(Integer areaId);
    public List<ProjectUserEntity> getAllProjectUserByManagerId(Integer managerId);

    /**
     * 项目总工作时长
     * @param startTime
     * @param endTime
     * @param projectId
     * @return
     */
    Double sumWorkTotal(String startTime, String endTime, Integer projectId);

    /**
     * 根据区域和项目查询所有项目
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<ProjectEntity> queryProjectByIdOrAreaId(String startTime, String endTime,Integer[] areaId,Integer[] projectId);

    /**
     * 去重查询所有项目名
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     */
    List<String> queryProjectAndDistinct(String startTime, String endTime,Integer[] areaId,Integer[] projectId);

    List<String> queryProjectIsNullAndDistinct(String startTime, String endTime,Integer[] areaId,Integer[] projectId);

    /**
     * 查询每个人在每个项目所用工时
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectId
     * @return
     */
    List<ResReportFormEntityParam> queryEveryUserTime(String startTime, String endTime, Integer[] areaId, Integer[] projectId);

    List<ProjectEntity> queryProjectByName(Integer[] areaId,String[] projectName);

    IPage<ProjectAdvanceEntity> searchProjectListByAssignUser(BasePagesParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    void editProject(ProjectEntity entity);

    Integer queryProjectCountBySystemId(Integer systemProjectId);

    List<ProjectAdvanceEntity> searchAllProjectListByUserIdAndAreaId(Integer userId, Integer areaId, boolean superFlag);

    void addUserWithProject(Integer userId,Integer projectId);

    List<Integer> getProjectIdsByName(String name);

    List<ProjectUserCountEntityParam> searchBoardProjectUser() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<ProjectAdvanceEntity> searchBoardProjectList(Integer pjNumber) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<ProjectEntity> searchProjectBySystemProjectId(Integer systemProjectId);

    List<ProjectEntity> searchAllForceDescProject();

    List<ProjectAdvanceEntity> searchAssociatedProjectAreaList(Integer mainAreaId, Integer managerId);

    List<ProjectAdvanceEntity> searchAssociatedProjectUserHoursList(Integer mainAreaId, Integer managerId);

}
