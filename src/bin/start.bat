rem this is a GBK encode File
if %1 == "" (
	echo fail!
	pause
	exit
)
echo %1
cd ../lib
java -DWORKING_HOME=%1 -jar filesync-0.0.1-SNAPSHOT.jar 
pause
