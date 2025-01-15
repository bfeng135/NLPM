package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.pm.server.registry.entity.HolidayEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface HolidayDao extends BaseMapper<HolidayEntity> {
    /**
     * 通过时间查询当前是否休假
     */
    HolidayEntity queryHolidayByDate(@Param("date") String date);

    /**
     * 查询一周内假日
     */
    List<HolidayEntity> queryHolidayByWeek(String date1, String date2);

    List<HolidayEntity> searchHolidayByDate(String startDate, String endDate);

    int checkHoliday(@Param("date")Date date);

    Integer delHolidayByDateStr(@Param("date") String date);
}
