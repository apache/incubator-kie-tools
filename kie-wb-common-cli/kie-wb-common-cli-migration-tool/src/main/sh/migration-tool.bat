@REM ----------------------------------------------------------------------------
@REM Copyright 2018 Red Hat, Inc. and/or its affiliates.
@REM
@REM Licensed under the Apache License, Version 2.0, available at
@REM http://apache.org/licenses/LICENSE-2.0
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Migration tool script
@REM

@echo off

if "%OS%"=="Windows_NT" SET "SCRIPT_HOME=%~dp0.."
if "%OS%"=="WINNT" SET "SCRIPT_HOME=%~dp0.."

@REM Remove extraneous quotes from variables
if not "%JAVA_HOME%" == "" set JAVA_HOME=%JAVA_HOME:"=%

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

@REM Try to infer the JAVA_HOME location from the registry
FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Runtime Environment" /v CurrentVersion') DO set CurVer=%%B

FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Runtime Environment\%CurVer%" /v JavaHome') DO set JAVA_HOME=%%B

if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto chkJVersion

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:chkJVersion
set PATH="%JAVA_HOME%\bin";%PATH%

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
   set JAVAVER=%%g
)
for /f "delims=. tokens=1-3" %%v in ("%JAVAVER%") do (
   set JAVAVER_MINOR=%%w
)

if %JAVAVER_MINOR% geq 8 goto run

echo.
echo A Java 1.8 or higher JRE is required to run. "%JAVA_HOME%\bin\java.exe" is version %JAVAVER%
echo.
goto error
@REM ==== END VALIDATION ====

goto run

@REM Start migration tool
:run

echo Using Java at %JAVA_HOME%

"%JAVA_HOME%"\bin\java.exe -cp "%SCRIPT_HOME%\lib\*" ${mainClass} %*

if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @end:local
if "%OS%"=="WINNT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
if "%OS%"=="WINNT" goto endNT

:endNT
@endlocal & set ERROR_CODE=%ERROR_CODE%
