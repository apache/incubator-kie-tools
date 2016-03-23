@ECHO OFF
rem Copyright 2014 Red Hat, Inc. and/or its affiliates.
rem
rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem
rem      http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

rem Script used to execute the jcr2vfs migration tool.
rem
rem Execution consists of two phases:
rem   1) Exporting the content of JCR repository into XML
rem   2) Importing the XML (generated in the previous step) into newly created VFS Git repository

setLocal enableExtensions enableDelayedExpansion

if exist %JAVA_HOME%\bin\java.exe (
    set "JAVA_BIN=%JAVA_HOME%\bin\java"
) else (
    java 2> NUL
    if not "%ERRORLEVEL%"==9009 (
        set "JAVA_BIN=java"
    ) else (
        echo.
        echo Error^^! Java installation not found on your system^^! Please install Java from http://www.java.com first.
        goto :end_process
    )
)

set "JCR_INPUT_DIR="
set "TMP_DIR=tmp-jcr2vfs"
set "VFS_OUTPUT_DIR=outputVfs"
set "OVERRIDE_VFS_REPO=false"

:loop
if not "%1"=="" (
    if "%1"=="-h" (
        goto :print_help
    )
    if "%1"=="-i" (
        set "JCR_INPUT_DIR=%2"
        shift
    )
    if "%1"=="-o" (
        set "VFS_OUTPUT_DIR=%2"
        shift
    )
    if "%1"=="-t" (
        set "TMP_DIR=%2"
        shift
    )
    if "%1"=="-f" (
        set "OVERRIDE_VFS_REPO=true"
    )
    shift
    goto :loop
)

if "%JCR_INPUT_DIR%"=="" (
    echo Error^^! JCR repository location needs to be specified using the "-i dir" option^^!
    echo.
    goto :print_help
)

set "EXPORTER_ARGS=-i %JCR_INPUT_DIR% -o %TMP_DIR%"

set "IMPORTER_ARGS=-i %TMP_DIR% -o %VFS_OUTPUT_DIR%"
if "%OVERRIDE_VFS_REPO%"=="true" (
    set "IMPORTER_ARGS=%IMPORTER_ARGS% -f"
)

set "EXPORTER_MAIN_CLASS=org.drools.workbench.jcr2vfsmigration.JcrExporterLauncher"
set "IMPORTER_MAIN_CLASS=org.drools.workbench.jcr2vfsmigration.VfsImporterLauncher"

%JAVA_BIN% -Xms256m -Xmx1024m -cp ..\jcr-exporter-libs\*; -Dlogback.configurationFile=../conf/logback.xml %EXPORTER_MAIN_CLASS% %EXPORTER_ARGS%

%JAVA_BIN% -Xms256m -Xmx1024m -cp ..\vfs-importer-libs\*; -Dlogback.configurationFile=../conf/logback.xml %IMPORTER_MAIN_CLASS% %IMPORTER_ARGS%

goto :end_process

:print_help
    echo Usage: ./runMigration.bat [options]
    echo Description: Migrates Guvnor 5.x JCR content into UberFire VFS repository.
    echo.
    echo Where [options]:
    echo     -h         Prints this help
    echo     -i dir     Directory with the JCR repository configuration
    echo     -o dir     Directory to store the migrated VFS repository in. Optional, defaults to "./outputVfs"
    echo     -r name    VFS repository name. Optional, defaults to "guvnor-jcr2vfs-migration"
    echo     -f         Force overwriting the resulting VFS repository. Optional
    echo.
    echo Notes:
    echo   - Working dir needs to be the directory of this script!
    echo   - Java is recommended to be JDK and version 6 or later
    echo   - The environment variable JAVA_HOME should be set to the JDK installation directory
    echo        For example: set JAVA_HOME="c:\Program Files\Java\jdk1.7.0_65"

:end_process
    endLocal
