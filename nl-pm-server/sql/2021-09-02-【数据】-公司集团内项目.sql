use `nl_pm_server`;

insert into project (`name`,`area_id`,`manager_id`)
select '公司集团内项目',`id`,`manager_id` from area where `id`  not in
(
SELECT a.`id` FROM area as a
left join project as p on p.`area_id` = a.`id`
where p.`name` ='公司集团内项目'
);

insert into project_user (`project_id`,`user_id`)
select p.`id`,p.`manager_id` from project as p left join project_user as pu on p.`id` = pu.`project_id`
where p.`name` ='公司集团内项目'and pu.`project_id` is null and p.`manager_id` is not null;