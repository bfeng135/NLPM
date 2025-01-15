USE `nl_pm_server`;
INSERT INTO `system_job`
    (`id`, `name`, `type`, `cron_expression`, `remark`, `enable`)
    VALUES
    ('3', '定时同步CRM项目提醒', 'CRM_PROJECT_SYNC_EMAIL', '0 0 8 * * ?', '每天同步项目提醒', '1');

ALTER TABLE `system_project`
ADD COLUMN `crm_customer_name_short` VARCHAR(200) NULL COMMENT '客户简称' AFTER `crm_stage_id`;
