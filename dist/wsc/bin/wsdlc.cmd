@echo off

rem set WSC_HOME=...
if "%WSC_HOME%" == "" set WSC_HOME=.. 

if [%1]==[] goto usage
if [%2]==[] goto usage

echo compiling wsdl '%1' and generating jar file '%2'

echo checking JDK
if "%JAVA_HOME%" == "" goto nojdk
if NOT EXIST "%JAVA_HOME%\lib\tools.jar" goto notools

echo checking WSC
if NOT EXIST "%WSC_HOME%\lib\wsc.jar" goto nowsc

set _CP=%WSC_HOME%\lib\wsc.jar;%JAVA_HOME%\lib\tools.jar

%JAVA_HOME%\bin\java -classpath  %_CP% com.sforce.ws.tools.wsdlc %*

echo done.
goto end

:usage
echo Usage: wsdlc [WSDL-URL] [dest-file.jar]
goto end

:nowsc
echo Unable to find wsc.jar at %WSC_HOME%\lib\wsc.jar Please check WSC_HOME 
goto end

:notools
echo Unable to find tools.jar at JDK_HOME
goto end

:nojdk
echo Please set JAVA_HOME
goto end

:end
