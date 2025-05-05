@echo off
color 0a
cd dist
if not exist README.TXT (
    exit
)
cd..
"C:\Program Files\7-Zip\7z" a -sdel tmp.null build
del tmp.null
del imbedscanner.jar
cd dist
mkdir rep
cd rep
for %%i in (..\lib\*.jar) do "C:\Program Files\7-Zip\7z" x "%%i"
"C:\Program Files\7-Zip\7z" a -sdel tmp.null META-INF
del tmp.null
"C:\Program Files\7-Zip\7z" x "..\imbedscanner.jar"
"C:\Program Files\7-Zip\7z" a -tzip ..\imbedscanner.jar .\*
cd..
advzip -z -3 imbedscanner.jar
del README.TXT
"C:\Program Files\7-Zip\7z" a -sdel tmp.null rep
del tmp.null
"C:\Program Files\7-Zip\7z" a -sdel tmp.null lib
del tmp.null
move imbedscanner.jar ..\imbedscanner.jar
cd..
"C:\Program Files\7-Zip\7z" a -sdel tmp.null dist
del tmp.null