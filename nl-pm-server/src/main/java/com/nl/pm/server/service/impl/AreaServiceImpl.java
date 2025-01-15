package com.nl.pm.server.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.common.ProjectUtils;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IAreaRegistry;
import com.nl.pm.server.registry.IProjectRegistry;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.ProjectEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.AreaPageableQueryParam;
import com.nl.pm.server.service.IAreaService;
import com.nl.pm.server.service.IRoleService;
import com.nl.pm.server.service.model.AreaModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.AreaSearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AreaServiceImpl implements IAreaService {

    @Autowired
    private IAreaRegistry areaRegistry;

    @Autowired
    private IProjectRegistry projectRegistry;

    @Autowired
    private IRoleService roleService;

    @Override
    public List<AreaModel> getAllAreaByIds(Integer[] areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<AreaModel> list = new ArrayList<>();
        List<AreaEntity> entities = areaRegistry.getAllAreaByIds(areaId);
        if(CollectionUtils.isNotEmpty(entities)){
            for(AreaEntity entity:entities){
                list.add((AreaModel)EntityUtils.fillModelWithEntity(entity));
            }
        }
        return list;
    }

    @Override
    public void updateAreaByAreaId(Integer areaId) {
        areaRegistry.updateAreaByAreaId(areaId);
    }

    @Override
    public AreaModel queryAreaByUserId(Integer areaId,Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        AreaEntity areaEntity = areaRegistry.queryAreaByUserId(areaId,userId);
        return (AreaModel)EntityUtils.fillModelWithEntity(areaEntity);
    }

    @Override
    public void updateAreaById(AreaModel areaModel) throws Exception {
        AreaEntity areaEntity = (AreaEntity) EntityUtils.convertModelToEntity(areaModel);
        areaRegistry.updateAreaById(areaEntity);
    }

    /**
     * 获取区域详情
     *
     * @param areaId
     * @return
     */
    @Override
    public AreaModel getAreaInfo(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        AreaEntity areaEntity = areaRegistry.getAreaById(areaId);
        if(areaEntity!=null){
            return (AreaModel) EntityUtils.fillModelWithEntity(areaEntity);
        }else{
            return null;
        }

    }

    /**
     * 获取区域列表
     *
     * @param areaSearchParam
     * @return
     */
    @Override
    public BasePagesDomain<AreaModel> getAreaList(AreaSearchParam areaSearchParam) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        int startIndex = (areaSearchParam.getPageNumber() - 1) * areaSearchParam.getPageSize();
        AreaPageableQueryParam areaPageableQueryParam = new AreaPageableQueryParam();

        areaPageableQueryParam.setStartIndex(startIndex);
        areaPageableQueryParam.setPageSize(areaSearchParam.getPageSize());
        areaPageableQueryParam.setGlobalName(areaSearchParam.getGolbalName());
        areaPageableQueryParam.setStatusFlag(areaSearchParam.getStatusFlag());
        BasePagesDomain<AreaEntity> areaEntityPagesDomain = areaRegistry.getAreaList(areaPageableQueryParam);

        List<AreaModel> areaModelList = new ArrayList<>();
        for (AreaEntity areaEntity : areaEntityPagesDomain.getTotalList()) {
            areaModelList.add((AreaModel) EntityUtils.fillModelWithEntity(areaEntity));
        }
        IPage<AreaModel> iPage = new Page<>();
        iPage.setRecords(areaModelList);
        iPage.setTotal(areaEntityPagesDomain.getTotal());
        return new BasePagesDomain<>(iPage);

    }

    /**
     * 新增区域
     *
     * @param areaModel
     * @return
     */
    @Override
    @Transactional
    public Integer addArea(AreaModel areaModel) throws Exception {
        String name = areaModel.getName();
        Integer checkAreaName = areaRegistry.checkAreaName(name);
        if(checkAreaName!=null && checkAreaName>0){
            throw new BaseServiceException(ServiceErrorCodeEnum.AREA_NAME_DUMPLII_ERROR);
        }
        AreaEntity areaEntity = (AreaEntity) EntityUtils.convertModelToEntity(areaModel);
        Integer areaId = areaRegistry.addArea(areaEntity);
        Integer managerId = areaEntity.getManagerId();
        // 初始化项目
        initProject(areaId, managerId);
        return areaId;

    }

    /**
     * 初始化项目
     *
     * @param areaId
     * @return
     */
    @Transactional
    public void initProject(Integer areaId, Integer managerId) throws BaseServiceException {
        if (areaId == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.AREAID_NULL_ERROR);
        }
