# ============================================================
# YaYa-SaaS-Plus 敏感信息脱敏脚本 (PowerShell)
#
# 用法:
#   .\script\desensitize.ps1            # 脱敏:就地替换已知敏感值(幂等)
#   .\script\desensitize.ps1 -Check     # 仅检查:发现敏感信息返回非0(用于 pre-push / CI)
#
# 目标文件: src/main/resources/application-dev.yml, application-prod.yml
# 说明: 真实密钥/密码/服务器地址一律通过环境变量注入(见yml文件头部注释),
#       本脚本用于兜底,防止真实敏感值被提交到仓库。
# ============================================================
param([switch]$Check)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot

$targets = @(
    (Join-Path $root 'src\main\resources\application-dev.yml'),
    (Join-Path $root 'src\main\resources\application-prod.yml')
)

# 已知敏感值(正则) -> 脱敏占位符
$map = [ordered]@{
    '106\.14\.27\.178'                     = '127.0.0.1'  # 生产服务器地址
    'xxx123'                               = 'xxxxx'      # 生产数据库/Redis密码
    'D:/code/yaya-saas-plus'               = 'D:/yaya-saas-plus'  # 本机绝对路径(匹配 d:/code 小写)
    'sk-[A-Za-z0-9]{20,}'                  = 'sk-xxxxx'   # 通用 API Key
}

$found = $false
foreach ($file in $targets) {
    if (-not (Test-Path $file)) { continue }
    $content = [System.IO.File]::ReadAllText($file)
    $changed = $false
    foreach ($pattern in $map.Keys) {
        if ($content -match $pattern) {
            Write-Host "[脱敏] 发现敏感信息: $pattern -> $($map[$pattern]) ($file)"
            $found = $true
            if (-not $Check) {
                $content = $content -replace $pattern, $map[$pattern]
                $changed = $true
            }
        }
    }
    if ($changed) {
        [System.IO.File]::WriteAllText($file, $content, (New-Object System.Text.UTF8Encoding($false)))
    }
}

if ($found -and $Check) {
    Write-Error '[脱敏] 检测到敏感信息,请先运行: .\script\desensitize.ps1'
    exit 1
}

if ($found) {
    Write-Host '[脱敏] 已替换敏感值,请检查差异后重新 git add'
}

exit 0