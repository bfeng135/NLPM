package com.nl.pm.server.service;


import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.AreaModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.AreaSearchParam;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface IAreaService {
    List<AreaModel> getAllAreaByIds(Integer[] areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    void updateAreaByAreaId(Integer areaId);
    AreaModel queryAreaByUserId(Integer areaId ,Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    void updateAreaById(AreaModel areaModel) throws Exception;

    /**
     * 获取区域详情
     * @param areaId
     * @return
     */
    AreaModel getAreaInfo(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 获取区域列表
     * @param areaSearchParam
     * @return
     */
    BasePagesDomain<AreaModel> getAreaList(AreaSearchParam areaSearchParam) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    /**
     * 添加区域
     * @param areaModel
     * @return
     */
    Integer addArea(AreaModel areaModel) throws Exception;

    /**
     * 编辑区域
     * @param areaModel
     */
    void updateArea(AreaModel areaModel) throws BaseServiceException;

    /**
     * 编辑区域
     * @param
     */
    void updateAreaStatus(Integer areaId,Boolean status) throws BaseServiceException;

    /**
     * 删除区域
     * @param areaId
     */
    void deleteArea(Integer areaId);

    Integer checkAreaDayReport(Integer areaId);

    Integer checkAreaOtherProject(Integer areaId);

    Integer checkAreaOtherUser(Integer areaId);

    void assignUserToArea(Integer areaId, List<Integer> userIdList);

    List<UserModel> searchOtherAreaAssUser(Integer areaId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<AreaModel> projectNameASSAreaList(String projectName) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

}
