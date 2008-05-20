echo off

echo check JDK
if "%JAVA_HOME%" == "" goto nojdk

set _CP=..\..\lib\wsc.jar;..\classes;..\..\lib\partner-142.jar

echo compiling ... 
%JAVA_HOME%\bin\javac -classpath %_CP% -d ..\classes *.java

echo running ... 
%JAVA_HOME%\bin\java -classpath  %_CP% perf.PerfClient 

echo done.
goto end

:nojdk
echo "Please set JAVA_HOME"

:end