//        if (managerId == null) {
//            throw new BaseServiceException(ServiceErrorCodeEnum.AREAMANAGERID_NULL_ERROR);
//        }
        ProjectEntity projectMeeting = new ProjectEntity();
        projectMeeting.setAreaId(areaId);
        projectMeeting.setManagerId(managerId);
        projectMeeting.setName(ProjectUtils.PROJECT_MEETING);
        projectRegistry.createProject(projectMeeting);
        ProjectEntity projectTraining = new ProjectEntity();
        projectTraining.setAreaId(areaId);
        projectTraining.setManagerId(managerId);
        projectTraining.setName(ProjectUtils.PROJECT_TRAINING);
        projectRegistry.createProject(projectTraining);
        ProjectEntity projectExhibitionSupport = new ProjectEntity();
        projectExhibitionSupport.setAreaId(areaId);
        projectExhibitionSupport.setManagerId(managerId);
        projectExhibitionSupport.setName(ProjectUtils.PROJECT_EXHIBITION_SUPPORT);
        projectRegistry.createProject(projectExhibitionSupport);
        ProjectEntity projectOtherAction = new ProjectEntity();
        projectOtherAction.setAreaId(areaId);
        projectOtherAction.setManagerId(managerId);
        projectOtherAction.setName(ProjectUtils.PROJECT_OTHER_ACTION);
        projectRegistry.createProject(projectOtherAction);
        ProjectEntity projectStudy = new ProjectEntity();
        projectStudy.setAreaId(areaId);
        projectStudy.setManagerId(managerId);
        projectStudy.setName(ProjectUtils.PROJECT_STUDY);
        projectRegistry.createProject(projectStudy);

        ProjectEntity projectInGroup = new ProjectEntity();
        projectInGroup.setAreaId(areaId);
        projectInGroup.setManagerId(managerId);
        projectInGroup.setName(ProjectUtils.PROJECT_IN_GROUP);
        projectRegistry.createProject(projectInGroup);

        ProjectEntity projectSalesSupport = new ProjectEntity();
        projectSalesSupport.setAreaId(areaId);
        projectSalesSupport.setManagerId(managerId);
        projectSalesSupport.setName(ProjectUtils.PROJECT_SALES_SUPPORT);
        projectRegistry.createProject(projectSalesSupport);

    }

    /**
     * 编辑区域
     *
     * @param areaModel
     */
    @Override
    @Transactional
    public void updateArea(AreaModel areaModel) throws BaseServiceException {
        AreaEntity oldAreaEntity = areaRegistry.getAreaById(areaModel.getId());
        String oldName = oldAreaEntity.getName();
        String newName = areaModel.getName();
        if(!oldName.equals(newName)) {
            Integer checkAreaName = areaRegistry.checkAreaName(newName);
            if (checkAreaName != null && checkAreaName > 0) {
                throw new BaseServiceException(ServiceErrorCodeEnum.AREA_NAME_DUMPLII_ERROR);
            }
        }
        AreaEntity areaEntity = new AreaEntity();
        areaEntity.setId(areaModel.getId());
        areaEntity.setName(areaModel.getName());
        areaEntity.setDesc(areaModel.getDesc());
        areaEntity.setManagerId(areaModel.getManagerId());
        if(areaEntity.getManagerId()!=null && !oldAreaEntity.getManagerId().equals(areaEntity.getManagerId())){

            //查询该区域下是否有角色
            Integer integer = roleService.queryCountByAreaId(areaModel.getId());
            if (integer != null && integer > 0) {
                throw new BaseServiceException(ServiceErrorCodeEnum.AREA_EXITS_MANAGER_ERROR);
            }

            projectRegistry.updateManagerByNameAndArea(areaModel.getId(),areaModel.getManagerId());
        }
        areaRegistry.updateArea(areaEntity);

    }

    @Override
    public void updateAreaStatus(Integer areaId,Boolean status) throws BaseServiceException {
        areaRegistry.updateAreaStatus(areaId,status);
    }

    /**
     * 删除区域
     *
     * @param areaId
     */
    @Override
    public void deleteArea(Integer areaId) {
        areaRegistry.deleteArea(areaId);
    }

    @Override
    public Integer checkAreaDayReport(Integer areaId) {
        return areaRegistry.checkAreaDayReport(areaId);
    }

    @Override
    public Integer checkAreaOtherProject(Integer areaId) {
        return areaRegistry.checkAreaOtherProject(areaId);
    }

    @Override
    public Integer checkAreaOtherUser(Integer areaId) {
        return areaRegistry.checkAreaOtherUser(areaId);
    }

    @Override
    public void assignUserToArea(Integer areaId, List<Integer> userIdList) {
        areaRegistry.assignUserToArea(areaId,userIdList);
    }

    @Override
    public List<UserModel> searchOtherAreaAssUser(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<UserEntity> userEntities = areaRegistry.searchOtherAreaAssUser(areaId);
        List<UserModel> modelList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(userEntities)) {
            for (UserEntity userEntity : userEntities) {
                UserModel userModel = (UserModel) EntityUtils.fillModelWithEntity(userEntity);
                modelList.add(userModel);
            }
        }
        return modelList;
    }

    @Override
    public List<AreaModel> projectNameASSAreaList(String projectName) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<AreaEntity> areaEntities = areaRegistry.projectNameASSAreaList(projectName);
        List<AreaModel> modelList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(areaEntities)){
            for (AreaEntity areaEntity : areaEntities) {
                AreaModel model = (AreaModel)EntityUtils.fillModelWithEntity(areaEntity);
                modelList.add(model);
            }
        }
        return modelList;
    }
}
