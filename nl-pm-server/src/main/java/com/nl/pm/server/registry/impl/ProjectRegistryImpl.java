package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.ProjectUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.common.pages.BasePagesParam;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.IProjectRegistry;
import com.nl.pm.server.registry.dao.ProjectDao;
import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;
import com.nl.pm.server.registry.entity.ProjectEntity;
import com.nl.pm.server.registry.entity.ProjectUserEntity;
import com.nl.pm.server.registry.param.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ProjectRegistryImpl implements IProjectRegistry {

    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Override
    public void updateProjectByProjectId(ProjectEntity projectEntity) {
        projectDao.updateProjectByProjectId(projectEntity);
    }

    @Override
    public List<DownProjectReportFormEntityParam> queryProjectAreaUsers(List<ProjectAreaEntityParam> projectAreaEntityParams) {
        return projectDao.queryProjectAreaUsers(projectAreaEntityParams);
    }

    @Override
    public void delRelevanceAreaAndProject(Integer userId) {
        projectDao.delRelevanceAreaAndProject(userId);
    }

    @Override
    public Integer queryCountManager(Integer userId) {
        return projectDao.queryCountManager(userId);
    }

    @Override
    @Transactional
    public void createProject(ProjectEntity entity) {
        projectDao.createProject(entity);
        if(entity.getManagerId()!=null) {
            List<Integer> userList = new ArrayList<>();
            userList.add(entity.getManagerId());
            projectDao.assignEmployees(entity.getId(), userList);
        }
    }

    @Override
    public IPage<ProjectAdvanceEntity> searchProject(ProjectSearchEntityParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Page<ProjectSearchEntityParam> page = new Page<>();
        page.setCurrent(param.getCurrentPage());
        page.setSize(param.getPageSize());
        param.setCurrentUserId(securityContextUtils.getCurrentUserId());
        param.setCurrentAreaId(securityContextUtils.getCurrentUserAreaId());
        IPage<ProjectAdvanceEntity> projectEntityIPage = projectDao.searchProject(page,param);
        return projectEntityIPage;
    }

    @Override
    public ProjectAdvanceEntity fetchProject(Integer id) {
        ProjectAdvanceEntity entity = projectDao.fetchProject(id);
        return entity;
    }

    @Override
    public List<ProjectUserEntity> getAllProjectUserById(Integer id) {
        List<ProjectUserEntity> list = projectDao.getAllProjectUserById(id);
        return list;
    }

    @Override
    public Integer checkProjectNameExisted(Integer areaId, String name) {
        Integer count = projectDao.checkProjectNameExisted(areaId, name);
        return count;
    }

    @Override
    public void updateProject(ProjectEntity entity) {
        projectDao.updateProject(entity);
    }

    @Override
    public void closeAllRelativeProjectBySystemProjectId(Integer systemProjectId) {
        projectDao.closeAllRelativeProjectBySystemProjectId(systemProjectId);
    }

    @Override
    public ProjectEntity fetchProjectBySystemProjectIdAndAreaId(Integer systemProjectId, Integer areaId) {
        return projectDao.fetchProjectBySystemProjectIdAndAreaId(systemProjectId,areaId);
    }

    @Override
    public void openOrClose(Integer id, boolean enable) {
        projectDao.openOrClose(id,enable);
    }

    @Override
    public void assignEmployees(Integer projectId , List<Integer> userIdList) {
        projectDao.assignEmployees(projectId,userIdList);
    }

    @Override
    public Integer checkDayReportByProjectId(Integer projectId) {
        return projectDao.checkDayReportByProjectId(projectId);
    }

    @Override
    public void deleteProject(Integer projectId) {
        projectDao.deleteProject(projectId);
    }

    @Override
    @Transactional
    public void updateManagerByNameAndArea(Integer areaId, Integer newManagerId) {
        List<String> nameList = new ArrayList<>();
        nameList.add(ProjectUtils.PROJECT_MEETING);
        nameList.add(ProjectUtils.PROJECT_TRAINING);
        nameList.add(ProjectUtils.PROJECT_EXHIBITION_SUPPORT);
        nameList.add(ProjectUtils.PROJECT_OTHER_ACTION);
        nameList.add(ProjectUtils.PROJECT_STUDY);
        nameList.add(ProjectUtils.PROJECT_IN_GROUP);
        nameList.add(ProjectUtils.PROJECT_SALES_SUPPORT);
        projectDao.updateManagerByNameAndArea(areaId,nameList,newManagerId);

        List<ProjectEntity> projectEntities = projectDao.searchProjectByNameAndArea(areaId, nameList);
        for (ProjectEntity projectEntity : projectEntities) {
            Integer pid = projectEntity.getId();
            List<ProjectUserEntity> allProjectUserById = projectDao.getAllProjectUserById(pid);
            Set<Integer> userSet = new HashSet<>();
            for (ProjectUserEntity projectUserEntity : allProjectUserById) {
                userSet.add(projectUserEntity.getUserId());
            }
            if(newManagerId!=null && !userSet.contains(newManagerId)){
                projectDao.addUserToProject(pid,newManagerId);
            }
        }

    }

    @Override
    public List<ProjectUserEntity> getAllProjectUserByAreaId(Integer areaId) {
        return projectDao.getAllProjectUserByAreaId(areaId);
    }

    @Override
    public List<ProjectUserEntity> getAllProjectUserByManagerId(Integer managerId) {
        return projectDao.getAllProjectUserByManagerId(managerId);
    }

    @Override
    public Double sumWorkTotal(String startTime, String endTime, Integer projectId) {
        return projectDao.sumWorkTotal(startTime,endTime,projectId);
    }

    @Override
    public List<ProjectEntity> queryProjectByIdOrAreaId(String startTime, String endTime, Integer[] areaId, Integer[] projectId) {
        return projectDao.queryProjectByIdOrAreaId(startTime,endTime, areaId,projectId);
    }

    @Override
    public List<String> queryProjectAndDistinct(String startTime, String endTime, Integer[] areaId, Integer[] projectId) {
        return projectDao.queryProjectAndDistinct(startTime, endTime, areaId, projectId);
    }

    @Override
    public List<String> queryProjectIsNullAndDistinct(String startTime, String endTime, Integer[] areaId, Integer[] projectId) {
        return projectDao.queryProjectIsNullAndDistinct(startTime, endTime, areaId, projectId);
    }

    @Override
    public List<ResReportFormEntityParam> queryEveryUserTime(String startTime, String endTime, Integer[] areaId, Integer[] projectId) {
        return projectDao.queryEveryUserTime(startTime, endTime, areaId, projectId);
    }

    @Override
    public List<ProjectEntity> queryProjectByName(Integer[] areaId,String[] projectName) {
        return projectDao.queryProjectByName(areaId, projectName);

    }

    @Override
    public IPage<ProjectAdvanceEntity> searchProjectListByAssignUser(BasePagesParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        Page<ProjectAdvanceEntity> iPage = new Page<>();
        iPage.setCurrent(param.getCurrentPage());
        iPage.setSize(param.getPageSize());


        IPage<ProjectAdvanceEntity> projectAdvanceEntityIPage = projectDao.searchProjectListByAssignUser(iPage, currentUserId,currentUserAreaId);
        return projectAdvanceEntityIPage;
    }

    @Override
    public void editProject(ProjectEntity entity) {
        projectDao.updateById(entity);
    }

    @Override
    public Integer queryProjectCountBySystemId(Integer systemProjectId) {
        return projectDao.queryProjectCountBySystemId(systemProjectId);
    }

    @Override
    public List<ProjectAdvanceEntity> searchAllProjectListByUserIdAndAreaId(Integer userId, Integer areaId, boolean superFlag){
        return projectDao.searchAllProjectListByUserIdAndAreaId(userId,areaId,superFlag);
    }

    @Override
    public void addUserWithProject(Integer userId, Integer projectId) {
        projectDao.addUserWithProject(userId,projectId);
    }

    @Override
    public List<Integer> getProjectIdsByName(String name) {
        return projectDao.getProjectIdsByName(name);
    }

    @Override
    public List<ProjectUserCountEntityParam> searchBoardProjectUser() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        List<ProjectUserCountEntityParam> projectUserCountEntityParams = projectDao.searchBoardProjectUserTop6(currentUserId, currentUserAreaId, currentUserRole.getCode());
//        Integer all = projectDao.searchBoardProjectUserAll(currentUserId, currentUserAreaId, currentUserRole.getCode());
//        ProjectUserCountEntityParam param = new ProjectUserCountEntityParam();
//        Integer other = all;
//        if(!CollectionUtils.isEmpty(projectUserCountEntityParams)){
//            for (ProjectUserCountEntityParam projectUserCountEntityParam : projectUserCountEntityParams) {
//                other = other - projectUserCountEntityParam.getCountUser();
//            }
//            param.setCountUser(other);
//            param.setProjectName("其他");
//            projectUserCountEntityParams.add(param);
//        }
        return projectUserCountEntityParams;
    }

    @Override
    public List<ProjectAdvanceEntity> searchBoardProjectList(Integer pjNumber) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Integer currentUserId = securityContextUtils.getCurrentUserId();
        Integer currentUserAreaId = securityContextUtils.getCurrentUserAreaId();
        RoleTypeEnum currentUserRole = securityContextUtils.getCurrentUserRole();
        List<ProjectAdvanceEntity> projectAdvanceEntities = projectDao.searchBoardProjectListTop6(currentUserId,currentUserAreaId, currentUserRole.getCode(),pjNumber);
