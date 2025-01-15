/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nl.pm.server.registry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nl.pm.server.registry.entity.OperationLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface LogDao extends BaseMapper<OperationLogEntity> {
    /**
     * insert systemLog
     * @param log log
     */
    void addSystemLog(@Param("log") OperationLogEntity log);

    /**
     * query log paging
     * @param page page plugin
     * @param module module
     * @param operate operate
     * @param userId userId
     * @param areaId areaId
     * @param start start
     * @param end end
     * @param searchVal search value
     * @return log paging
     */
    IPage<OperationLogEntity> querySystemLogPaging(Page<OperationLogEntity> page, @Param("module") String module, @Param("operate") String operate, @Param("userId") Integer userId, @Param("areaId") Integer areaId, @Param("start") Date start, @Param("end") Date end, @Param("searchVal") String searchVal);

}
