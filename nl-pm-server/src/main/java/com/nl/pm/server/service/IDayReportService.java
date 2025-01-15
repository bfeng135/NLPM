package com.nl.pm.server.service;


import com.nl.pm.server.common.pages.BasePagesDomain;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.service.model.*;
import com.nl.pm.server.service.param.DayReportSearchModelParam;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IDayReportService {
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

    void createDayReport(DayReportAdvanceModel model) throws Exception;

    void updateDayReport(DayReportAdvanceModel model) throws Exception;

    DayReportAdvanceModel fetchDayReportDetail(Integer id) throws Exception;

    BasePagesDomain<DayReportAdvanceModel> searchDayReport(DayReportSearchModelParam param) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    void deleteDayReport(Integer id) throws Exception;

    List<DayReportModel> searchDayReportByLimitUser(List<Integer> limitUser,Date startDate,Date endDate,Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    Integer queryCountDraftDayReportByUserId(Integer userId);

    DraftDayReportModel queryDraftDayReport(Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    DraftDayExchangeModel queryDraftDayExchange(Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<DraftDayReportTaskModel> queryDraftDayReportTask(Integer userId) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    void delDraft(Integer userId);

    void saveDraft(DraftDayReportModel draftDayReportModel,List<DraftDayReportTaskModel> draftDayReportTaskModels,DraftDayExchangeModel draftDayExchangeModel) throws Exception;

}
