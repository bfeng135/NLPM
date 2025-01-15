package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.registry.entity.SystemProjectEntity;
import com.nl.pm.server.registry.entity.SystemStageEntity;
import com.nl.pm.server.registry.param.QuerySystemProjectPagingEntityParam;
import com.nl.pm.server.registry.param.RpaSystemProjectEntityParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 14:58
 */
@Mapper
public interface SystemProjectDao extends BaseMapper<SystemProjectEntity> {
    void batchSaveSystemStage(@Param("collect")  List<SystemStageEntity> collect);
    void updateStage(@Param("id")String id,@Param("name") String name);
    void updateSystemProjectNameById(@Param("id") Integer id,@Param("name") String name);
    List<SystemStageEntity> distinctGetAllStage();
    void batchSave(@Param("list")List<RpaSystemProjectEntityParam> list);
    void updateProjectTemplate(@Param("id") Integer id,@Param("areaId") Integer areaId);
    Integer queryProjectTemplateCountByName(@Param("name")String name);

    IPage<SystemProjectEntity> querySystemProjectPaging(Page<SystemProjectEntity> page, @Param("entityParam")QuerySystemProjectPagingEntityParam entityParam);


    Integer editProjectTemplateByCrmId(@Param("entity") SystemProjectEntity entity);

    Integer editProjectTemplateByName(@Param("entity") SystemProjectEntity entity);

    SystemProjectEntity queryProjectTemplateByCrmId(@Param("entity")SystemProjectEntity entity);
    SystemProjectEntity getSystemProjectByName(@Param("name")String name);
    SystemProjectEntity getSystemProjectById(@Param("id")Integer id);
    void changeForceDescFlag(@Param("id")Integer id,@Param("flag")Boolean flag);
    void editGoalHours(@Param("id")Integer id,@Param("goalHours")Double goalHours);
}
