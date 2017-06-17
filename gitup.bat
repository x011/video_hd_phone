@echo off
echo 输入提交备注
set /p commit=

set gitPath="C:\Program Files\Git\cmd\"
::echo 当前盘符和路径：%~dp0
if not exist .git (
 ::echo 不存在
 echo 首次提交输入提交地址
 set /p url=
 %gitPath%git init
 %gitPath%git remote add origin %url%
)
%gitPath%git pull origin master
%gitPath%git add -A
%gitPath%git commit -m "%commit%"
%gitPath%git push origin master 
pause