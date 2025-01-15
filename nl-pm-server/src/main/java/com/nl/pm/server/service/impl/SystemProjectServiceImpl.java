package com.nl.pm.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.ReqRpaSystemProjectVO;
import com.nl.pm.server.controller.vo.ReqSynSystemProjectErrorVO;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.DynamicServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.registry.IProjectRegistry;
import com.nl.pm.server.registry.ISystemProjectRegistry;
import com.nl.pm.server.registry.entity.ProjectEntity;
import com.nl.pm.server.registry.entity.SystemProjectEntity;
import com.nl.pm.server.registry.entity.SystemStageEntity;
import com.nl.pm.server.registry.param.QuerySystemProjectPagingEntityParam;
import com.nl.pm.server.registry.param.RpaSystemProjectEntityParam;
import com.nl.pm.server.service.ISystemProjectService;
import com.nl.pm.server.service.model.SystemProjectModel;
import com.nl.pm.server.service.model.SystemStageModel;
import com.nl.pm.server.service.param.QuerySystemProjectPagingModelParam;
import com.nl.pm.server.service.param.RpaSystemProjectModelParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 14:56
 */
@Service
public class SystemProjectServiceImpl implements ISystemProjectService {
    private static final Logger log = LoggerFactory.getLogger(SystemProjectServiceImpl.class);

    @Autowired
    private ISystemProjectRegistry systemProjectRegistry;
    @Autowired
    private IProjectRegistry projectRegistry;

