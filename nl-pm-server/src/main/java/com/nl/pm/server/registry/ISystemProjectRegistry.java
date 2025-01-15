package com.nl.pm.server.registry;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nl.pm.server.registry.entity.SystemProjectEntity;
import com.nl.pm.server.registry.entity.SystemStageEntity;
import com.nl.pm.server.registry.param.QuerySystemProjectPagingEntityParam;
import com.nl.pm.server.registry.param.RpaSystemProjectEntityParam;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 14:57
 */
public interface ISystemProjectRegistry {
    void batchSaveSystemStage( List<SystemStageEntity> collect);
    void updateStage(String id,String name);
    List<SystemStageEntity> distinctGetAllStage();
    void batchSave(List<RpaSystemProjectEntityParam> list);
    void updateProjectTemplate(Integer id,Integer areaId);
    List<SystemProjectEntity> findAllProjectTemplate();

    Integer queryProjectTemplateCountByName(String name);

    void addProjectTemplate(SystemProjectEntity entity);

    void editProjectTemplate(SystemProjectEntity entity);

    Integer editProjectTemplateByCrmId(SystemProjectEntity entity);

    Integer editProjectTemplateByName(SystemProjectEntity entity);

    SystemProjectEntity queryProjectTemplateByCrmId(SystemProjectEntity entity);

    void delProjectTemplate(Integer id);

    IPage<SystemProjectEntity>  querySystemProjectPaging(QuerySystemProjectPagingEntityParam entityParam);
    SystemProjectEntity queryProjectByName(String name);
    SystemProjectEntity queryProjectById(Integer id);
    void changeForceDescFlag(Integer id);
    void editGoalHours(Integer id,Double goalHours);
}
