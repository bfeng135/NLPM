#!/bin/sh
PID=$(cat ../log/nl-pm-server.pid)
kill -9 $PID
echo "服务已停止！"
echo "PID为：" $PID
echo "按任意键退出"
read -n 1
