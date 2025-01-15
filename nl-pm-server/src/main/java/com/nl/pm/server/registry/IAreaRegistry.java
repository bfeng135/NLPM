package com.nl.pm.server.registry;

import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.AreaPageableQueryParam;

import java.util.List;

public interface IAreaRegistry {
    List<AreaEntity> getAllAreaByIds(Integer[] areaId);
    Integer queryCountManager(Integer userId);
    void updateAreaByAreaId(Integer areaId);
    AreaEntity queryAreaByUserId(Integer areaId,Integer userId);
    void updateAreaById(AreaEntity areaEntity);

    /**
     * 通过id获取区域详情
     * @param areaId
     * @return
     */
    AreaEntity getAreaById(Integer areaId);

    /**
     * 查询区域列表
     * @param areaPageableQueryParam
     * @return
     */
    BasePagesDomain<AreaEntity> getAreaList(AreaPageableQueryParam areaPageableQueryParam);

    /**
     * 新增区域
     * @param areaEntity
     * @return
     */
    Integer addArea(AreaEntity areaEntity);

    /**
     * 编辑区域
     * @param areaEntity
     */
    void updateArea(AreaEntity areaEntity);

    void updateAreaStatus(Integer areaId,Boolean status);

    /**
     * 删除区域
     * @param areaId
     */
    void deleteArea(Integer areaId);

    Integer checkAreaDayReport(Integer areaId);

    Integer checkAreaOtherProject(Integer areaId);

    Integer checkAreaOtherUser(Integer areaId);

    Integer checkAreaName(String name);

    void assignUserToArea(Integer areaId, List<Integer> userIdList);

    List<UserEntity> searchOtherAreaAssUser(Integer areaId);

    List<AreaEntity> projectNameASSAreaList(String projectName);
}
