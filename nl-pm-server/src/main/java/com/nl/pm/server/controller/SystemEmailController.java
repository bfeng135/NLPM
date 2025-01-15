package com.nl.pm.server.controller;

import com.nl.pm.server.controller.vo.ReqAddSystemEmailVO;
import com.nl.pm.server.controller.vo.ReqUpdateSystemEmailVO;
import com.nl.pm.server.controller.vo.ResSystemEmailVO;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.exception.errorEnum.ServiceErrorCodeEnum;
import com.nl.pm.server.service.ISystemEmailService;
import com.nl.pm.server.service.model.SystemEmailModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pf
 * @version 1.0
 * @date 2021/8/27 9:55
 */
@RestController
@Api(value = "系统邮箱管理",tags = {"系统邮箱管理"})
@RequestMapping("/systemEmail")
public class SystemEmailController {
    private static final Logger log = LoggerFactory.getLogger(SystemEmailController.class);

    @Autowired
    private ISystemEmailService systemEmailService;

    @ApiOperation(value = "查询所有系统邮箱",notes = "查询所有系统邮箱接口")
    @GetMapping("/getAll")
    public List<ResSystemEmailVO> findAll() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        List<ResSystemEmailVO> list = new ArrayList<>();
        List<SystemEmailModel> all = systemEmailService.findAll();
        if(CollectionUtils.isNotEmpty(all)){
            for (SystemEmailModel model :all){
                ResSystemEmailVO vo = new ResSystemEmailVO();
                vo.setId(model.getId());
                vo.setHost(model.getHost());
                vo.setUsername(model.getUsername());
                vo.setPassword(model.getPassword());
                vo.setSendNum(model.getSendNum());
                list.add(vo);
            }
        }
        return list;
    }

    @ApiOperation(value = "添加系统邮箱",notes = "添加系统邮箱接口")
    @PostMapping("/add")
    public void add(@RequestBody List<ReqAddSystemEmailVO> vos) throws Exception {
        List<SystemEmailModel> modelList = new ArrayList<>();
        if(CollectionUtils.isEmpty(vos)){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
        }
        for(ReqAddSystemEmailVO vo : vos){
            if(StringUtils.isEmpty(vo.getHost()) || StringUtils.isEmpty(vo.getUsername()) || StringUtils.isEmpty(vo.getPassword())){
                throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
            }
            Integer integer = systemEmailService.querySystemEmailByUserName(vo.getUsername());
            if(integer != null && integer > 0){
                throw new BaseServiceException(ServiceErrorCodeEnum.EMAIL_EXITS_ERROR);
            }
            SystemEmailModel model = new SystemEmailModel();
            model.setHost(vo.getHost());
            model.setUsername(vo.getUsername());
            model.setPassword(vo.getPassword());
            model.setSendNum(0);
            modelList.add(model);
        }
        systemEmailService.add(modelList);
    }

    @ApiOperation(value = "删除邮箱",notes = "删除系统邮箱接口")
    @DeleteMapping("/del/{id}")
    public void del(@PathVariable Integer id){
        systemEmailService.del(id);
    }

    @ApiOperation(value = "更新邮箱",notes = "更新系统邮箱接口")
    @PutMapping("/update")
    public void update(@RequestBody ReqUpdateSystemEmailVO vo) throws Exception {
        SystemEmailModel model = new SystemEmailModel();
        if(vo == null){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
        }
        if(StringUtils.isEmpty(vo.getHost()) || StringUtils.isEmpty(vo.getUsername()) || StringUtils.isEmpty(vo.getPassword())){
            throw new BaseServiceException(ServiceErrorCodeEnum.INPUT_ERROR);
        }
        model.setId(vo.getId());
        model.setUsername(vo.getUsername());
        model.setPassword(vo.getPassword());
        model.setHost(vo.getHost());
        systemEmailService.update(model);
    }
}
