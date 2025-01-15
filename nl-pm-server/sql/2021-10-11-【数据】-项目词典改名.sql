USE `nl_pm_server`;

##SELECT * FROM nl_pm_server.project where `name` = '邢台机械轧辊有限公司80MN油压机远程运维系统';
update nl_pm_server.project set `name` = '邢台机械轧辊有限公司AI检测项目'
where id =152;


##SELECT id FROM nl_pm_server.system_project where name = '邢台机械轧辊有限公司80MN油压机远程运维系统';
update nl_pm_server.system_project set `name` = '邢台机械轧辊有限公司AI检测项目'
where id =82;