package com.nl.pm.server.service;

import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.controller.vo.ReqRpaSystemProjectVO;
import com.nl.pm.server.controller.vo.ReqSynSystemProjectErrorVO;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.DynamicServiceException;
import com.nl.pm.server.service.model.SystemProjectModel;
import com.nl.pm.server.service.model.SystemStageModel;
import com.nl.pm.server.service.param.QuerySystemProjectPagingModelParam;
import com.nl.pm.server.service.param.RpaSystemProjectModelParam;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/24 14:56
 */
public interface ISystemProjectService {
    List<SystemStageModel> distinctGetAllStage() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 检查Crm导入的项目是否符合标准并返回符合的项目列表，并记录错误项目信息
     * @param vos
     * @return
     */
    Map<String,Object> checkCrmProject(List<ReqRpaSystemProjectVO> vos) throws DynamicServiceException;

    Map<String,Object> synSystemProject(List<RpaSystemProjectModelParam> params) throws BaseServiceException;
    /**
     * 查询所有项目
     */
    List<SystemProjectModel> findAllProjectTemplate() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    Integer queryProjectTemplateCountByName(String name);

    void addProjectTemplate(SystemProjectModel model) throws Exception;

    void editProjectTemplate(SystemProjectModel model) throws Exception;

    void editProjectTemplateByCrmId(SystemProjectModel model) throws Exception;

    void delProjectTemplate(Integer id);
    void updateProjectTemplate(Integer id,Integer areaId);


    BasePagesDomain<SystemProjectModel>  querySystemProjectPaging(QuerySystemProjectPagingModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    SystemProjectModel queryProjectById(Integer id) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    void changeForceDescFlag(Integer id);

    void editGoalHours(Integer id,Double goalHours);
}
