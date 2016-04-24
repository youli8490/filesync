rem this is a GBK encode File
if %1 == "" (
	echo fail!
	pause
	exit
)
cd ..
set class_path=filesync-0.0.1-SNAPSHOT.jar;
setlocal enabledelayedexpansion
for /f "delims=\" %%a in ('dir /b lib') do (
	rem if或for复合语句中，取变量名使用!而不是%
	set class_path=!class_path!./lib/%%a;
)
start javaw -classpath %class_path% -Dfile.encoding=UTF-8 -DWORKING_HOME=%1 youli/open/filesync/client/jface/FileSyncSWTClient
