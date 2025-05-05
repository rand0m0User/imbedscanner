@echo off
color 0a
for /r %%i in (*.png) do java -Xms1G -Xmx1G -jar "imbedscanner.jar" "%%i"
pause