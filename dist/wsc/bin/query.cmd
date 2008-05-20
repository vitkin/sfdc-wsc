echo off

if "%JAVA_HOME%" == "" goto nojdk
if NOT EXIST "%WSC_HOME%\lib\wsc.jar" goto nowsc

set _CP=%WSC_HOME%\lib\wsc.jar

%JAVA_HOME%\bin\java -classpath %_CP% com.sforce.ws.tools.soql.Main config=%WSC_HOME%\bin\soql.config "cmd=query {%*}" 

goto end

:usage
echo Usage: query <soql> 
echo example: query select name from account 
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
