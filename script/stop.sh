#!/bin/bash

APP_NAME="yaya-saas-plus-1.0.0.jar"

PID=$(ps -ef | grep "$APP_NAME" | grep -v grep | awk '{print $2}')

if [ -z "$PID" ]; then
    echo "$APP_NAME 未运行。"
    exit 0
fi

echo "正在停止 $APP_NAME ..."
kill $PID

# 等待程序退出
for i in {1..10}
do
    if ps -p $PID > /dev/null 2>&1; then
        sleep 1
    else
        echo "停止成功。"
        exit 0
    fi
done

echo "程序未正常退出，执行强制停止..."
kill -9 $PID

echo "停止完成。"
