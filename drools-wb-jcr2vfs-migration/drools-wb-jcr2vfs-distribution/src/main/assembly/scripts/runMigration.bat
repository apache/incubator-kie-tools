@ECHO OFF

setLocal EnableDelayedExpansion
set exporterMainClass=org.drools.workbench.jcr2vfsmigration.JcrExporterLauncher
set importerMainClass=org.drools.workbench.jcr2vfsmigration.VfsImporterLauncher

rem echo "Usage: runMigration.bat"
rem echo "For example: runMigration.bat"
rem echo "Some notes:"
rem echo "- Working dir should be the directory of this script."
rem echo "- Java is recommended to be JDK and java 6 for optimal performance"
rem echo "- The environment variable JAVA_HOME should be set to the JDK installation directory"
rem echo "  For example: set JAVA_HOME="C:\Program Files\Java\jdk1.6.0"
rem echo
rem echo "Starting export app..."

set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

if exist %JAVA_HOME%\bin\java.exe (
    set JAVA_BIN=%JAVA_HOME%\bin\java
) else (
    set JAVA_BIN=java
)

%JAVA_BIN% -Xms256m -Xmx1024m -cp ..\jcr-exporter-libs\*; %exporterMainClass%

%JAVA_BIN% -Xms256m -Xmx1024m -cp ..\vfs-importer-libs\*; %importerMainClass%