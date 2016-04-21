rem this is a GBK encode File
if %1 == "" (
	echo fail!
	pause
	exit
)
echo %1
cd ../lib
set lib_dir=%cd%
set class_path=
setlocal enabledelayedexpansion
for /f "delims=\" %%a in ('dir /b "%lib_dir%"') do (
	rem if或for复合语句中，取变量名使用!而不是%
	set class_path=%%a;!class_path!
)
java -classpath %class_path% -DWORKING_HOME=%1 youli/open/filesync/client/jface/FileSyncSWTClient
pause
