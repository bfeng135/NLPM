package com.nl.pm.server.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.common.pages.BasePagesParam;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.ProjectAdvanceModel;
import com.nl.pm.server.service.model.ProjectModel;
import com.nl.pm.server.service.model.ProjectUserModel;
import com.nl.pm.server.service.param.DownProjectReportFormModelParam;
import com.nl.pm.server.service.param.ProjectAreaModelParam;
import com.nl.pm.server.service.param.ProjectSearchModelParam;
import com.nl.pm.server.service.param.ProjectUserCountModelParam;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface IProjectService {
    List<DownProjectReportFormModelParam> queryProjectAreaUsers(List<ProjectAreaModelParam> params) throws Exception;
    public void createProject(ProjectModel model) throws Exception;
    public BasePagesDomain<ProjectAdvanceModel> searchProject(ProjectSearchModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    public ProjectAdvanceModel fetchProject(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    public List<ProjectUserModel> getAllProjectUserById(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    public Integer checkProjectNameExisted(Integer areaId,String name);
    public void updateProject(ProjectModel model) throws Exception;
    public void closeAllRelativeProjectBySystemProjectId(Integer systemProjectId);
    public ProjectModel fetchProjectBySystemProjectIdAndAreaId(Integer systemProjectId,Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    public void openOrClose(Integer id,boolean enable);
    public void assignEmployees(Integer projectId , List<Integer> userIdList) throws Exception;
    public Integer checkDayReportByProjectId(Integer projectId);
    public void deleteProject(Integer projectId);
    public List<ProjectUserModel> getAllProjectUserByAreaId(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    public List<ProjectUserModel> getAllProjectUserByManagerId(Integer manager) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 项目总工作时长
     * @param startTime
     * @param endTime
     * @param projectId
     * @return
     */
    Double sumWorkTotal(String startTime, String endTime, Integer projectId);

    IPage<ProjectAdvanceModel> searchProjectListByAssignUser(BasePagesParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    void editProject(ProjectModel model) throws Exception;

    Integer queryProjectCountBySystemId(Integer systemProjectId);

    List<ProjectAdvanceModel> searchAllProjectListByUserIdAndAreaId(Integer userId, Integer areaId, boolean superFlag) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;


    void updateManagerByNameAndArea(Integer areaId,Integer managerId);

    void addUserWithProject(Integer userId,Integer projectId);

    List<Integer> getProjectIdsByName(String name);

    List<ProjectUserCountModelParam> searchBoardProjectUser() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<ProjectAdvanceModel> searchBoardProjectList(Integer pjNumber) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<ProjectModel> searchProjectBySystemProjectId(Integer systemProjectId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<ProjectModel> searchAllForceDescProject() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;


    List<ProjectAdvanceModel> searchAssociatedProjectAreaList(Integer mainAreaId,Integer managerId) throws IllegalAccessException, InvocationTargetException, InstantiationException, BaseServiceException, NoSuchMethodException, ClassNotFoundException;

    List<ProjectAdvanceModel> searchAssociatedProjectUserHoursList(Integer mainAreaId,Integer managerId) throws IllegalAccessException, InvocationTargetException, InstantiationException, BaseServiceException, NoSuchMethodException, ClassNotFoundException;


}
