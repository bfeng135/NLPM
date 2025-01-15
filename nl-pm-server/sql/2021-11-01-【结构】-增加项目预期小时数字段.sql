USE `nl_pm_server`;

ALTER TABLE `nl_pm_server`.`system_project`
ADD COLUMN `goal_hours` DOUBLE NULL DEFAULT NULL COMMENT '项目完结目标小时数' AFTER `area_id`;
