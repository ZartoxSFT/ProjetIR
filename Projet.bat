@echo off

set "TOMCAT_BASE=C:\Users\darkf\Desktop\Travail\INFOREPARTIE\tomcat"
set "CATALINA_HOME=%TOMCAT_BASE%"

echo ===============================
echo Compilation des servlets...
echo ===============================

cd /d "%TOMCAT_BASE%\webapps\ProjetIR\WEB-INF\classes"

REM Génère la liste de tous les fichiers Java (récursif)
dir /s /b *.java > sources.txt

REM Compilation avec classpath Tomcat
javac -cp ".;%TOMCAT_BASE%\lib\servlet-api.jar;%TOMCAT_BASE%\lib\jsp-api.jar;%TOMCAT_BASE%\lib\el-api.jar" -d . @sources.txt

REM Supprime le fichier temporaire
del sources.txt

if errorlevel 1 (
    echo.
    echo Erreur de compilation. Tomcat ne sera pas redemarre.
    pause
    exit /b 1
)

echo.
echo ===============================
echo Redemarrage de Tomcat...
echo ===============================

REM Arrête Tomcat (ignore l'erreur si Tomcat n'est pas en cours d'exécution)
call "%TOMCAT_BASE%\bin\shutdown.bat" 2>nul

REM Attends un peu
timeout /t 2 /nobreak

REM Redémarre Tomcat
call "%TOMCAT_BASE%\bin\startup.bat"

echo.
echo ===============================
echo Termine !
echo ===============================
pause