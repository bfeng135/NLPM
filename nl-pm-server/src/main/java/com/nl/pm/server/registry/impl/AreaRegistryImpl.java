package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.common.ProjectUtils;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.registry.IAreaRegistry;
import com.nl.pm.server.registry.dao.AreaDao;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.AreaPageableQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AreaRegistryImpl implements IAreaRegistry {

    @Autowired
    private AreaDao areaDao;

    @Override
    public List<AreaEntity> getAllAreaByIds(Integer[] areaId) {
        return areaDao.getAllAreaByIds(areaId);
    }

    @Override
    public Integer queryCountManager(Integer userId) {
        return areaDao.queryCountManager(userId);
    }

    @Override
    public void updateAreaByAreaId(Integer areaId) {
        areaDao.updateAreaByAreaId(areaId);
    }

    @Override
    public AreaEntity queryAreaByUserId(Integer areaId,Integer userId) {
//        QueryWrapper<AreaEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id",areaId).eq("manager_id",userId);
        return areaDao.queryAreaByUserId(areaId,userId);
    }

    @Override
    public void updateAreaById(AreaEntity areaEntity) {
        areaDao.updateById(areaEntity);
    }

    /**
     * 查询区域详细
     * @param areaId
     * @return
     */
    @Override
    public AreaEntity getAreaById(Integer areaId) {

        AreaEntity areaEntity = areaDao.getAreaById(areaId);

        return areaEntity;
    }

    /**
     * 查询区域列表
     * @param areaPageableQueryParam
     * @return
     */
    @Override
    public BasePagesDomain<AreaEntity> getAreaList(AreaPageableQueryParam areaPageableQueryParam) {
        List<AreaEntity> areaEntityList = areaDao.getAreaList(areaPageableQueryParam);
        long totalCount = areaDao.getAreaTotalByParams(areaPageableQueryParam);
        IPage<AreaEntity> iPage = new Page<>();
        iPage.setTotal(totalCount);
        iPage.setRecords(areaEntityList);
        BasePagesDomain<AreaEntity> areaPage = new BasePagesDomain<>(iPage);
        areaPage.setTotalList(areaEntityList);
        return areaPage;
    }

    /**
     * 新增区域
     * @param areaEntity
     * @return
     */
    @Override
    public Integer addArea(AreaEntity areaEntity) {
        areaDao.addArea(areaEntity);
        return areaEntity.getId();
    }

    /**
     * 编辑区域
     * @param areaEntity
     */
    @Override
    public void updateArea(AreaEntity areaEntity) {
        areaDao.updateArea(areaEntity);
    }

    @Override
    public void updateAreaStatus(Integer areaId, Boolean status) {
        areaDao.updateAreaStatus(areaId,status);
    }

    /**
     * 删除区域
     * @param areaId
     */
    @Override
    public void deleteArea(Integer areaId) {
        areaDao.deleteArea(areaId);
    }

    @Override
    public Integer checkAreaDayReport(Integer areaId) {
        return areaDao.checkAreaDayReport(areaId);
    }

    @Override
    public Integer checkAreaOtherProject(Integer areaId) {
        List<String> nameList = new ArrayList<>();
        nameList.add(ProjectUtils.PROJECT_MEETING);
        nameList.add(ProjectUtils.PROJECT_TRAINING);
        nameList.add(ProjectUtils.PROJECT_EXHIBITION_SUPPORT);
        nameList.add(ProjectUtils.PROJECT_OTHER_ACTION);
        nameList.add(ProjectUtils.PROJECT_STUDY);
        nameList.add(ProjectUtils.PROJECT_IN_GROUP);
        nameList.add(ProjectUtils.PROJECT_SALES_SUPPORT);
        return areaDao.checkAreaOtherProject(areaId,nameList);
    }

    @Override
    public Integer checkAreaOtherUser(Integer areaId) {
        return areaDao.checkAreaOtherUser(areaId);
    }

    @Override
    public Integer checkAreaName(String name) {
        return areaDao.checkAreaName(name);
    }

    @Override
    public void assignUserToArea(Integer areaId, List<Integer> userIdList) {
        areaDao.assignUserToArea(areaId,userIdList);
    }

    @Override
    public List<UserEntity> searchOtherAreaAssUser(Integer areaId) {
        return areaDao.searchOtherAreaAssUser(areaId);
    }

    @Override
    public List<AreaEntity> projectNameASSAreaList(String projectName) {
        return areaDao.projectNameASSAreaList(projectName);
    }
}
