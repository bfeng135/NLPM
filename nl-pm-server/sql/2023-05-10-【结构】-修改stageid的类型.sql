ALTER TABLE nl_pm_server.system_stage MODIFY COLUMN crm_stage_id varchar(32) NOT NULL;


ALTER TABLE nl_pm_server.system_project MODIFY COLUMN crm_stage_id varchar(32) NULL COMMENT 'Crm状态阶段ID,该字段由rpa传入，且存入后不可更改';
