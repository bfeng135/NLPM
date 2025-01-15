package com.nl.pm.server.registry.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.registry.ISystemProjectRegistry;
import com.nl.pm.server.registry.dao.SystemProjectDao;
import com.nl.pm.server.registry.entity.SystemProjectEntity;
import com.nl.pm.server.registry.entity.SystemStageEntity;
import com.nl.pm.server.registry.param.QuerySystemProjectPagingEntityParam;
import com.nl.pm.server.registry.param.RpaSystemProjectEntityParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 14:57
 */
@Repository
public class SystemProjectRegistryImpl implements ISystemProjectRegistry {
    @Autowired
    private SystemProjectDao systemProjectDao;

    @Override
    public void batchSaveSystemStage( List<SystemStageEntity> collect) {
        systemProjectDao.batchSaveSystemStage(collect);
    }

    @Override
    public void updateStage(String id, String name) {
        systemProjectDao.updateStage(id,name);
    }

    @Override
    public List<SystemStageEntity> distinctGetAllStage() {
        return systemProjectDao.distinctGetAllStage();
    }

    @Override
    public void batchSave(List<RpaSystemProjectEntityParam> list) {
        systemProjectDao.batchSave(list);
    }

    @Override
    public void updateProjectTemplate(Integer id, Integer areaId) {
        systemProjectDao.updateProjectTemplate(id,areaId);
    }

    @Override
    public List<SystemProjectEntity> findAllProjectTemplate() {
        QueryWrapper<SystemProjectEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enable",true);
        queryWrapper.orderByAsc("name");
        return systemProjectDao.selectList(queryWrapper);
    }

    @Override
    public Integer queryProjectTemplateCountByName(String name) {
        return systemProjectDao.queryProjectTemplateCountByName(name);
    }

    @Override
    public void addProjectTemplate(SystemProjectEntity entity) {
        systemProjectDao.insert(entity);
    }

    @Override
    public void editProjectTemplate(SystemProjectEntity entity) {
        systemProjectDao.updateSystemProjectNameById(entity.getId(), entity.getName());
    }

    @Override
    public Integer editProjectTemplateByCrmId(SystemProjectEntity entity) {
        return systemProjectDao.editProjectTemplateByCrmId(entity);
    }

    @Override
    public Integer editProjectTemplateByName(SystemProjectEntity entity) {
        return systemProjectDao.editProjectTemplateByName(entity);
    }

    @Override
    public SystemProjectEntity queryProjectTemplateByCrmId(SystemProjectEntity entity) {
        return systemProjectDao.queryProjectTemplateByCrmId(entity);
    }

    @Override
    public void delProjectTemplate(Integer id) {
        systemProjectDao.deleteById(id);
    }

    @Override
    public IPage<SystemProjectEntity> querySystemProjectPaging(QuerySystemProjectPagingEntityParam entityParam) {
        Page<SystemProjectEntity> page = new Page<>(entityParam.getCurrentPage(), entityParam.getPageSize());
        return systemProjectDao.querySystemProjectPaging(page,entityParam);
    }

    @Override
    public SystemProjectEntity queryProjectByName(String name) {
        return systemProjectDao.getSystemProjectByName(name);
    }

    @Override
    public SystemProjectEntity queryProjectById(Integer id) {
        return systemProjectDao.selectById(id);
    }

    @Override
    public void changeForceDescFlag(Integer id) {
        SystemProjectEntity systemProjectEntity = systemProjectDao.getSystemProjectById(id);
        Boolean forceDescFlag = systemProjectEntity.getForceDescFlag();
        if(null != forceDescFlag){
            systemProjectDao.changeForceDescFlag(systemProjectEntity.getId(),!forceDescFlag);
        }
    }

    @Override
    public void editGoalHours(Integer id, Double goalHours) {
        systemProjectDao.editGoalHours(id,goalHours);
    }
}
