@echo off

set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0"

echo ===============================
echo Compilation des servlets...
echo ===============================

cd /d "%CATALINA_HOME%\webapps\TP4\WEB-INF\classes"

javac -cp ".;%CATALINA_HOME%\lib\*" *.java

if errorlevel 1 (
    echo.
    echo Erreur de compilation. Tomcat ne sera pas redemarre.
    pause
    exit /b 1
)

echo.
echo ===============================
echo Redemarrage du service Tomcat...
echo ===============================

powershell -Command "Restart-Service Tomcat11"

if errorlevel 1 (
    echo.
    echo Echec du redemarrage de Tomcat. Lance ce script en administrateur.
    pause
    exit /b 1
)

echo.
echo ===============================
echo Termine !
echo ===============================
pause