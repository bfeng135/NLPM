#!/bin/sh
echo "开始备份当日数据到backup文件夹"
nl_pm_everyday_name=$(date +%Y-%m-%d-data-backup.sql)
mysqldump -uadmin -padmin nl_pm_server > ../backup/$nl_pm_everyday_name

echo "备份完毕"
echo 按任意键关闭窗口
read -n 1
