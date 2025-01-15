package com.nl.pm.server.service;


import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.service.model.HolidayModel;
import com.nl.pm.server.service.param.DelHolidayModelParam;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

public interface IHolidayService {
    /**
     * 查询所有节日
     */
    List<HolidayModel> findAllHoliday() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;

    List<HolidayModel> searchHolidayByDate(String startDate,String endDate) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
    /**
     * 保存节日
     * @param list
     */
    void save(List<HolidayModel> list) throws Exception;

    /**
     * 删除节日
     */
    void delHoliday(List<Integer> ids);

    boolean checkHoliday(Date date);

    Integer delHolidayByDateStr(List<DelHolidayModelParam> params);
}
