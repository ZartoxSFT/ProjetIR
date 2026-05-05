@echo off
setlocal EnableDelayedExpansion

set "TOMCAT_BASE=C:\Program Files\Apache Software Foundation\Tomcat 11.0"
set "CATALINA_HOME=%TOMCAT_BASE%"
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "CLASSES_DIR=%TOMCAT_BASE%\webapps\ProjetIR\WEB-INF\classes"
set "SOURCES_FILE=%CLASSES_DIR%\sources.txt"

echo ===============================
echo Compilation des servlets...
echo ===============================

cd /d "%CLASSES_DIR%"

REM Genere une liste de chemins relatifs pour eviter les problemes avec C:\Program Files.
(
    for /r %%f in (*.java) do (
        set "SOURCE=%%f"
        set "SOURCE=!SOURCE:%CLASSES_DIR%\=!"
        set "SOURCE=!SOURCE:\=/!"
        echo "!SOURCE!"
    )
) > "%SOURCES_FILE%"

REM Compilation avec toutes les librairies Tomcat, dont servlet-api, jsp-api et le driver PostgreSQL.
javac -cp ".;%TOMCAT_BASE%\lib\*" -d . @"%SOURCES_FILE%"

if errorlevel 1 (
    echo.
    echo Erreur de compilation. Tomcat ne sera pas redemarre.
    del "%SOURCES_FILE%" >nul 2>&1
    pause
    exit /b 1
)

del "%SOURCES_FILE%" >nul 2>&1

echo.
echo ===============================
echo Redemarrage de Tomcat...
echo ===============================

REM Redemarre le service Windows Tomcat. Si ca echoue, lance ce script en administrateur.
powershell -NoProfile -Command "Restart-Service Tomcat11"

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
