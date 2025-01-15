package com.nl.pm.server.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.common.pages.BasePagesParam;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.IProjectRegistry;
import com.nl.pm.server.registry.entity.ProjectAdvanceEntity;
import com.nl.pm.server.registry.entity.ProjectEntity;
import com.nl.pm.server.registry.entity.ProjectUserEntity;
import com.nl.pm.server.registry.param.DownProjectReportFormEntityParam;
import com.nl.pm.server.registry.param.ProjectAreaEntityParam;
import com.nl.pm.server.registry.param.ProjectSearchEntityParam;
import com.nl.pm.server.registry.param.ProjectUserCountEntityParam;
import com.nl.pm.server.service.IProjectService;
import com.nl.pm.server.service.model.ProjectAdvanceModel;
import com.nl.pm.server.service.model.ProjectModel;
import com.nl.pm.server.service.model.ProjectUserModel;
import com.nl.pm.server.service.param.DownProjectReportFormModelParam;
import com.nl.pm.server.service.param.ProjectAreaModelParam;
import com.nl.pm.server.service.param.ProjectSearchModelParam;
import com.nl.pm.server.service.param.ProjectUserCountModelParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private IProjectRegistry iProjectRegistry;

    @Override
    public List<DownProjectReportFormModelParam> queryProjectAreaUsers(List<ProjectAreaModelParam> params) throws Exception {
        List<ProjectAreaEntityParam> projectAreaEntityParams = new ArrayList<>();
        List<DownProjectReportFormModelParam> list = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(params)){
            for (ProjectAreaModelParam modelParam: params){
                ProjectAreaEntityParam param = new ProjectAreaEntityParam();
                param.setProjectId(modelParam.getProjectId());
                param.setAreaId(modelParam.getAreaId());
                projectAreaEntityParams.add(param);
            }
        }
        List<DownProjectReportFormEntityParam> entityParams = iProjectRegistry.queryProjectAreaUsers(projectAreaEntityParams);
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(entityParams)){
            for(DownProjectReportFormEntityParam entityParam:entityParams){
                DownProjectReportFormModelParam param = new DownProjectReportFormModelParam();
                param.setProjectId(entityParam.getProjectId());
                param.setProjectName(entityParam.getProjectName());
                if(entityParam.getProjectEnable()){
                    param.setProjectEnable("开启");
                }else{
                    param.setProjectEnable("关闭");
                }
                param.setAreaId(entityParam.getAreaId());
                param.setAreaName(entityParam.getAreaName());
                param.setUserId(entityParam.getUserId());
                param.setNickname(entityParam.getNickname());
                param.setRoleCode(entityParam.getRoleCode());
                param.setRoleName(entityParam.getRoleName());
                if(entityParam.getUserStatus()){
                    param.setUserStatus("在职");
                }else {
                    param.setUserStatus("离职");
                }
                list.add(param);
            }
        }
        return list;
    }

    @Override
    public void createProject(ProjectModel model) throws Exception {
        ProjectEntity entity = (ProjectEntity)EntityUtils.convertModelToEntity(model);
        iProjectRegistry.createProject(entity);
    }

    @Override
    public BasePagesDomain<ProjectAdvanceModel> searchProject(ProjectSearchModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ProjectSearchEntityParam entityParam = new ProjectSearchEntityParam();
        entityParam.setEnableFlag(param.getEnableFlag());
        entityParam.setAreaId(param.getAreaId());
        entityParam.setManagerId(param.getManagerId());
        entityParam.setUserId(param.getUserId());
        entityParam.setName(param.getName());
        entityParam.setPageSize(param.getPageSize());
        entityParam.setCurrentPage(param.getCurrentPage());
        IPage<ProjectAdvanceEntity> projectAdvanceEntityIPage = iProjectRegistry.searchProject(entityParam);
        List<ProjectAdvanceModel> list = new ArrayList<>();
        for (ProjectAdvanceEntity record : projectAdvanceEntityIPage.getRecords()) {
            ProjectAdvanceModel m = (ProjectAdvanceModel) EntityUtils.fillModelWithEntity(record);
            list.add(m);
        }

        BasePagesDomain<ProjectAdvanceModel> model = new BasePagesDomain(projectAdvanceEntityIPage);
        model.setTotalList(list);
        return model;
    }

    @Override
    public ProjectAdvanceModel fetchProject(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ProjectAdvanceEntity entity = iProjectRegistry.fetchProject(id);
        ProjectAdvanceModel model = (ProjectAdvanceModel) EntityUtils.fillModelWithEntity(entity);
        return model;
    }

    @Override
    public List<ProjectUserModel> getAllProjectUserById(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectUserEntity> allProjectUserById = iProjectRegistry.getAllProjectUserById(id);
        List<ProjectUserModel> list = new ArrayList<>();
        for (ProjectUserEntity projectUserEntity : allProjectUserById) {
            ProjectUserModel model = (ProjectUserModel) EntityUtils.fillModelWithEntity(projectUserEntity);
            list.add(model);
        }
        return list;
    }

    @Override
    public Integer checkProjectNameExisted(Integer areaId, String name) {
        Integer count = iProjectRegistry.checkProjectNameExisted(areaId, name);
        return count;
    }

    @Override
    public void updateProject(ProjectModel model) throws Exception {
        ProjectEntity entity = (ProjectEntity) EntityUtils.convertModelToEntity(model);
        iProjectRegistry.updateProject(entity);
    }

    @Override
    public void closeAllRelativeProjectBySystemProjectId(Integer systemProjectId) {
        iProjectRegistry.closeAllRelativeProjectBySystemProjectId(systemProjectId);
    }

    @Override
    public ProjectModel fetchProjectBySystemProjectIdAndAreaId(Integer systemProjectId, Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ProjectEntity projectEntity = iProjectRegistry.fetchProjectBySystemProjectIdAndAreaId(systemProjectId, areaId);
        ProjectModel model = (ProjectModel) EntityUtils.fillModelWithEntity(projectEntity);
        return model;
    }

    @Override
    public void openOrClose(Integer id, boolean enable) {
        iProjectRegistry.openOrClose(id,enable);
    }

    @Override
    public void assignEmployees(Integer projectId , List<Integer> userIdList) throws Exception {

        iProjectRegistry.assignEmployees(projectId,userIdList);

    }

    @Override
    public Integer checkDayReportByProjectId(Integer projectId) {
        return iProjectRegistry.checkDayReportByProjectId(projectId);
    }

    @Override
    public void deleteProject(Integer projectId) {
        iProjectRegistry.deleteProject(projectId);
    }

    @Override
    public List<ProjectUserModel> getAllProjectUserByAreaId(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectUserModel> list = new ArrayList<>();
        for (ProjectUserEntity projectUserEntity : iProjectRegistry.getAllProjectUserByAreaId(areaId)) {
            ProjectUserModel model = (ProjectUserModel) EntityUtils.fillModelWithEntity(projectUserEntity);
            list.add(model);
        }

        return list;
    }

    @Override
    public List<ProjectUserModel> getAllProjectUserByManagerId(Integer manager) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectUserModel> list = new ArrayList<>();
        for (ProjectUserEntity projectUserEntity : iProjectRegistry.getAllProjectUserByManagerId(manager)) {
            ProjectUserModel model = (ProjectUserModel) EntityUtils.fillModelWithEntity(projectUserEntity);
            list.add(model);
        }

        return list;
    }

    @Override
    public Double sumWorkTotal(String startTime, String endTime, Integer projectId) {
        return iProjectRegistry.sumWorkTotal(startTime,endTime,projectId);
    }

    @Override
    public IPage<ProjectAdvanceModel> searchProjectListByAssignUser(BasePagesParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        IPage<ProjectAdvanceEntity> entityIPages = iProjectRegistry.searchProjectListByAssignUser(param);
        List<ProjectAdvanceEntity> projectEntities = entityIPages.getRecords();
        List<ProjectAdvanceModel> modelList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(projectEntities)){
            for (ProjectEntity projectEntity : projectEntities) {
                ProjectAdvanceModel model = (ProjectAdvanceModel)EntityUtils.fillModelWithEntity(projectEntity);
                modelList.add(model);
            }

        }
        IPage<ProjectAdvanceModel> modelPages = new Page<>();
        modelPages.setRecords(modelList);
        modelPages.setCurrent(entityIPages.getCurrent());
        modelPages.setPages(entityIPages.getPages());
        modelPages.setSize(entityIPages.getSize());
        modelPages.setTotal(entityIPages.getTotal());
        return modelPages;
    }

    @Override
    public void editProject(ProjectModel model) throws Exception {
        ProjectEntity entity = (ProjectEntity)EntityUtils.convertModelToEntity(model);
        iProjectRegistry.editProject(entity);
    }

    @Override
    public Integer queryProjectCountBySystemId(Integer systemProjectId) {
        return iProjectRegistry.queryProjectCountBySystemId(systemProjectId);
    }

    @Override
    public List<ProjectAdvanceModel> searchAllProjectListByUserIdAndAreaId(Integer userId, Integer areaId, boolean superFlag) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectAdvanceEntity> projectAdvanceEntities = iProjectRegistry.searchAllProjectListByUserIdAndAreaId(userId, areaId, superFlag);
        List<ProjectAdvanceModel> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(projectAdvanceEntities)){
            for (ProjectAdvanceEntity projectAdvanceEntity : projectAdvanceEntities) {
                ProjectAdvanceModel model = (ProjectAdvanceModel)EntityUtils.fillModelWithEntity(projectAdvanceEntity);
                list.add(model);
            }
        }
        return list;
    }

    @Override
    public void updateManagerByNameAndArea(Integer areaId, Integer managerId) {
        iProjectRegistry.updateManagerByNameAndArea(areaId,managerId);
    }

    @Override
    public void addUserWithProject(Integer userId, Integer projectId) {
        iProjectRegistry.addUserWithProject(userId,projectId);
    }

    @Override
    public List<Integer> getProjectIdsByName(String name) {
        return iProjectRegistry.getProjectIdsByName(name);
    }

    @Override
    public List<ProjectUserCountModelParam> searchBoardProjectUser() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectUserCountEntityParam> projectUserCountEntityParams = iProjectRegistry.searchBoardProjectUser();
        List<ProjectUserCountModelParam> modelList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(projectUserCountEntityParams)){
            for (ProjectUserCountEntityParam projectUserCountEntityParam : projectUserCountEntityParams) {
                ProjectUserCountModelParam model = new ProjectUserCountModelParam();
                model.setProjectName(projectUserCountEntityParam.getProjectName());
                model.setCountUser(projectUserCountEntityParam.getCountUser());
                modelList.add(model);

            }
        }

        return modelList;
    }

    @Override
    public List<ProjectAdvanceModel> searchBoardProjectList(Integer pjNumber) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectAdvanceEntity> projectAdvanceEntities = iProjectRegistry.searchBoardProjectList(pjNumber);
        List<ProjectAdvanceModel> modelList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(projectAdvanceEntities)){
            for (ProjectAdvanceEntity projectAdvanceEntity : projectAdvanceEntities) {
                ProjectAdvanceModel model = (ProjectAdvanceModel) EntityUtils.fillModelWithEntity(projectAdvanceEntity);
                modelList.add(model);
            }
        }
        return modelList;
    }

    @Override
    public List<ProjectModel> searchProjectBySystemProjectId(Integer systemProjectId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectEntity> entityList = iProjectRegistry.searchProjectBySystemProjectId(systemProjectId);
        List<ProjectModel> modelList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(entityList)) {
            for (ProjectEntity entity : entityList) {
                ProjectModel model = (ProjectModel) EntityUtils.fillModelWithEntity(entity);
                modelList.add(model);
            }
        }
        return modelList;
    }

    @Override
    public List<ProjectModel> searchAllForceDescProject() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ProjectEntity> projectEntities = iProjectRegistry.searchAllForceDescProject();
        List<ProjectModel> list = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(projectEntities)){
            for (ProjectEntity projectEntity : projectEntities) {
                ProjectModel model =  (ProjectModel)EntityUtils.fillModelWithEntity(projectEntity);
                list.add(model);
            }

        }
        return list;
    }

    @Override
    public List<ProjectAdvanceModel> searchAssociatedProjectAreaList(Integer mainAreaId, Integer managerId) throws IllegalAccessException, InvocationTargetException, InstantiationException, BaseServiceException, NoSuchMethodException, ClassNotFoundException {
        List<ProjectAdvanceEntity> projectAdvanceEntities = iProjectRegistry.searchAssociatedProjectAreaList(mainAreaId, managerId);
        List<ProjectAdvanceModel> modelList = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(projectAdvanceEntities)){
            for (ProjectAdvanceEntity projectAdvanceEntity : projectAdvanceEntities) {
                ProjectAdvanceModel projectAdvanceModel = (ProjectAdvanceModel) EntityUtils.fillModelWithEntity(projectAdvanceEntity);
                modelList.add(projectAdvanceModel);
            }
        }
        return  modelList;
    }

    @Override
    public List<ProjectAdvanceModel> searchAssociatedProjectUserHoursList(Integer mainAreaId, Integer managerId) throws IllegalAccessException, InvocationTargetException, InstantiationException, BaseServiceException, NoSuchMethodException, ClassNotFoundException {
        List<ProjectAdvanceEntity> projectAdvanceEntities = iProjectRegistry.searchAssociatedProjectUserHoursList(mainAreaId, managerId);
        List<ProjectAdvanceModel> modelList = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(projectAdvanceEntities)){
            for (ProjectAdvanceEntity projectAdvanceEntity : projectAdvanceEntities) {
                ProjectAdvanceModel projectAdvanceModel = (ProjectAdvanceModel) EntityUtils.fillModelWithEntity(projectAdvanceEntity);
                modelList.add(projectAdvanceModel);
            }
        }
        return  modelList;
    }


}
