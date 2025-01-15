## 先用swagger 删除  /nl-pm-server/systemProject/delSystemProject
##{
##  "areaId": 9,
##  "id": 106,
##  "name": "邢台机械轧辊有限公司80MN油压机远程运维系统"
##}



USE `nl_pm_server`;

##SELECT id FROM nl_pm_server.project where `name` = '邢台项目';
update nl_pm_server.project set `name` = '邢台机械轧辊有限公司80MN油压机远程运维系统'
where id =152;


##SELECT id FROM nl_pm_server.system_project where name = '邢台项目';
update nl_pm_server.system_project set `name` = '邢台机械轧辊有限公司80MN油压机远程运维系统'
where id =82;


##SELECT id FROM nl_pm_server.project where `name` = '萍乡绝缘子';
update nl_pm_server.project set `name` = '萍乡陶瓷绝缘子'
where id =54;


##SELECT id FROM nl_pm_server.system_project where name = '萍乡绝缘子';
update nl_pm_server.system_project set `name` = '萍乡陶瓷绝缘子'
where id =1;

