package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.pm.server.registry.entity.AreaEntity;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.registry.param.AreaPageableQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AreaDao extends BaseMapper<AreaEntity> {
    List<AreaEntity> getAllAreaByIds(@Param("areaId")Integer[] areaId);
    Integer queryCountManager(@Param("userId")Integer userId);
    void updateAreaByAreaId(@Param("areaId") Integer areaId);
    AreaEntity queryAreaByUserId(@Param("areaId") Integer areaId,@Param("userId") Integer userId);

    /**
     * 获取区域详情
     * @param areaId
     * @return
     */
    AreaEntity getAreaById(@Param("areaId") Integer areaId);

    /**
     * 获取区域列表
     * @param param
     * @return
     */
    List<AreaEntity> getAreaList(@Param("param") AreaPageableQueryParam param);

    /**
     * 查询区域总数
     * @param param
     * @return
     */
    long getAreaTotalByParams(@Param("param") AreaPageableQueryParam param);

    /**
     * 新增区域
     * @param areaEntity
     * @return
     */
    Integer addArea(@Param("areaEntity") AreaEntity areaEntity);

    /**
     * 编辑区域
     * @param areaEntity
     */
    void updateArea(@Param("areaEntity") AreaEntity areaEntity);

    void updateAreaStatus(@Param("areaId")Integer areaId,@Param("status")Boolean status);

    /**
     * 删除区域
     * @param areaId
     */
    void deleteArea(@Param("areaId") Integer areaId);

    Integer checkAreaDayReport(@Param("areaId") Integer areaId);

    Integer checkAreaOtherProject(@Param("areaId") Integer areaId,@Param("nameList") List<String> nameList);

    Integer checkAreaOtherUser(@Param("areaId") Integer areaId);

    Integer checkAreaName(@Param("name") String name);

    void assignUserToArea(@Param("areaId")Integer areaId,@Param("userIdList") List<Integer> userIdList);

    List<UserEntity> searchOtherAreaAssUser(@Param("areaId")Integer areaId);

    List<AreaEntity> projectNameASSAreaList(@Param("projectName")String projectName);
}
