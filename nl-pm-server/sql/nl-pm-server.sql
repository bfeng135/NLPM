drop database if exists `nl_pm_server`;
CREATE SCHEMA `nl_pm_server` DEFAULT CHARACTER SET utf8mb4 ;

USE `nl_pm_server`;
-- MySQL dump 10.13  Distrib 8.0.26, for macos11 (x86_64)
--
-- Host: 127.0.0.1    Database: nl_pm_server
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `area`
--

DROP TABLE IF EXISTS `area`;

CREATE TABLE `area` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '区域 id',
  `name` varchar(100) NOT NULL COMMENT '区域名称',
  `desc` varchar(2000) DEFAULT NULL COMMENT '区域描述',
  `manager_id` int(11) DEFAULT NULL COMMENT '区域负责人id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='区域主表';

/*Data for the table `area` */

insert  into `area`(`id`,`name`,`desc`,`manager_id`,`create_time`,`update_time`) values
(1,'公司管理层大区','系统管理层',2,'2021-08-06 12:56:17','2021-08-06 12:56:17');

/*Table structure for table `area_user_ass` */

DROP TABLE IF EXISTS `area_user_ass`;

CREATE TABLE `area_user_ass` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '区域相关人员关系表ID',
  `area_id` int(11) NOT NULL COMMENT '区域 id',
  `user_id` int(11) NOT NULL COMMENT '用户 id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `area_user_UNIQUE` (`area_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='区域相关人员关系表';

/*Data for the table `area_user_ass` */

/*Table structure for table `day_exchange` */

DROP TABLE IF EXISTS `day_exchange`;

