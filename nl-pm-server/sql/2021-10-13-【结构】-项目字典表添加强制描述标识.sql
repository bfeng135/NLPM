USE `nl_pm_server`;

ALTER TABLE `nl_pm_server`.`system_project`
ADD COLUMN `force_desc_flag` TINYINT NOT NULL DEFAULT 0 COMMENT '强制日报描述标识（1:强制，0:不强制）' AFTER `enable`;
