echo off

echo check JDK
if "%JAVA_HOME%" == "" goto nojdk

set _CP=..\classes;..\..\lib\enterprise-142.jar

echo compiling ... 
%JAVA_HOME%\bin\javac -classpath %_CP% -d ..\classes *.java

echo running ... 
%JAVA_HOME%\bin\java -classpath  %_CP% enterprise.EnterpriseClient 

echo done.
goto end

:nojdk
echo "Please set JAVA_HOME"

:end
