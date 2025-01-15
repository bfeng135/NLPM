package com.nl.pm.server.controller;

import com.nl.pm.server.common.DateUtils;
import com.nl.pm.server.common.enums.RemindEmailEnum;
import com.nl.pm.server.controller.vo.ReqAddSystemJobVO;
import com.nl.pm.server.controller.vo.ResSystemJobVO;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.service.ISystemJobService;
import com.nl.pm.server.service.ISystemJob;
import com.nl.pm.server.service.model.SystemJobModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Api(value = "定时任务",tags = {"定时任务管理"})
@RequestMapping("/systemJob")
public class JobController {
    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private ISystemJobService systemJobService;
    @Autowired
    private ISystemJob systemJob;

    @ApiOperation("查看")
    @RequestMapping(method = RequestMethod.GET, value = "/getSystemJob")
    public List<ResSystemJobVO> getSystemJob() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ResSystemJobVO> list = new ArrayList<>();
        List<SystemJobModel> systemJob = systemJobService.getSystemJob();
        if(systemJob.size() > 0){
            for(SystemJobModel model:systemJob){
                ResSystemJobVO vo = new ResSystemJobVO();
                vo.setId(model.getId());
                vo.setName(model.getName());
                vo.setType(model.getType());
                vo.setCronExpression(model.getCronExpression());
                vo.setRemark(model.getRemark());
                vo.setEnable(model.getEnable());
                vo.setCreateTime(DateUtils.convertDateToLong(model.getCreateTime()));
                vo.setUpdateTime(DateUtils.convertDateToLong(model.getUpdateTime()));
                list.add(vo);
            }
        }
        return list;
    }

    @ApiOperation("添加")
    @PostMapping("/addSystemJob")
    public void addSystemJob(@RequestBody ReqAddSystemJobVO vo) throws Exception {
        if(StringUtils.isEmpty(vo.getType()) || StringUtils.isEmpty(vo.getCronExpression())){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_INFORMATION_ERROR);
        }

        if(!RemindEmailEnum.checkEmailType(vo.getType())){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
        }
        SystemJobModel systemJobModel = systemJobService.querySystemJobByType(vo.getType());
        if(systemJobModel != null){
            throw new BaseServiceException(ServiceErrorCodeEnum.ENTITY_EXIST);
        }

        SystemJobModel model = new SystemJobModel();
        model.setName(vo.getName());
        model.setType(vo.getType());
        model.setCronExpression(vo.getCronExpression());
        model.setRemark(vo.getRemark());
        model.setCreateTime(new Date());
        systemJobService.addSystemJob(model);
    }

    @ApiOperation("修改")
    @PostMapping("/editSystemJob")
    public void editSystemJob(@RequestBody ReqAddSystemJobVO vo) throws Exception {
        if(StringUtils.isEmpty(vo.getType()) || StringUtils.isEmpty(vo.getCronExpression())){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_INFORMATION_ERROR);
        }
        if(!RemindEmailEnum.checkEmailType(vo.getType())){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
        }
        //查询任务是否开启，开启，先关闭
        SystemJobModel systemJobModel = systemJobService.querySystemJobById(vo.getId());
        if(systemJobModel == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.ENTITY_ERROR);
        }
        if(systemJobModel.getType() != vo.getType()){
            throw new BaseServiceException(ServiceErrorCodeEnum.DATA_TYPE_ERROR);
        }
        if(systemJobModel.getEnable()){
            throw new BaseServiceException(ServiceErrorCodeEnum.JOB_START_STATE_EDIT_ERROR);
        }

        SystemJobModel model = new SystemJobModel();
        model.setId(vo.getId());
        model.setName(vo.getName());
        model.setType(vo.getType());
        model.setCronExpression(vo.getCronExpression());
        model.setRemark(vo.getRemark());
        model.setUpdateTime(new Date());
        //修改
        systemJobService.editSystemJob(model);
    }

    //开始和关闭两个定时任务

    @ApiOperation("开启或关闭")
    @GetMapping("/systemJob/{jobId}")
    private void systemJob(@PathVariable Integer jobId) throws Exception {
        systemJob.systemJob(jobId);
    }
}
