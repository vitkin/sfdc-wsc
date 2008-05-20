echo on

if "%JAVA_HOME%" == "" goto nojdk
if NOT EXIST "%WSC_HOME%\lib\wsc.jar" goto nowsc

set _CP=%WSC_HOME%\lib\wsc.jar;%WSC_HOME%\lib\wsc-ui.jar;%WSC_HOME%\lib\partner-142.jar

%JAVA_HOME%\bin\java -jar %WSC_HOME%\lib\wsc-ui.jar

goto end

:nowsc
echo Unable to find wsc.jar at %WSC_HOME%\lib\wsc.jar Please check WSC_HOME 
goto end

:nojdk
echo Please set JAVA_HOME
goto end

:end
