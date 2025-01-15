#!/bin/sh
nohup java -jar -Duser.timezone=GMT+08 ../nl-pm-server-v1.0.jar  >> ../log/nl-pm-server-start.out 2>&1 &
echo $! > ../log/nl-pm-server.pid
echo "服务已启动！"
echo "PID为:" $!
echo 按任意键关闭窗口
read -n 1