CREATE TABLE `day_exchange` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '调休 id',
  `day_report_id` int(11) NOT NULL COMMENT '关联日报 id',
  `leave_hour` double NOT NULL COMMENT '请假时长',
  `desc` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `day_report_id_UNIQUE` (`day_report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调休表';

/*Data for the table `day_exchange` */

/*Table structure for table `day_report` */

DROP TABLE IF EXISTS `day_report`;

CREATE TABLE `day_report` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日报主表 id',
  `date` date NOT NULL COMMENT '日期(yyyy-mm-dd)',
  `user_id` int(11) NOT NULL COMMENT '员工id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `date_user_id_UNIQUE` (`date`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='日报主表';

/*Data for the table `day_report` */

/*Table structure for table `day_report_task` */

DROP TABLE IF EXISTS `day_report_task`;

CREATE TABLE `day_report_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日报任务子表 id',
  `day_report_id` int(11) NOT NULL COMMENT '日报主表 id',
  `project_id` int(11) NOT NULL COMMENT '项目 id',
  `hours` double NOT NULL COMMENT '工时',
  `desc` varchar(2000) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `main_id_project_id_UNIQUE` (`day_report_id`,`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='日报任务子表';

/*Data for the table `day_report_task` */

/*Table structure for table `holiday` */

DROP TABLE IF EXISTS `holiday`;

CREATE TABLE `holiday` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '假日表 id',
  `year` int(11) NOT NULL COMMENT '年',
  `month` int(11) NOT NULL COMMENT '月',
  `day` int(11) NOT NULL COMMENT '日',
  `date_ymd` date NOT NULL COMMENT '日期(yyyy-mm-dd)',
  `date_str` varchar(45) NOT NULL COMMENT '日期字符串（yyyy-mm-dd）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `date_UNIQUE` (`date_ymd`),
  UNIQUE KEY `date_str_UNIQUE` (`date_str`),
  UNIQUE KEY `y-m-d_UNIQUE` (`year`,`month`,`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='假日表';

/*Data for the table `holiday` */

/*Table structure for table `menu` */

insert  into `holiday`(`id`,`year`,`month`,`day`,`date_ymd`,`date_str`,`create_time`,`update_time`) values
(1,2021,8,1,'2021-08-01','2021-08-01','2021-08-16 08:51:04','2021-08-21 13:36:53'),
(2,2021,8,7,'2021-08-07','2021-08-07','2021-08-16 08:51:24','2021-08-21 13:36:56'),
(3,2021,8,8,'2021-08-08','2021-08-08','2021-08-18 15:17:10','2021-08-21 13:36:59'),
(4,2021,8,14,'2021-08-14','2021-08-14','2021-08-21 13:37:42','2021-08-21 13:37:42'),
(5,2021,8,15,'2021-08-15','2021-08-15','2021-08-21 13:38:01','2021-08-21 13:38:01'),
(6,2021,8,21,'2021-08-21','2021-08-21','2021-08-21 13:38:40','2021-08-21 13:39:13'),
(7,2021,8,22,'2021-08-22','2021-08-22','2021-08-21 13:39:09','2021-08-21 13:39:09'),
(8,2021,8,28,'2021-08-28','2021-08-28','2021-08-21 13:42:54','2021-08-21 13:42:54'),
(9,2021,8,29,'2021-08-29','2021-08-29','2021-08-21 13:43:34','2021-08-21 13:43:34'),
(10,2021,9,4,'2021-09-04','2021-09-04','2021-08-21 13:43:56','2021-08-21 13:43:56'),
(11,2021,9,5,'2021-09-05','2021-09-05','2021-08-21 13:44:16','2021-08-21 13:44:16'),
(12,2021,9,11,'2021-09-11','2021-09-11','2021-08-21 13:44:41','2021-08-21 13:44:41'),
(13,2021,9,12,'2021-09-12','2021-09-12','2021-08-21 13:45:05','2021-08-21 13:45:05'),
(14,2021,9,19,'2021-09-19','2021-09-19','2021-08-21 13:45:40','2021-08-21 13:45:40'),
(15,2021,9,20,'2021-09-20','2021-09-20','2021-08-21 13:46:01','2021-08-21 13:46:01'),
(16,2021,9,21,'2021-09-21','2021-09-21','2021-08-21 13:46:23','2021-08-21 13:46:23'),
(17,2021,9,25,'2021-09-25','2021-09-25','2021-08-21 13:47:07','2021-08-21 13:47:07'),
(18,2021,10,1,'2021-10-01','2021-10-01','2021-08-21 13:47:33','2021-08-21 13:47:33'),
(19,2021,10,2,'2021-10-02','2021-10-02','2021-08-21 13:47:51','2021-08-21 13:47:51'),
(20,2021,10,3,'2021-10-03','2021-10-03','2021-08-21 13:48:10','2021-08-21 13:48:10'),
(21,2021,10,4,'2021-10-04','2021-10-04','2021-08-21 13:48:27','2021-08-21 13:48:27'),
(22,2021,10,5,'2021-10-05','2021-10-05','2021-08-21 13:48:47','2021-08-21 13:48:47'),
(23,2021,10,6,'2021-10-06','2021-10-06','2021-08-21 13:49:17','2021-08-21 13:49:17'),
(24,2021,10,7,'2021-10-07','2021-10-07','2021-08-21 13:49:42','2021-08-21 13:49:42');

DROP TABLE IF EXISTS `menu`;

CREATE TABLE `menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单 id',
  `code` varchar(100) NOT NULL COMMENT '菜单编码',
  `name` varchar(100) NOT NULL COMMENT '菜单名称',
  `parent_code` varchar(100) DEFAULT NULL COMMENT '父级菜单编码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜单表';

/*Data for the table `menu` */

/*Table structure for table `operation_log` */

DROP TABLE IF EXISTS `operation_log`;

CREATE TABLE `operation_log` (
  `id` varchar(100) NOT NULL COMMENT 'log id',
  `module` varchar(100) DEFAULT NULL COMMENT 'model',
  `operate` varchar(100) DEFAULT NULL COMMENT 'operate: ADD,UPDATE,DELETE,QUERY',
  `content` text COMMENT 'log content',
  `user_id` int(11) DEFAULT NULL COMMENT 'user id',
  `username` varchar(100) DEFAULT NULL COMMENT 'user name',
  `area_id` int(11) DEFAULT NULL COMMENT 'tenant id',
  `ip` varchar(100) DEFAULT NULL COMMENT 'ip address',
  `operate_time` datetime DEFAULT NULL COMMENT 'operate time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `operation_log` */

insert  into `operation_log`(`id`,`module`,`operate`,`content`,`user_id`,`username`,`area_id`,`ip`,`operate_time`) values
('025db843-8dea-41b6-8580-d4eb5da1a3b6',NULL,NULL,'{\"areaId\":1,\"username\":\"superadmin\",\"password\":\"123456\"}',NULL,'superadmin',1,'0:0:0:0:0:0:0:1','2021-08-07 15:26:58');

/*Table structure for table `project` */

DROP TABLE IF EXISTS `project`;

CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '项目 id',
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `desc` varchar(2000) DEFAULT NULL COMMENT '描述',
  `area_id` int(11) NOT NULL COMMENT '区域 id',
  `manager_id` int(11) COMMENT '项目负责人 id',
  `enable` tinyint(4) NOT NULL DEFAULT '1' COMMENT '启用标识：1、启用，0、禁用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_area_UNIQUE` (`name`,`area_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='项目主表';

/*Data for the table `project` */

insert  into `project`(`id`,`name`,`desc`,`area_id`,`manager_id`,`enable`,`create_time`,`update_time`) values
(1,'公司培训',NULL,1,2,1,'2021-08-07 16:15:12','2021-08-07 16:15:12'),
(2,'公司会议',NULL,1,2,1,'2021-08-07 16:15:12','2021-08-07 16:15:12'),
(3,'公司展会支持',NULL,1,2,1,'2021-08-07 16:15:12','2021-08-07 16:15:12'),
(4,'公司其他活动',NULL,1,2,1,'2021-08-07 16:15:12','2021-08-07 16:15:12'),
(5,'学习提升',NULL,1,2,1,'2021-08-07 16:15:12','2021-08-07 16:15:12'),
(6,'公司集团内项目',NULL,1,2,1,'2021-08-07 16:15:12','2021-08-07 16:15:12'),
(7,'公司销售支持',NULL,1,2,1,'2021-08-07 16:15:12','2021-08-07 16:15:12');

/*Table structure for table `project_user` */

DROP TABLE IF EXISTS `project_user`;

CREATE TABLE `project_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '项目附属员工表 id',
  `project_id` int(11) NOT NULL COMMENT '项目 id',
  `user_id` int(11) NOT NULL COMMENT '员工 id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `project_id_user_id_UNIQUE` (`project_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='项目附属员工表';

/*Data for the table `project_user` */

insert  into `project_user`(`id`,`project_id`,`user_id`,`create_time`,`update_time`) values
(1,1,2,'2021-08-07 16:16:56','2021-08-07 16:16:56'),
(2,2,2,'2021-08-07 16:16:56','2021-08-07 16:16:56'),
(3,3,2,'2021-08-07 16:16:56','2021-08-07 16:16:56'),
(4,4,2,'2021-08-07 16:16:56','2021-08-07 16:16:56');

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色 id',
  `code` varchar(100) NOT NULL COMMENT '角色编码',
  `name` varchar(100) NOT NULL COMMENT '角色名称',
  `desc` varchar(2000) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='角色主表';

/*Data for the table `role` */

insert  into `role`(`id`,`code`,`name`,`desc`,`create_time`,`update_time`) values
(1,'SUPER_ADMIN','超级管理员',NULL,'2021-08-07 16:08:22','2021-08-07 16:08:22'),
(2,'AREA_MANAGER','区长',NULL,'2021-08-07 16:08:22','2021-08-07 16:08:22'),
(3,'GROUP_MANAGER','组长',NULL,'2021-08-07 16:08:22','2021-08-07 16:08:22'),
(4,'EMPLOYEE','普通员工',NULL,'2021-08-07 16:08:22','2021-08-07 16:08:22'),
(5,'FINANCE','财务',NULL,'2021-08-07 16:08:22','2021-08-07 16:08:22'),
(6,'HR','人事',NULL,'2021-08-07 16:08:22','2021-08-07 16:08:22'),
(7,'MANAGEMENT','行政',NULL,'2021-08-07 16:08:22','2021-08-07 16:08:22');

/*Table structure for table `system_info` */

DROP TABLE IF EXISTS `system_info`;

CREATE TABLE `system_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '系统信息 id',
  `host` varchar(50) NOT NULL COMMENT '邮件服务器地址',
  `name` varchar(100) NOT NULL COMMENT '系统名称',
  `passwrod` varchar(200) NOT NULL COMMENT '邮箱密码',
  `email` varchar(100) NOT NULL COMMENT '系统电子邮件',
  `desc` varchar(2000) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='系统信息表';

/*Data for the table `system_info` */

insert  into `system_info`(`id`,`host`,`name`,`passwrod`,`email`,`desc`,`create_time`,`update_time`) values
(1,'smtp.163.com','北光项目工时管理系统','ZKTAJMJUCCLOCBXX','yjpingfan@163.com',NULL,'2021-08-07 08:00:30','2021-08-13 01:01:04');

/*Table structure for table `user` */






DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户 id',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(100) NOT NULL COMMENT '用户昵称',
  `role_id` int(11) NOT NULL COMMENT '角色 id',
  `area_id` int(11) NOT NULL COMMENT '区域 id',
  `email` varchar(100) NOT NULL COMMENT '电子邮箱',
  `phone` varchar(100) DEFAULT NULL COMMENT '电话',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：1、在职 ，0、离职',
  `email_notice` tinyint(4) NOT NULL DEFAULT '1' COMMENT '邮件通知：1、开启，0、关闭',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `token` varchar(4000) DEFAULT '' COMMENT 'Token',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `username_tenant_id_unq` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='用户主表';

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`nickname`,`role_id`,`area_id`,`email`,`phone`,`status`,`email_notice`,`create_time`,`update_time`,`token`) values
(1,'superadmin','e10adc3949ba59abbe56e057f20f883e','超级管理员',1,1,'\'\'',NULL,1,1,'2021-08-05 22:47:21','2021-08-16 08:25:27','eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MjkxNTk5MjcsInVzZXIiOiJ7XCJpZFwiOjEsXCJ1c2VybmFtZVwiOlwic3VwZXJhZG1pblwiLFwicGFzc3dvcmRcIjpcImUxMGFkYzM5NDliYTU5YWJiZTU2ZTA1N2YyMGY4ODNlXCIsXCJuaWNrbmFtZVwiOlwi6LaF57qn566h55CG5ZGYXCIsXCJhcmVhSWRcIjoxfSJ9.CkXGLPbY-oqdr3WebwW00M8DePBb5Tf5OuVQaMM6tCw'),
(2,'nladmin','e10adc3949ba59abbe56e057f20f883e','北光管理区区长',2,1,'\'\'',NULL,1,1,'2021-08-07 16:12:20','2021-08-07 16:12:20','');



DROP TABLE IF EXISTS `system_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `type` varchar(50) NOT NULL COMMENT '类型',
  `cron_expression` varchar(50) NOT NULL COMMENT '表达式',
  `remark` varchar(50) DEFAULT NULL COMMENT '备注',
  `enable` tinyint(4) NOT NULL DEFAULT '0' COMMENT '启用标识：1、启用，0、关闭',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_job`
--

LOCK TABLES `system_job` WRITE;
/*!40000 ALTER TABLE `system_job` DISABLE KEYS */;

INSERT INTO `system_job` VALUES
(1,'每天日报提醒','DAY_EMAIL','0 0 9 * * ?','每天日报提醒',1,'2021-08-20 01:59:18','2021-08-25 08:59:21'),
(2,'每周日报提醒','WEEK_EMAIL','0 0 10 ? * 1','每周日报提醒',1,'2021-08-20 01:59:58','2021-08-25 08:59:21');

/*!40000 ALTER TABLE `system_job` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;


DROP TABLE IF EXISTS `system_project`;

CREATE TABLE `system_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `enable` tinyint(4) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `def1` varchar(20) DEFAULT NULL,
  `def2` varchar(20) DEFAULT NULL,
  `def3` varchar(20) DEFAULT NULL,
  `def4` varchar(20) DEFAULT NULL,
  `def5` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `system_email` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `host` varchar(200) NOT NULL,
  `username` varchar(200) NOT NULL,
  `password` varchar(200) NOT NULL,
  `send_num` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `system_email` */

insert  into `system_email`(`id`,`host`,`username`,`password`,`send_num`) values
(1,'smtp.ym.163.com','dayreportsystem@nltd.com.cn','Taylor2008',0),
(2,'smtp.ym.163.com','dayreportsystem1@nltd.com.cn','Taylor2008',0),
(3,'smtp.ym.163.com','dayreportsystem2@nltd.com.cn','Taylor2008',0),
(4,'smtp.ym.163.com','dayreportsystem3@nltd.com.cn','Taylor2008',0);



DROP VIEW IF EXISTS `nl_pm_server`.`user_exchange_hour` ;
USE `nl_pm_server`;
CREATE
     OR REPLACE ALGORITHM = UNDEFINED
    DEFINER = `admin`@`%`
    SQL SECURITY DEFINER
VIEW `user_exchange_hour` AS
    SELECT
        `exchange_table`.`user_id` AS `user_id`,
        `exchange_table`.`nickname` AS `nickname`,
        `exchange_table`.`area_id` AS `area_id`,
        `exchange_table`.`area_name` AS `area_name`,
        `exchange_table`.`over_hour` AS `over_hour`,
        `exchange_table`.`leave_hour` AS `leave_hour`,
        `exchange_table`.`exchange_hour` AS `exchange_hour`,
        `exchange_table`.`work_hour` AS `work_hour`
    FROM
        (SELECT
            `detail3`.`user_id` AS `user_id`,
                `u`.`nickname` AS `nickname`,
                `a`.`id` AS `area_id`,
                `a`.`name` AS `area_name`,
                `detail3`.`leave_hour` AS `leave_hour`,
                `detail3`.`over_hour` AS `over_hour`,
                `detail3`.`exchange_hour` AS `exchange_hour`,
                `detail3`.`work_hour` AS `work_hour`
        FROM
            (((SELECT
            `detail2`.`user_id` AS `user_id`,
                SUM(`detail2`.`leave_hour`) AS `leave_hour`,
                SUM(`detail2`.`work_hour`) AS `work_hour`,
                SUM(`detail2`.`over_hour`) AS `over_hour`,
                SUM((`detail2`.`over_hour` - `detail2`.`leave_hour`)) AS `exchange_hour`
        FROM
            (SELECT
            `detail`.`day_report_id` AS `day_report_id`,
                `detail`.`user_id` AS `user_id`,
                `detail`.`leave_hour` AS `leave_hour`,
                `detail`.`work_hour` AS `work_hour`,
                (CASE
                    WHEN
                        ((SELECT
                                COUNT(`holiday`.`id`)
                            FROM
                                `holiday`
                            WHERE
                                (`holiday`.`date_ymd` = `detail`.`date`)) > 0)
                    THEN
                        `detail`.`work_hour`
                    ELSE (CASE
                        WHEN (((`detail`.`leave_hour` + `detail`.`work_hour`) - 8.0) > 0) THEN ((`detail`.`leave_hour` + `detail`.`work_hour`) - 8.0)
                        ELSE 0.0
                    END)
                END) AS `over_hour`
        FROM
            (SELECT
            `dr`.`id` AS `day_report_id`,
                `dr`.`user_id` AS `user_id`,
                `dr`.`date` AS `date`,
                MAX((CASE
                    WHEN (`de`.`leave_hour` IS NULL) THEN 0.0
                    ELSE `de`.`leave_hour`
                END)) AS `leave_hour`,
                SUM((CASE
                    WHEN (`drt`.`hours` IS NULL) THEN 0.0
                    ELSE `drt`.`hours`
                END)) AS `work_hour`
        FROM
            ((`day_report` `dr`
        LEFT JOIN `day_exchange` `de` ON ((`de`.`day_report_id` = `dr`.`id`)))
        LEFT JOIN `day_report_task` `drt` ON ((`drt`.`day_report_id` = `dr`.`id`)))
        GROUP BY `dr`.`id` , `dr`.`user_id` , `dr`.`date`) `detail`) `detail2`
        GROUP BY `detail2`.`user_id`) `detail3`
        LEFT JOIN `user` `u` ON ((`u`.`id` = `detail3`.`user_id`)))
        LEFT JOIN `area` `a` ON ((`a`.`id` = `u`.`area_id`)))) `exchange_table`
    ORDER BY `exchange_table`.`area_id` DESC , `exchange_table`.`nickname`;






/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


