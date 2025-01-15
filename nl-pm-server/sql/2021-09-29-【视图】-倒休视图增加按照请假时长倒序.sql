USE `nl_pm_server`;
CREATE
     OR REPLACE ALGORITHM = UNDEFINED
    DEFINER = `admin`@`%`
    SQL SECURITY DEFINER
VIEW `user_exchange_hour` AS
    SELECT
        `exchange_table`.`user_id` AS `user_id`,
        `exchange_table`.`nickname` AS `nickname`,
        `exchange_table`.`area_id` AS `area_id`,
        `exchange_table`.`area_name` AS `area_name`,
        `exchange_table`.`over_hour` AS `over_hour`,
        `exchange_table`.`leave_hour` AS `leave_hour`,
        `exchange_table`.`exchange_hour` AS `exchange_hour`,
        `exchange_table`.`work_hour` AS `work_hour`
    FROM
        (SELECT
            `detail3`.`user_id` AS `user_id`,
                `u`.`nickname` AS `nickname`,
                `a`.`id` AS `area_id`,
                `a`.`name` AS `area_name`,
                `detail3`.`leave_hour` AS `leave_hour`,
                `detail3`.`over_hour` AS `over_hour`,
                `detail3`.`exchange_hour` AS `exchange_hour`,
                `detail3`.`work_hour` AS `work_hour`
        FROM
            (((SELECT
            `detail2`.`user_id` AS `user_id`,
                SUM(`detail2`.`leave_hour`) AS `leave_hour`,
                SUM(`detail2`.`work_hour`) AS `work_hour`,
                SUM(`detail2`.`over_hour`) AS `over_hour`,
                SUM((`detail2`.`over_hour` - `detail2`.`leave_hour`)) AS `exchange_hour`
        FROM
            (SELECT
            `detail`.`day_report_id` AS `day_report_id`,
                `detail`.`user_id` AS `user_id`,
                `detail`.`leave_hour` AS `leave_hour`,
                `detail`.`work_hour` AS `work_hour`,
                (CASE
                    WHEN
                        ((SELECT
                                COUNT(`holiday`.`id`)
                            FROM
                                `holiday`
                            WHERE
                                (`holiday`.`date_ymd` = `detail`.`date`)) > 0)
                    THEN
                        `detail`.`work_hour`
                    ELSE (CASE
                        WHEN (((`detail`.`leave_hour` + `detail`.`work_hour`) - 8.0) > 0) THEN ((`detail`.`leave_hour` + `detail`.`work_hour`) - 8.0)
                        ELSE 0.0
                    END)
                END) AS `over_hour`
        FROM
            (SELECT
            `dr`.`id` AS `day_report_id`,
                `dr`.`user_id` AS `user_id`,
                `dr`.`date` AS `date`,
                MAX((CASE
                    WHEN (`de`.`leave_hour` IS NULL) THEN 0.0
                    ELSE `de`.`leave_hour`
                END)) AS `leave_hour`,
                SUM((CASE
                    WHEN (`drt`.`hours` IS NULL) THEN 0.0
                    ELSE `drt`.`hours`
                END)) AS `work_hour`
        FROM
            ((`day_report` `dr`
        LEFT JOIN `day_exchange` `de` ON ((`de`.`day_report_id` = `dr`.`id`)))
        LEFT JOIN `day_report_task` `drt` ON ((`drt`.`day_report_id` = `dr`.`id`)))
        GROUP BY `dr`.`id` , `dr`.`user_id` , `dr`.`date`) `detail`) `detail2`
        GROUP BY `detail2`.`user_id`) `detail3`
        LEFT JOIN `user` `u` ON ((`u`.`id` = `detail3`.`user_id`)))
        LEFT JOIN `area` `a` ON ((`a`.`id` = `u`.`area_id`)))) `exchange_table`
    ORDER BY `exchange_table`.`area_id` DESC , `exchange_table`.`leave_hour` DESC, `exchange_table`.`nickname`;
