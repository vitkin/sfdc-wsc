echo off

echo check JDK
if "%JAVA_HOME%" == "" goto nojdk

del custom-enterprise.jar

set _CP=custom-enterprise.jar;..\..\lib\wsc.jar;..\classes;%JAVA_HOME%\lib\tools.jar

echo wsdlc ...
%JAVA_HOME%\bin\java -classpath %_CP% -Dpackage-prefix=test142 com.sforce.ws.tools.wsdlc custom-enterprise.wsdl custom-enterprise.jar

echo compiling ... 
%JAVA_HOME%\bin\javac -classpath %_CP% -d ..\classes *.java

echo running ... 
%JAVA_HOME%\bin\java -classpath  %_CP% custom.CustomClient

echo done.
goto end

:nojdk
echo "Please set JAVA_HOME"

:end
