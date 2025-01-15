package com.nl.pm.server.registry;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.entity.*;
import com.nl.pm.server.registry.param.DayReportSearchEntityParam;
import com.nl.pm.server.registry.param.ResEveryWorkTotalTimeEntityParam;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IDayReportRegistry {
    /**
     * 根据项目和区域、时间查询日报
     * @return
     */
    List<Date> queryReportByProjectIdAndAreaId(String startTime, String endTime, Integer projectId, Integer areaId);

    /**
     * 根据时间、项目、区域，查询每个人工作时间
     */
    List<Map<UserEntity,Double>> queryReportTimeByTimeStrTest(String timeStr, Integer projectId, Integer areaId);

    /**
     * 根据时间、项目、区域，查询每个人工作时间
     */
    List<UserEntity> queryReportTimeByTimeStr(String timeStr, Integer projectId, Integer areaId);

    @Transactional
    void createDayReport(DayReportAdvanceEntity entity) throws BaseServiceException;

    void updateDayReport(DayReportAdvanceEntity entity) throws BaseServiceException;

    DayReportAdvanceEntity fetchDayReportDetail(Integer id) throws Exception;

    DayReportEntity checkDayReportExist(String dateStr, Integer userId);

    IPage<DayReportAdvanceEntity> searchDayReport(DayReportSearchEntityParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    void deleteDayReport(Integer id) throws Exception;

    /**
     * 单项目去重查询时间
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectName
     * @return
     */
    List<Date> queryTimeDistinctByTime(String startTime, String endTime,Integer[] areaId,String projectName);

    /**
     * 查询每天工作时长
     * @param startTime
     * @param endTime
     * @param areaId
     * @param projectName
     * @return
     */
    List<ResEveryWorkTotalTimeEntityParam> queryEveryDateWorkTotalTime(String startTime, String endTime, Integer[] areaId, String projectName);

    List<DayReportEntity> searchDayReportByLimitUser(List<Integer> limitUser,Date startDate,Date endDate,Integer userId);

    Integer queryCountDraftDayReportByUserId(Integer userId);

    DraftDayReportEntity queryDraftDayReport(Integer userId);

    DraftDayExchangeEntity queryDraftDayExchange(Integer userId);

    List<DraftDayReportTaskEntity> queryDraftDayReportTask(Integer userId);

    void delDraft(Integer userId);

    void saveDraft(DraftDayReportEntity draftDayReportEntity,List<DraftDayReportTaskEntity> draftDayReportTaskEntities,DraftDayExchangeEntity draftDayExchangeEntity);

}
