#!/bin/sh
# ============================================================
# YaYa-SaaS-Plus 敏感信息脱敏脚本 (POSIX sh,兼容 Git Bash / Linux)
#
# 用法:
#   sh script/desensitize.sh           # 脱敏:就地替换已知敏感值(幂等)
#   sh script/desensitize.sh --check   # 仅检查:发现敏感信息返回非0(用于 pre-push / CI)
#
# 目标文件: src/main/resources/application-dev.yml, application-prod.yml
# 说明: 真实密钥/密码/服务器地址一律通过环境变量注入(见yml文件头部注释),
#       本脚本用于兜底,防止真实敏感值被提交到仓库。
# ============================================================
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
CHECK_ONLY="${1:-}"

# 已知敏感值(正则)|脱敏占位符
set -- \
  '106\.14\.27\.178|127.0.0.1' \
  'xxx123|xxxxx' \
  'D:/code/yaya-saas-plus|D:/yaya-saas-plus' \
  'd:/code/yaya-saas-plus|D:/yaya-saas-plus' \
  'sk-[A-Za-z0-9]{20,}|sk-xxxxx'

found=0
for file in "$ROOT_DIR/src/main/resources/application-dev.yml" "$ROOT_DIR/src/main/resources/application-prod.yml"; do
  [ -f "$file" ] || continue
  for pair in "$@"; do
    pattern=${pair%%|*}
    repl=${pair#*|}
    if grep -qE "$pattern" "$file"; then
      echo "[脱敏] 发现敏感信息: $pattern -> $repl ($file)"
      found=1
      if [ "$CHECK_ONLY" != "--check" ]; then
        sed -i -E "s|$pattern|$repl|g" "$file"
      fi
    fi
  done
done

if [ "$found" -eq 1 ] && [ "$CHECK_ONLY" = "--check" ]; then
  echo "[脱敏] 检测到敏感信息,请先运行: sh script/desensitize.sh" >&2
  exit 1
fi

if [ "$found" -eq 1 ]; then
  echo "[脱敏] 已替换敏感值,请检查差异后重新 git add"
fi

exit 0