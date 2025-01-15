package com.nl.pm.server.controller;

import com.alibaba.fastjson.JSONArray;
import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.SecurityContextUtils;
import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.controller.vo.ReqAddHolidayVO;
import com.nl.pm.server.controller.vo.ReqDelHolidayVO;
import com.nl.pm.server.controller.vo.ReqEditHolidayVO;
import com.nl.pm.server.controller.vo.ResHolidayVO;
import com.nl.pm.server.exception.BaseAuthException;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.AuthErrorCodeEnum;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.service.IHolidayService;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.HolidayModel;
import com.nl.pm.server.service.model.UserModel;
import com.nl.pm.server.service.param.DelHolidayModelParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "节假日管理",tags = {"节假日管理"})
@RequestMapping("/holiday")
public class HolidayController {
    @Autowired
    private IHolidayService holidayService;
    @Autowired
    private IUserService userService;
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @ApiOperation(value = "节日查询",notes = "节日查询接口")
    @GetMapping("/findAllHoliday")
    public String findAllHoliday() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<HolidayModel> holidayModels = holidayService.findAllHoliday();
        return JSONArray.toJSONString(toResHolidayVO(holidayModels));
    }

    @ApiOperation(value = "添加节日查询",notes = "添加节日查询接口")
    @PostMapping("/addHoliday")
    public void addHoliday(@RequestBody List<ReqAddHolidayVO> vo) throws Exception {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(CollectionUtils.isEmpty(vo)){
            throw new BaseServiceException(ServiceErrorCodeEnum.HOLIDAY_INPUT_INFORMATION_ERROR);
        }
        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
            List<HolidayModel> holidayModels = holidayService.findAllHoliday();
            List<String> time = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(holidayModels)){
                for(HolidayModel model:holidayModels){
                    time.add(model.getDateStr());
                }
            }
            if(vo.size() > 0){
                List<HolidayModel> list = new ArrayList<>();
                for (ReqAddHolidayVO reqAddHolidayVO :vo){
                    if(time.contains(reqAddHolidayVO.getDateStr())){
                        throw new BaseServiceException(ServiceErrorCodeEnum.HOLIDAY_EXITS_ERROR);
                    }
                    HolidayModel model = new HolidayModel();

                    String[] split = reqAddHolidayVO.getDateStr().split("-");
                    model.setYear(Integer.valueOf(split[0]));
                    model.setMonth(Integer.valueOf(split[1]));
                    model.setDay(Integer.valueOf(split[2]));
                    model.setDateYmd(DateUtils.convertStrToDate(reqAddHolidayVO.getDateStr()));
                    model.setDateStr(reqAddHolidayVO.getDateStr());
                    list.add(model);
                }
                holidayService.save(list);
            }
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }


//    @ApiOperation(value = "修改节日查询",notes = "修改节日查询接口")
//    @PutMapping("/editHoliday")
//    public void editHoliday(@RequestBody ReqEditHolidayVO vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
//        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
//        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
//                || RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
//
//        }
//        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
//    }

    @ApiOperation(value = "删除节日查询",notes = "删除节日查询接口")
    @DeleteMapping("/delHoliday")
    public void delHoliday(@RequestBody List<ReqDelHolidayVO> vo) throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, BaseAuthException {
        UserModel currentUserModel = userService.queryUserByUserName(securityContextUtils.getCurrentUsername());
        if(RoleTypeEnum.SUPER_ADMIN == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())
                || RoleTypeEnum.MANAGEMENT == RoleTypeEnum.convertToEnum(currentUserModel.getRoleCode())){
//            if(ids.size() > 0){
//                holidayService.delHoliday(ids);
//            }
            if(CollectionUtils.isEmpty(vo)){
                throw new BaseServiceException(ServiceErrorCodeEnum.HOLIDAY_INPUT_INFORMATION_ERROR);
            }
            List<DelHolidayModelParam> params = new ArrayList<>();
            for(ReqDelHolidayVO vo2:vo){
                DelHolidayModelParam param = new DelHolidayModelParam();
                param.setId(vo2.getId());
                param.setData(vo2.getData());
                params.add(param);
            }
            Integer integer = holidayService.delHolidayByDateStr(params);
            if(integer == 0){
                throw new BaseServiceException(ServiceErrorCodeEnum.HOLIDAY_DEL_ERROR);
            }
            return;
        }
        throw new BaseAuthException(AuthErrorCodeEnum.NO_AUTH);
    }


    private List<ResHolidayVO> toResHolidayVO(List<HolidayModel> holidayModels){
        List<ResHolidayVO> list = new ArrayList<>();
        if(holidayModels.size() > 0){
            for (HolidayModel model:holidayModels){
                ResHolidayVO vo = new ResHolidayVO();
                vo.setId(model.getId());
                vo.setYear(model.getYear());
                vo.setMonth(model.getMonth());
                vo.setDay(model.getDay());
                vo.setDateYmd(model.getDateYmd());
                vo.setDateStr(model.getDateStr());
                list.add(vo);
            }
        }
        return list;
    }
}
