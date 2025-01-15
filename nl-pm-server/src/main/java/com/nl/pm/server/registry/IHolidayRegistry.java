package com.nl.pm.server.registry;

import com.nl.pm.server.registry.entity.HolidayEntity;
import com.nl.pm.server.registry.param.DelHolidayEntityParam;

import java.util.Date;
import java.util.List;

public interface IHolidayRegistry {
    /**
     * 查询所有节日
     */
    List<HolidayEntity> findAllHoliday();

    /**
     * 通过时间查询当前是否休假
     */
    HolidayEntity queryHolidayByDate(String date);

    /**
     * 查询一周内假日
     */
    List<HolidayEntity> queryHolidayByWeek(String date1,String date2);

    List<HolidayEntity> searchHolidayByDate(String startDate,String endDate);

    /**
     * 保存假日
     */
    void save(HolidayEntity entity);

    /**
     * 删除节日
     */
    void delHoliday(List<Integer> ids);

    boolean checkHoliday(Date date);

    Integer delHolidayByDateStr(List<DelHolidayEntityParam> delHolidayEntityParams);
}
