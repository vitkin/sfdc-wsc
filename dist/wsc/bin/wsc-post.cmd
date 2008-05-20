@echo off

if "%JAVA_HOME%" == "" goto nojdk
if NOT EXIST "%WSC_HOME%\lib\wsc.jar" goto nowsc

set _CP=%WSC_HOME%\lib\wsc.jar

%JAVA_HOME%\bin\java -classpath %_CP% com.sforce.ws.tools.post %1 %2 %3 %4 

goto end

:usage
echo Usage: wsc-post [endpoint] [request-file] 
goto end

:nowsc
echo Unable to find wsc.jar at %WSC_HOME%\lib\wsc.jar Please check WSC_HOME 
goto end

:nojdk
echo Please set JAVA_HOME
goto end

:end
