use `nl_pm_server`;


/*Table structure for table `draft_day_exchange` */

DROP TABLE IF EXISTS `draft_day_exchange`;

CREATE TABLE `draft_day_exchange` (
  `user_id` int(11) NOT NULL COMMENT '关联草稿人员 id',
  `leave_hour` double DEFAULT NULL COMMENT '请假时长',
  `desc` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='草稿调休表';

/*Data for the table `draft_day_exchange` */

/*Table structure for table `draft_day_report` */

DROP TABLE IF EXISTS `draft_day_report`;

CREATE TABLE `draft_day_report` (
  `user_id` int(11) NOT NULL COMMENT '员工id',
  `date` date DEFAULT NULL COMMENT '日期(yyyy-mm-dd)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='草稿日报主表';

/*Data for the table `draft_day_report` */

/*Table structure for table `draft_day_report_task` */

DROP TABLE IF EXISTS `draft_day_report_task`;

CREATE TABLE `draft_day_report_task` (
  `user_id` int(11) NOT NULL COMMENT '草稿日报主表用户 id',
  `project_id` int(11) DEFAULT NULL COMMENT '项目 id',
  `hours` double DEFAULT NULL COMMENT '工时',
  `desc` varchar(2000) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  UNIQUE KEY `main_id_project_id_UNIQUE` (`user_id`,`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='草稿日报任务子表';

/*Data for the table `draft_day_report_task` */