    @Override
    public List<SystemStageModel> distinctGetAllStage() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<SystemStageModel> list = new ArrayList<>();
        List<SystemStageEntity> entities = systemProjectRegistry.distinctGetAllStage();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(entities)) {
            for (SystemStageEntity entity : entities) {
                list.add((SystemStageModel) EntityUtils.fillModelWithEntity(entity));
            }
        }
        return list;
    }

    @Override
    public Map<String,Object> checkCrmProject(List<ReqRpaSystemProjectVO> vos) throws DynamicServiceException {
        Map<String,Object> resMap = new HashMap<>();

        List<ReqSynSystemProjectErrorVO> errorVOS = new ArrayList<>();
        Map<String, ReqRpaSystemProjectVO> map = new HashMap<>();
        Set<String> name = new HashSet<>();
        Map<String, ReqRpaSystemProjectVO> cpIdMap = new HashMap<>();
        Set<String> cpIdSet = new HashSet<>();
        String strDetail = "";
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(vos)) {
            List<RpaSystemProjectModelParam> params = new ArrayList<>();
            for (ReqRpaSystemProjectVO vo : vos) {
                if (map.containsKey(vo.getName())) {
                    ReqSynSystemProjectErrorVO errorVO = new ReqSynSystemProjectErrorVO();
                    errorVO.setName(vo.getName());
                    errorVO.setCrmProjectId(vo.getCrmProjectId());
                    errorVO.setCrmStageId(vo.getCrmStageId());
                    errorVO.setCrmStageName(vo.getCrmStageName());
                    errorVO.setDesc("项目名【" + vo.getName() + "】重复");
                    errorVOS.add(errorVO);
                    if (!name.contains(vo.getName())) {
                        ReqSynSystemProjectErrorVO errorVO2 = new ReqSynSystemProjectErrorVO();
                        errorVO2.setName(map.get(vo.getName()).getName());
                        errorVO2.setCrmProjectId(map.get(vo.getName()).getCrmProjectId());
                        errorVO2.setCrmStageId(map.get(vo.getName()).getCrmStageId());
                        errorVO2.setCrmStageName(map.get(vo.getName()).getCrmStageName());
                        errorVO2.setDesc("项目名【" + vo.getName() + "】重复");
                        errorVOS.add(errorVO2);
                        name.add(vo.getName());
                    }
                    continue;
                }

                if (cpIdMap.containsKey(vo.getCrmProjectId())) {
                    ReqSynSystemProjectErrorVO errorVO = new ReqSynSystemProjectErrorVO();
                    errorVO.setName(vo.getName());
                    errorVO.setCrmProjectId(vo.getCrmProjectId());
                    errorVO.setCrmStageId(vo.getCrmStageId());
                    errorVO.setCrmStageName(vo.getCrmStageName());
                    errorVO.setDesc("项目ID【" + vo.getCrmProjectId() + "】重复");
                    errorVOS.add(errorVO);
                    if (!cpIdSet.contains(vo.getCrmProjectId())) {
                        ReqSynSystemProjectErrorVO errorVO2 = new ReqSynSystemProjectErrorVO();
                        errorVO2.setName(cpIdMap.get(vo.getCrmProjectId()).getName());
                        errorVO2.setCrmProjectId(cpIdMap.get(vo.getCrmProjectId()).getCrmProjectId());
                        errorVO2.setCrmStageId(cpIdMap.get(vo.getCrmProjectId()).getCrmStageId());
                        errorVO2.setCrmStageName(cpIdMap.get(vo.getCrmProjectId()).getCrmStageName());
                        errorVO2.setDesc("项目ID【" + vo.getCrmProjectId() + "】重复");
                        errorVOS.add(errorVO2);
                        cpIdSet.add(vo.getName());
                    }
                    continue;
                }

                if (vo.getCrmProjectId() == null || vo.getName() == null) {
                    ReqSynSystemProjectErrorVO errorVO = new ReqSynSystemProjectErrorVO();
                    errorVO.setName(vo.getName());
                    errorVO.setCrmProjectId(vo.getCrmProjectId());
                    errorVO.setCrmStageId(vo.getCrmStageId());
                    errorVO.setCrmStageName(vo.getCrmStageName());
                    errorVO.setDesc("项目名或Crm系统对应项目id为空");
                    errorVOS.add(errorVO);
                    continue;
                } else if (vo.getName().indexOf(" ") == 0 || vo.getCrmProjectId().indexOf(" ") == (vo.getCrmProjectId().length()-1) ||
                        vo.getName().length() == 0 || vo.getCrmProjectId().length() == 0) {
                    ReqSynSystemProjectErrorVO errorVO = new ReqSynSystemProjectErrorVO();
                    errorVO.setName(vo.getName());
                    errorVO.setCrmProjectId(vo.getCrmProjectId());
                    errorVO.setCrmStageId(vo.getCrmStageId());
                    errorVO.setCrmStageName(vo.getCrmStageName());
                    errorVO.setDesc("项目名或Crm系统对应项目id为空字符串或这对应的两个字段存在空格");
                    errorVOS.add(errorVO);
                    continue;
                } else {
                    RpaSystemProjectModelParam param = new RpaSystemProjectModelParam();
                    param.setCrmProjectId(vo.getCrmProjectId());
                    param.setCrmStageId(vo.getCrmStageId());
                    param.setCrmStageName(vo.getCrmStageName());
                    param.setName(vo.getName());
                    param.setCrmCustomerNameShort(vo.getCrmCustomerNameShort());
                    map.put(vo.getName(), vo);
                    cpIdMap.put(vo.getCrmProjectId(), vo);
                    params.add(param);
                }
            }

            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(params)) {
                try {
                    Map resMapSync = this.synSystemProject(params);
                    strDetail = resMapSync.get("strDetail")==null?"":(String) resMapSync.get("strDetail");

                    List<ReqSynSystemProjectErrorVO> resList = new ArrayList<>();
                    Object resObjList = resMapSync.get("resList");
                    if(resObjList != null){
                        resList = (List<ReqSynSystemProjectErrorVO>) resMapSync.get("resList");
                    }

                    errorVOS.addAll(resList);
                } catch (Exception e) {
                    throw new DynamicServiceException(e.getMessage());
                }
            }


            resMap.put("strDetail",strDetail);
            resMap.put("resList",errorVOS);
            return resMap;
        }else{
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String,Object> synSystemProject(List<RpaSystemProjectModelParam> params) throws BaseServiceException {
        List<ReqSynSystemProjectErrorVO> errorVOS = new ArrayList<>();
        QuerySystemProjectPagingEntityParam param = new QuerySystemProjectPagingEntityParam();
        param.setSystemProjectName(null);
        param.setPageSize(99999999);
        param.setCurrentPage(1);
        //查询数据库中所有系统项目数据
        IPage<SystemProjectEntity> systemProjectEntityIPage = systemProjectRegistry.querySystemProjectPaging(param);
        List<SystemStageEntity> entities = systemProjectRegistry.distinctGetAllStage();
        Map<String, String> mapSystemStageAllData = new HashMap<>();
        String strDetail = "";
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(entities)) {
            mapSystemStageAllData = entities.stream().collect(Collectors.toMap(SystemStageEntity::getCrmStageId, SystemStageEntity::getCrmStageName, (key1, key2) -> key2));
        }

        //Map<Integer, String> mapSystemStage = new HashMap<>();
        //如果数据库中所有系统项目数据为空，rpa传过来的数据 直接保存
        if (org.apache.commons.collections.CollectionUtils.isEmpty(systemProjectEntityIPage.getRecords())) {
            List<RpaSystemProjectEntityParam> list = new ArrayList<>();
            for (RpaSystemProjectModelParam modelParam : params) {

                if(modelParam.getCrmStageId() != null && modelParam.getCrmStageName() != null){
                    if(mapSystemStageAllData.containsValue(modelParam.getCrmStageName())){
                        if(mapSystemStageAllData.get(modelParam.getCrmStageId())==null || !mapSystemStageAllData.get(modelParam.getCrmStageId()).equalsIgnoreCase(modelParam.getCrmStageName())){
                            ReqSynSystemProjectErrorVO vo = new ReqSynSystemProjectErrorVO();
                            vo.setName(modelParam.getName());
                            vo.setCrmProjectId(modelParam.getCrmProjectId());
                            vo.setCrmStageId(modelParam.getCrmStageId());
                            vo.setCrmStageName(modelParam.getCrmStageName());
                            vo.setDesc("阶段名称【" + modelParam.getCrmStageName() + "】已被占用");
                            errorVOS.add(vo);
                            continue;
                        }
                    }
                    mapSystemStageAllData.put(modelParam.getCrmStageId(),modelParam.getCrmStageName());
                }
                RpaSystemProjectEntityParam entityParam = new RpaSystemProjectEntityParam();
                entityParam.setCrmProjectId(modelParam.getCrmProjectId());
                entityParam.setCrmStageId(modelParam.getCrmStageId());
                entityParam.setCrmStageName(modelParam.getCrmStageName());
                entityParam.setName(modelParam.getName());
                entityParam.setCrmCustomerNameShort(modelParam.getCrmCustomerNameShort());
                list.add(entityParam);
            }
            systemProjectRegistry.batchSave(list);
        } else {
            //如果数据库中所有系统项目数据不为空
            Map<String, SystemProjectEntity> idToEntityMap = new HashMap<>();
            Map<String, String> nameToIdMap = new HashMap<>();
//            Set<String> setName = new HashSet<>();
            //遍历组合成map
            for (SystemProjectEntity entity : systemProjectEntityIPage.getRecords()) {
                idToEntityMap.put(entity.getCrmProjectId(), entity);
                nameToIdMap.put(entity.getName(),entity.getCrmProjectId());
            }
            List<RpaSystemProjectEntityParam> list = new ArrayList<>();
            //遍历rpa传过来的数据
            for (RpaSystemProjectModelParam modelParam : params) {

                if(modelParam.getCrmStageId() != null && modelParam.getCrmStageName() != null){
                    if(mapSystemStageAllData.containsValue(modelParam.getCrmStageName())){
                        if(mapSystemStageAllData.get(modelParam.getCrmStageId())==null || !mapSystemStageAllData.get(modelParam.getCrmStageId()).equalsIgnoreCase(modelParam.getCrmStageName())){
                            ReqSynSystemProjectErrorVO vo = new ReqSynSystemProjectErrorVO();
                            vo.setName(modelParam.getName());
                            vo.setCrmProjectId(modelParam.getCrmProjectId());
                            vo.setCrmStageId(modelParam.getCrmStageId());
                            vo.setCrmStageName(modelParam.getCrmStageName());
                            vo.setDesc("阶段名称【" + modelParam.getCrmStageName() + "】已被占用");
                            errorVOS.add(vo);
                            continue;
                        }
                    }
                    mapSystemStageAllData.put(modelParam.getCrmStageId(),modelParam.getCrmStageName());
                }

                //如果传来的projectId可以在map中找到key
                if (idToEntityMap.containsKey(modelParam.getCrmProjectId())) {
                    //如果crmProjectId相同但是项目名字不一致
                    if(!modelParam.getName().equalsIgnoreCase(idToEntityMap.get(modelParam.getCrmProjectId()).getName())){
                        //判断是否和其他已经存在的项目名同名
                        if (nameToIdMap.containsKey(modelParam.getName()) && !(nameToIdMap.get(modelParam.getName()).equalsIgnoreCase(modelParam.getCrmProjectId()))) {
                            ReqSynSystemProjectErrorVO vo = new ReqSynSystemProjectErrorVO();
                            vo.setName(modelParam.getName());
                            vo.setCrmProjectId(modelParam.getCrmProjectId());
                            vo.setCrmStageId(modelParam.getCrmStageId());
                            vo.setCrmStageName(modelParam.getCrmStageName());
                            vo.setDesc("项目名【" + modelParam.getName() + "】重复");
                            errorVOS.add(vo);
                        } else {
                            //更新 系统项目
                            SystemProjectEntity entity = new SystemProjectEntity();
                            entity.setName(modelParam.getName());
                            entity.setCrmProjectId(modelParam.getCrmProjectId());
                            entity.setCrmStageId(modelParam.getCrmStageId());
                            entity.setCrmStageName(modelParam.getCrmStageName());
                            entity.setCrmCustomerNameShort(modelParam.getCrmCustomerNameShort());
                            systemProjectRegistry.editProjectTemplateByCrmId(entity);
                            //同步修改 项目表 name列值
                            ProjectEntity projectEntity = new ProjectEntity();
                            projectEntity.setSystemProjectId(idToEntityMap.get(modelParam.getCrmProjectId()).getId());
                            projectEntity.setName(modelParam.getName());
                            projectRegistry.updateProjectByProjectId(projectEntity);


                            //记录邮件更新内容
                            strDetail = strDetail + " 同步了 【"+idToEntityMap.get(modelParam.getCrmProjectId()).getName()+"】-【"+idToEntityMap.get(modelParam.getCrmProjectId()).getCrmStageName()+"】变更为【"+modelParam.getName()+"】-【"+modelParam.getCrmStageName()+"】;"+"<br/>";


                            //同步缓存
                            nameToIdMap.remove(idToEntityMap.get(modelParam.getCrmProjectId()).getName());
                            nameToIdMap.put(modelParam.getName(), modelParam.getCrmProjectId());
                            //同步缓存
                            idToEntityMap.put(modelParam.getCrmProjectId(), entity);
                        }
                    }else {
                        if(modelParam.getCrmStageId() != null && !modelParam.getCrmStageId().equals(idToEntityMap.get(modelParam.getCrmProjectId()).getCrmStageId())){
                            //更新 系统项目
                            SystemProjectEntity entity = new SystemProjectEntity();
                            entity.setName(modelParam.getName());
                            entity.setCrmProjectId(modelParam.getCrmProjectId());
                            entity.setCrmStageId(modelParam.getCrmStageId());
                            entity.setCrmCustomerNameShort(modelParam.getCrmCustomerNameShort());
                            systemProjectRegistry.editProjectTemplateByCrmId(entity);

                            //记录邮件更新内容
                            strDetail = strDetail + " 同步了 【"+idToEntityMap.get(modelParam.getCrmProjectId()).getName()+"】-【"+idToEntityMap.get(modelParam.getCrmProjectId()).getCrmStageName()+"】变更为【"+modelParam.getName()+"】-【"+modelParam.getCrmStageName()+"】;"+"<br/>";

                            //更新缓存
                            idToEntityMap.put(modelParam.getCrmProjectId(), entity);
                        }
                    }

                } else {
                    //如果传来的projectId在map中找不到key
                    //添加
                    //先向list中添加数据
                    if (nameToIdMap.containsKey(modelParam.getName())) {
                        if(nameToIdMap.get(modelParam.getName())!=null){

                            ReqSynSystemProjectErrorVO vo = new ReqSynSystemProjectErrorVO();
                            vo.setName(modelParam.getName());
                            vo.setCrmProjectId(modelParam.getCrmProjectId());
                            vo.setCrmStageId(modelParam.getCrmStageId());
                            vo.setCrmStageName(modelParam.getCrmStageName());
                            vo.setDesc("项目名【" + modelParam.getName() + "】重复");
                            errorVOS.add(vo);
                        }else{
                            //更新 系统项目
                            SystemProjectEntity entity = new SystemProjectEntity();
                            entity.setName(modelParam.getName());
                            entity.setCrmProjectId(modelParam.getCrmProjectId());
                            entity.setCrmStageId(modelParam.getCrmStageId());
                            entity.setCrmCustomerNameShort(modelParam.getCrmCustomerNameShort());
                            systemProjectRegistry.editProjectTemplateByName(entity);

                            SystemProjectEntity systemProjectEntity = systemProjectRegistry.queryProjectByName(modelParam.getName());
                            //记录邮件更新内容
                            strDetail = strDetail + " 同步了 【"+modelParam.getName()+"】-【"+systemProjectEntity.getCrmStageName()+"】变更为【"+modelParam.getName()+"】-【"+modelParam.getCrmStageName()+"】;"+"<br/>";

                            //同步缓存
                            idToEntityMap.put(modelParam.getCrmProjectId(), entity);
                            nameToIdMap.put(modelParam.getName(),modelParam.getCrmProjectId());

                        }
                    } else {
                        RpaSystemProjectEntityParam entityParam = new RpaSystemProjectEntityParam();
                        entityParam.setCrmProjectId(modelParam.getCrmProjectId());
                        entityParam.setCrmStageId(modelParam.getCrmStageId());
                        entityParam.setCrmStageName(modelParam.getCrmStageName());
                        entityParam.setName(modelParam.getName());
                        entityParam.setCrmCustomerNameShort(modelParam.getCrmCustomerNameShort());
                        list.add(entityParam);
                    }
                }

            }
            //如果传来的projectId在map中找不到key，如果list中有数据，则批量添加
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)) {
                try{
                    //记录邮件更新内容
                    for (RpaSystemProjectEntityParam modelParam : list) {
                        strDetail = strDetail + " 新增了 【"+modelParam.getName()+"】-【"+modelParam.getCrmStageName()+"】;"+"</br>";
                    }
                    //新增
                    systemProjectRegistry.batchSave(list);
                }catch (Exception e){
                    ReqSynSystemProjectErrorVO vo = new ReqSynSystemProjectErrorVO();
                    vo.setDesc(e.getMessage());
                    errorVOS.add(vo);
                }

            }
        }
        if(!mapSystemStageAllData.isEmpty()){
            List<SystemStageEntity> collect = mapSystemStageAllData.entrySet().stream().map(item -> new SystemStageEntity(item.getKey(), item.getValue())).collect(Collectors.toList());
            try {
                systemProjectRegistry.batchSaveSystemStage(collect);
                log.info(collect.toString());
            }catch (Exception e){
                ReqSynSystemProjectErrorVO vo = new ReqSynSystemProjectErrorVO();
                vo.setDesc(e.getMessage());
                errorVOS.add(vo);
            }

        }
        Map<String,Object> resMap = new HashMap<>();
        resMap.put("strDetail",strDetail);
        resMap.put("resList",errorVOS);
        return resMap;
    }

    @Override
    public List<SystemProjectModel> findAllProjectTemplate() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<SystemProjectModel> modelList = new ArrayList<>();
        List<SystemProjectEntity> allProjectTemplate = systemProjectRegistry.findAllProjectTemplate();
        if (!CollectionUtils.isEmpty(allProjectTemplate)) {
            for (SystemProjectEntity entity : allProjectTemplate) {
                modelList.add((SystemProjectModel) EntityUtils.fillModelWithEntity(entity));
            }
        }
        return modelList;
    }

    @Override
    public Integer queryProjectTemplateCountByName(String name) {
        return systemProjectRegistry.queryProjectTemplateCountByName(name);
    }

    @Override
    public void addProjectTemplate(SystemProjectModel model) throws Exception {
        SystemProjectEntity entity = (SystemProjectEntity) EntityUtils.convertModelToEntity(model);
        entity.setEnable(false);
        systemProjectRegistry.addProjectTemplate(entity);
    }

    @Override
    public void editProjectTemplate(SystemProjectModel model) throws Exception {
        SystemProjectEntity entity = (SystemProjectEntity) EntityUtils.convertModelToEntity(model);
        systemProjectRegistry.editProjectTemplate(entity);
    }

    @Override
    @Transactional
    public void editProjectTemplateByCrmId(SystemProjectModel model) throws Exception {
        SystemProjectEntity entity = (SystemProjectEntity) EntityUtils.convertModelToEntity(model);
        systemProjectRegistry.editProjectTemplateByCrmId(entity);
        SystemProjectEntity entity1 = systemProjectRegistry.queryProjectTemplateByCrmId(entity);
        if (entity1 == null) {
            throw new BaseServiceException(ServiceErrorCodeEnum.ENTITY_ERROR);
        }
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setSystemProjectId(entity1.getId());
        projectEntity.setName(model.getName());
        projectRegistry.updateProjectByProjectId(projectEntity);
    }

    @Override
    public void delProjectTemplate(Integer id) {
        systemProjectRegistry.delProjectTemplate(id);
    }

    @Override
    public void updateProjectTemplate(Integer id, Integer areaId) {
        systemProjectRegistry.updateProjectTemplate(id, areaId);
    }

    @Override
    public BasePagesDomain<SystemProjectModel> querySystemProjectPaging(QuerySystemProjectPagingModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        QuerySystemProjectPagingEntityParam entityParam = new QuerySystemProjectPagingEntityParam();
        entityParam.setSystemProjectName(param.getSystemProjectName());
        entityParam.setCurrentPage(param.getCurrentPage());
        entityParam.setCrmProjectId(param.getCrmProjectId());
        entityParam.setCrmStageId(param.getCrmStageId());
        entityParam.setAreaId(param.getAreaId());
        entityParam.setSearchVal(param.getSearchVal());
        entityParam.setPageSize(param.getPageSize());
        entityParam.setEnable(param.getEnable());
        IPage<SystemProjectEntity> systemProjectEntityIPage = systemProjectRegistry.querySystemProjectPaging(entityParam);
        List<SystemProjectModel> systemProjectModels = new LinkedList<>();
        if (systemProjectEntityIPage != null && com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(systemProjectEntityIPage.getRecords())) {
            for (SystemProjectEntity entity : systemProjectEntityIPage.getRecords()) {
                systemProjectModels.add((SystemProjectModel) EntityUtils.fillModelWithEntity(entity));
            }

        }
        BasePagesDomain<SystemProjectModel> pageInfo = new BasePagesDomain(systemProjectEntityIPage);
        pageInfo.setTotalList(systemProjectModels);
        return pageInfo;
    }

    @Override
    public SystemProjectModel queryProjectById(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        return (SystemProjectModel) EntityUtils.fillModelWithEntity(systemProjectRegistry.queryProjectById(id));
    }

    @Override
    public void changeForceDescFlag(Integer id) {
        systemProjectRegistry.changeForceDescFlag(id);
    }

    @Override
    public void editGoalHours(Integer id, Double goalHours) {
        systemProjectRegistry.editGoalHours(id,goalHours);
    }
}
