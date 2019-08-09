@echo off

rem rem 注释指令
rem @ 指令不在控制台打印
rem echo off 关闭指令在控制台打印功能

cd ..

set work_home=%cd%

if %work_home% == "" (
	echo fail!
	pause
	exit
)

set class_path="";

setlocal enabledelayedexpansion

for /f "delims=\" %%a in ('dir /b lib') do (
	rem if或for复合语句中，取变量名使用!而不是%
	set class_path=!class_path!./lib/%%a;
)

start javaw -classpath %class_path% -Dfile.encoding=UTF-8 -DWORKING_HOME=%work_home% youli/open/filesync/client/jface/FileSyncSWTClient
