USE `nl_pm_server`;

ALTER TABLE `nl_pm_server`.`area`
ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1:启用，2:禁用' AFTER `name`;
