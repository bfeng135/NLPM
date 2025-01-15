USE `nl_pm_server`;

ALTER TABLE `nl_pm_server`.`project`
ADD COLUMN `system_project_id` INT NULL COMMENT '关联系统项目词典的id，用于解决和crm同步的问题' AFTER `enable`;

ALTER TABLE `nl_pm_server`.`system_project`
ADD COLUMN `crm_project_id` VARCHAR(1000) NULL COMMENT 'Crm系统对应项目id，该字段由rpa传入，且存入后不可更改' AFTER `area_id`,
ADD COLUMN `crm_stage_id` INT NULL COMMENT 'Crm状态阶段ID,该字段由rpa传入，且存入后不可更改' AFTER `crm_project_id`;

ALTER TABLE `nl_pm_server`.`system_project`
ADD UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE;

set SQL_SAFE_UPDATES = 0;
update `project` as p set p.system_project_id = case when (select sp.id from system_project as sp where sp.name = p.name) is null then -1 else (select sp.id from system_project as sp where sp.name = p.name) end;
set SQL_SAFE_UPDATES = 1;

CREATE TABLE `nl_pm_server`.`system_stage` (
  `crm_stage_id` INT NOT NULL,
  `crm_stage_name` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`crm_stage_id`),
  UNIQUE INDEX `crm_stage_name_UNIQUE` (`crm_stage_name` ASC) VISIBLE,
  UNIQUE INDEX `crm_stage_id_UNIQUE` (`crm_stage_id` ASC) VISIBLE);
