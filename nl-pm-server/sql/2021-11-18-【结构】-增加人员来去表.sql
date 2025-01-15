USE `nl_pm_server`;

CREATE TABLE `nl_pm_server`.`user_come_leave` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '来去表id',
  `user_id` INT NOT NULL COMMENT '用户id',
`come_date` date DEFAULT NULL COMMENT '入职时间',
  `leave_date` date DEFAULT NULL COMMENT '离开时间',
  PRIMARY KEY (`id`))
COMMENT = '用户来去（在职-离开）时间统计表';


insert into `nl_pm_server`.`user_come_leave`(`user_id`,`come_date`,`leave_date`)
select `id`,`create_time`,(case when `status` = 1 then null  else `update_time` end) from `user`;
