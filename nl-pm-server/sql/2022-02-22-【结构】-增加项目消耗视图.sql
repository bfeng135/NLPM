USE `nl_pm_server`;

CREATE
    ALGORITHM = UNDEFINED
    DEFINER = `admin`@`%`
    SQL SECURITY DEFINER
VIEW `area_project_user_cost` AS
    SELECT
        `temp`.`projectId` AS `projectId`,
        `temp`.`projectName` AS `projectName`,
        `temp`.`areaId` AS `areaId`,
        `temp`.`areaName` AS `areaName`,
        `temp`.`mainAreaId` AS `mainAreaId`,
        `nl_pm_server`.`area`.`name` AS `mainAreaName`,
        `temp`.`userId` AS `userId`,
        `temp`.`nickname` AS `nickname`,
        `temp`.`date` AS `date`,
        `temp`.`hours` AS `hours`,
        `temp`.`desc` AS `desc`
    FROM
        (((SELECT
            `p`.`id` AS `projectId`,
                `p`.`name` AS `projectName`,
                `a`.`id` AS `areaId`,
                `a`.`name` AS `areaName`,
                `sp`.`area_id` AS `mainAreaId`,
                `u`.`id` AS `userId`,
                `u`.`nickname` AS `nickname`,
                `dr`.`date` AS `date`,
                `drt`.`hours` AS `hours`,
                `drt`.`desc` AS `desc`
        FROM
            (((((`nl_pm_server`.`day_report` `dr`
        JOIN `nl_pm_server`.`day_report_task` `drt` ON ((`dr`.`id` = `drt`.`day_report_id`)))
        JOIN `nl_pm_server`.`user` `u` ON ((`u`.`id` = `dr`.`user_id`)))
        JOIN `nl_pm_server`.`project` `p` ON ((`drt`.`project_id` = `p`.`id`)))
        JOIN `nl_pm_server`.`area` `a` ON ((`a`.`id` = `p`.`area_id`)))
        JOIN `nl_pm_server`.`system_project` `sp` ON ((`p`.`system_project_id` = `sp`.`id`))))) `temp`
        JOIN `nl_pm_server`.`area` ON ((`temp`.`mainAreaId` = `nl_pm_server`.`area`.`id`)))
    ORDER BY `temp`.`projectName` , `temp`.`areaName` , `temp`.`nickname` , `temp`.`date` DESC