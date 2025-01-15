package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.registry.entity.*;
import com.nl.pm.server.registry.param.DayReportSearchEntityParam;
import com.nl.pm.server.registry.param.ResEveryWorkTotalTimeEntityParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface DayReportDao {
    /**
     * 根据项目和区域、时间查询日报
     * @return
     */
    List<Date> queryReportByProjectIdAndAreaId(@Param("startTime") String startTime, @Param("endTime")String endTime,
                                               @Param("projectId")Integer projectId, @Param("areaId")Integer areaId);

    /**
     * 根据时间、项目、区域，查询每个人工作时间
     */
    List<Map<UserEntity,Double>> queryReportTimeByTimeStrTest(@Param("timeStr")String timeStr, @Param("projectId")Integer projectId,
                                                          @Param("areaId")Integer areaId);


    /**
     * 根据时间、项目、区域，查询每个人工作时间
     */
    List<UserEntity> queryReportTimeByTimeStr(@Param("timeStr")String timeStr, @Param("projectId")Integer projectId,
                                                              @Param("areaId")Integer areaId);


    void createDayReport(@Param("entity")DayReportAdvanceEntity entity);
    void updateDayReport(@Param("entity")DayReportAdvanceEntity entity);
    void createDayReportTask(@Param("entity")DayReportAdvanceEntity entity);
    void createDayReportExchange(@Param("entity")DayReportAdvanceEntity entity);

    DayReportEntity fetchDayReport(@Param("id") Integer id);
    DayExchangeEntity fetchDayReportExchange(@Param("id") Integer id);
    List<DayReportTaskEntity> fetchDayReportTask(@Param("id") Integer id);

    DayReportEntity checkDayReportExist(@Param("date") String date,@Param("userId") Integer userId);

    IPage<DayReportEntity> searchDayReport(Page<DayReportEntity> page, @Param("param")DayReportSearchEntityParam param);
    List<DayExchangeEntity> searchDayReportExchange(@Param("dayReportIdList") List<Integer> dayReportIdList);
    List<DayReportTaskEntity> searchDayReportTask(@Param("dayReportIdList") List<Integer> dayReportIdList,@Param("currentUserId") Integer currentUserId,@Param("roleCode") String roleCode);

    void deleteDayReport(@Param("id") Integer id);

    /**
     * 单项目去重查询时间
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectName
     * @return
     */
    List<Date> queryTimeDistinctByTime(@Param("startTime") String startTime,@Param("endTime") String endTime,
                                       @Param("areaId")Integer[] areaId,@Param("projectName")String projectName);

    /**
     * 查询每天工作时长
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectName
     * @return
     */
    List<ResEveryWorkTotalTimeEntityParam> queryEveryDateWorkTotalTime(@Param("startTime") String startTime,@Param("endTime") String endTime,
                                                                       @Param("areaId")Integer[] areaId,@Param("projectName")String projectName);

    List<DayReportEntity> searchDayReportByLimitUser(@Param("limitUser")List<Integer> limitUser,@Param("startDate") Date startDate,@Param("endDate") Date endDate,@Param("userId") Integer userId);

    Integer queryCountDraftDayReportByUserId(@Param("userId")Integer userId);

    DraftDayReportEntity queryDraftDayReport(@Param("userId")Integer userId);

    DraftDayExchangeEntity queryDraftDayExchange(@Param("userId")Integer userId);

    List<DraftDayReportTaskEntity> queryDraftDayReportTask(@Param("userId")Integer userId);

    void delDraft(@Param("userId")Integer userId);

    void saveDraft(@Param("draftDayReportEntity") DraftDayReportEntity draftDayReportEntity,
                   @Param("draftDayReportTaskEntities") List<DraftDayReportTaskEntity> draftDayReportTaskEntities,
                   @Param("draftDayExchangeEntity") DraftDayExchangeEntity draftDayExchangeEntity);

}
