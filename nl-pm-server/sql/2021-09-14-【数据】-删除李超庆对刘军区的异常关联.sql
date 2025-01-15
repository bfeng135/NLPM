USE `nl_pm_server`;
DELETE FROM nl_pm_server.`project_user`
WHERE
    `id` IN (SELECT
        tempTable.`id`
    FROM
        (SELECT 
            `id`
        FROM
            nl_pm_server.`project_user`
        
        WHERE
            `user_id` = 33
            AND `project_id` IN (SELECT
                `id`
            FROM
                nl_pm_server.`project`
            
            WHERE
                `area_id` = 5)) AS tempTable);
