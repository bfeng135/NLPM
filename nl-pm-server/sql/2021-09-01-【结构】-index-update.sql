USE `nl_pm_server`;
ALTER TABLE `nl_pm_server`.`area`
ADD INDEX `manager_index` (`manager_id` ASC),
ADD INDEX `name_index` (`name` ASC),
ADD INDEX `id_index` (`id` ASC);
;
ALTER TABLE `nl_pm_server`.`area_user_ass` 
ADD INDEX `area_user_index` (`area_id` ASC, `user_id` ASC);
;
ALTER TABLE `nl_pm_server`.`user` 
ADD INDEX `role_index` (`role_id` ASC),
ADD INDEX `area_index` (`area_id` ASC),
ADD INDEX `nickname_index` (`nickname` ASC),
ADD INDEX `email_index` (`email` ASC),
ADD INDEX `id_index` (`id` ASC);
;
ALTER TABLE `nl_pm_server`.`role` 
ADD INDEX `code_index` (`code` ASC),
ADD INDEX `name_index` (`name` ASC),
ADD INDEX `id_index` (`id` ASC);
;
ALTER TABLE `nl_pm_server`.`project` 
ADD INDEX `name_index` (`name` ASC),
ADD INDEX `manager_index` (`manager_id` ASC),
ADD INDEX `area_index` (`area_id` ASC),
ADD INDEX `id_index` (`id` ASC);
;
ALTER TABLE `nl_pm_server`.`project_user` 
ADD INDEX `project_index` (`project_id` ASC),
ADD INDEX `user_index` (`user_id` ASC);
;
ALTER TABLE `nl_pm_server`.`holiday` 
ADD INDEX `date_ymd_index` (`date_ymd` ASC),
ADD INDEX `date_str_index` (`date_str` ASC);
;
ALTER TABLE `nl_pm_server`.`day_report` 
ADD INDEX `date_index` (`date` ASC),
ADD INDEX `user_index` (`user_id` ASC);
;
ALTER TABLE `nl_pm_server`.`day_report_task` 
ADD INDEX `report_index` (`day_report_id` ASC),
ADD INDEX `project_index` (`project_id` ASC);
;
ALTER TABLE `nl_pm_server`.`day_exchange` 
ADD INDEX `report_index` (`day_report_id` ASC);
;
ALTER TABLE `nl_pm_server`.`system_project` 
ADD INDEX `name_index` (`name` ASC),
ADD INDEX `enable_index` (`enable` ASC);
;
