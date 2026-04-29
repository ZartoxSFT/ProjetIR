@echo off
setlocal EnableDelayedExpansion

set "CATALINA_HOME=C:\Program Files\Apache Software Foundation\Tomcat 11.0"
set "CLASSES_DIR=%CATALINA_HOME%\webapps\ProjetIR\WEB-INF\classes"
set "SOURCES_FILE=%CLASSES_DIR%\sources.txt"

echo ===============================
echo Compilation de TOUS les .java...
echo ===============================

cd /d "%CLASSES_DIR%"

REM Liste tous les fichiers Java, puis les compile ensemble.
REM Les guillemets sont importants car le chemin contient "Program Files".
REM Les slashs / evitent que javac interprete les \ comme des caracteres d'echappement.
REM Compiler ensemble est important quand une classe depend d'une autre classe du projet.
(
    for /r %%f in (*.java) do (
        set "SOURCE=%%f"
        echo "!SOURCE:\=/!"
    )
) > "%SOURCES_FILE%"

javac -cp ".;%CATALINA_HOME%\lib\*" -d . @"%SOURCES_FILE%"

if errorlevel 1 (
    echo.
    echo Erreur de compilation.
    del "%SOURCES_FILE%" >nul 2>&1
    pause
    exit /b 1
)

del "%SOURCES_FILE%" >nul 2>&1

echo.
echo ===============================
echo Redemarrage du service Tomcat...
echo ===============================

powershell -Command "Restart-Service Tomcat11"

if errorlevel 1 (
    echo.
    echo Echec du redemarrage de Tomcat. Lance en admin.
    pause
    exit /b 1
)

echo.
echo ===============================
echo Compilation terminee avec succes !
echo ===============================
pause
