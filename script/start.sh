#!/bin/bash

# 配置区域（请根据实际修改）
APP_NAME="YaYa-SaaS-Plus" # 应用名称
JAR_PATH="/home/hs/api/yaya-saas-plus-1.0.0.jar"  # JAR 文件路径
LOG_DIR="/home/hs/api/$APP_NAME"  # 日志目录
# JAVA_OPTS="-Xms512m -Xmx1024m"  # JVM 参数（可选）

# 确保日志目录存在
mkdir -p "$LOG_DIR"

# 检查 JAR 文件是否存在
if [ ! -f "$JAR_PATH" ]; then
  echo "错误：JAR 文件不存在 ($JAR_PATH)"
  exit 1
fi

# 启动应用（后台运行 + 日志重定向）
# nohup java $JAVA_OPTS -jar "$JAR_PATH" > "$LOG_DIR/out.log" 2>&1 &
nohup java -jar "$JAR_PATH" --spring.profiles.active=prod > "$LOG_DIR/out.log" 2>&1 &

# 获取进程ID并保存
PID=$!
echo "应用启动成功 (PID: $PID)"
echo "日志输出: $LOG_DIR/out.log"


# 修复sh文件命令:   sed -i 's/\r$//' start.sh
