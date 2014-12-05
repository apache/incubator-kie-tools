@ECHO OFF

setLocal EnableDelayedExpansion
set mainClass=org.drools.workbench.jcr2vfsmigration.VfsImporterLauncher


rem echo "Usage: runImport.bat"
rem echo "For example: runImport.bat"
rem echo "Some notes:"
rem echo "- Working dir should be the directory of this script."
rem echo "- Java is recommended to be JDK and java 6 for optimal performance"
rem echo "- The environment variable JAVA_HOME should be set to the JDK installation directory"
rem echo "  For example: set JAVA_HOME="C:\Program Files\Java\jdk1.6.0"
rem echo
rem echo "Starting import app..."


set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

rem You can use -Xmx128m or less too, but it might be slower
if exist %JAVA_HOME%\bin\java.exe (
    %JAVA_HOME%\bin\java -Xms256m -Xmx512m -server -cp ..\libs\*; %mainClass% %CMD_LINE_ARGS%
) else (
    java -Xms256m -Xmx512m -cp ..\libs\*; %mainClass% %CMD_LINE_ARGS%
)
