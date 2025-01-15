package com.nl.pm.server.registry.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nl.pm.server.registry.IHolidayRegistry;
import com.nl.pm.server.registry.dao.HolidayDao;
import com.nl.pm.server.registry.entity.HolidayEntity;
import com.nl.pm.server.registry.param.DelHolidayEntityParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class HolidayRegistryImpl implements IHolidayRegistry {
    @Autowired
    private HolidayDao holidayDao;

    @Override
    public List<HolidayEntity> findAllHoliday() {
        QueryWrapper<HolidayEntity> queryWrapper = new QueryWrapper<>();
        return holidayDao.selectList(queryWrapper);
    }

    @Override
    public HolidayEntity queryHolidayByDate(String date) {
        return holidayDao.queryHolidayByDate(date);
    }

    @Override
    public List<HolidayEntity> queryHolidayByWeek(String date1, String date2) {
        return holidayDao.queryHolidayByWeek(date1,date2);
    }

    @Override
    public List<HolidayEntity> searchHolidayByDate(String startDate, String endDate) {
        return holidayDao.searchHolidayByDate(startDate,endDate);
    }

    @Override
    public void save(HolidayEntity entity) {
        holidayDao.insert(entity);
    }

    @Override
    public void delHoliday(List<Integer> ids) {
        for(Integer id :ids){
            holidayDao.deleteById(id);
        }
    }

    @Override
    public boolean checkHoliday(Date date) {
        int count = holidayDao.checkHoliday(date);
        if(count > 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Integer delHolidayByDateStr(List<DelHolidayEntityParam> delHolidayEntityParams) {
        int count = 0;
        for (DelHolidayEntityParam entityParam:delHolidayEntityParams){
            Integer integer = holidayDao.delHolidayByDateStr(entityParam.getData());
            count += integer;
        }
        return count;
    }
}