//        Double all = projectDao.searchBoardProjectListAll(currentUserId,currentUserAreaId, currentUserRole.getCode());
//        if(all == null){
//            all =0.0;
//        }
//        ProjectAdvanceEntity entity = new ProjectAdvanceEntity();
//        entity.setId(0);
//        entity.setName("其他");
//        Double pre4 = 0.0;
//        if(!CollectionUtils.isEmpty(projectAdvanceEntities)){
//            for (ProjectAdvanceEntity projectAdvanceEntity : projectAdvanceEntities) {
//                pre4 = pre4 + projectAdvanceEntity.getHours();
//            }
//
//        }
//        entity.setHours(all - pre4);
//        projectAdvanceEntities.add(entity);
        return projectAdvanceEntities;
    }

    @Override
    public List<ProjectEntity> searchProjectBySystemProjectId(Integer systemProjectId) {
        return projectDao.searchProjectBySystemProjectId(systemProjectId);
    }

    @Override
    public List<ProjectEntity> searchAllForceDescProject() {
        return projectDao.searchAllForceDescProject();
    }

    @Override
    public List<ProjectAdvanceEntity> searchAssociatedProjectAreaList(Integer mainAreaId, Integer managerId) {
        return projectDao.searchAssociatedProjectAreaList(mainAreaId,managerId);
    }

    @Override
    public List<ProjectAdvanceEntity> searchAssociatedProjectUserHoursList(Integer mainAreaId, Integer managerId) {
        return projectDao.searchAssociatedProjectUserHoursList(mainAreaId,managerId);
    }


}
