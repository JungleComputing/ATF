@echo off

set IPL_APP_ARGS=

:setupArgs
if ""%1""=="""" goto doneStart
set IPL_APP_ARGS=%IPL_APP_ARGS% %1
shift
goto setupArgs

:doneStart

set DEPLOY_HOME=D:\work\Ibis-Deploy
echo %DEPLOY_HOME%
java -cp ".;lib\*;%DEPLOY_HOME%;%DEPLOY_HOME%\lib\*;%DEPLOY_HOME%\lib\javagat\*;%DEPLOY_HOME%\lib\ipl\*" -Dgat.adaptor.path=%DEPLOY_HOME%\lib\javagat\adaptors -Dibis.deploy.home=%DEPLOY_HOME% -Dworkerlibfile=lib\worker.jar atf.Test %IPL_APP_ARGS%