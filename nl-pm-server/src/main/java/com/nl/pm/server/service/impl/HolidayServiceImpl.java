package com.nl.pm.server.service.impl;


import com.nl.pm.server.common.EntityUtils;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.registry.IHolidayRegistry;
import com.nl.pm.server.registry.entity.HolidayEntity;
import com.nl.pm.server.registry.param.DelHolidayEntityParam;
import com.nl.pm.server.service.IHolidayService;
import com.nl.pm.server.service.model.HolidayModel;
import com.nl.pm.server.service.param.DelHolidayModelParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class HolidayServiceImpl implements IHolidayService {

    @Autowired
    private IHolidayRegistry holidayRegistry;


    @Override
    public List<HolidayModel> findAllHoliday() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<HolidayModel> modelList = new ArrayList<>();
        List<HolidayEntity> holidayEntities = holidayRegistry.findAllHoliday();
        if(holidayEntities.size() > 0){
            for(HolidayEntity holidayEntity :holidayEntities){
                modelList.add((HolidayModel)EntityUtils.fillModelWithEntity(holidayEntity));
            }
        }
        return modelList;
    }

    @Override
    public List<HolidayModel> searchHolidayByDate(String startDate, String endDate) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<HolidayEntity> holidayEntities = holidayRegistry.searchHolidayByDate(startDate, endDate);
        List<HolidayModel> lists = new ArrayList<>();
        if(!CollectionUtils.isEmpty(holidayEntities)){
            for (HolidayEntity holidayEntity : holidayEntities) {
                HolidayModel holidayModel = (HolidayModel) EntityUtils.fillModelWithEntity(holidayEntity);
                lists.add(holidayModel);
            }
        }
        return  lists;
    }

    @Override
    public void save(List<HolidayModel> list) throws Exception {
        List<HolidayEntity> holidayEntities = new ArrayList<>();
        for(HolidayModel model:list){
            HolidayEntity entity = (HolidayEntity)EntityUtils.convertModelToEntity(model);
            holidayEntities.add(entity);
        }
        if(holidayEntities.size() > 0){
            for(HolidayEntity entity:holidayEntities){
                holidayRegistry.save(entity);
            }
        }
    }

    @Override
    public void delHoliday(List<Integer> ids) {
        holidayRegistry.delHoliday(ids);
    }

    @Override
    public boolean checkHoliday(Date date) {
      return  holidayRegistry.checkHoliday(date);

    }

    @Override
    public Integer delHolidayByDateStr(List<DelHolidayModelParam> params) {
        List<DelHolidayEntityParam> delHolidayEntityParams = new ArrayList<>();
        for(DelHolidayModelParam param:params){
            DelHolidayEntityParam entityParam = new DelHolidayEntityParam();
            entityParam.setId(param.getId());
            entityParam.setData(param.getData());
            delHolidayEntityParams.add(entityParam);
        }
        return holidayRegistry.delHolidayByDateStr(delHolidayEntityParams);
    }
}
