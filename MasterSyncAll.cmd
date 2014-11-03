REM ***Master Sync All************************************

cd ..\common
git fetch origin
git checkout master
git merge origin/master

cd ..\agents
git fetch origin
git checkout master
git merge origin/master

cd ..\dataprovider
git fetch origin
git checkout master
git merge origin/master

cd ..\ingestion
git fetch origin
git checkout master
git merge origin/master

cd ..\monitor
git fetch origin
git checkout master
git merge origin/master

cd ..\publish
git fetch origin
git checkout master
git merge origin/master

cd ..\transformation
git fetch origin
git checkout master
git merge origin/master

cd ..\prism-utils

REM ***INSTALL ALL************************************************

cd ..\common
call gradle install

cd ..\agents
call gradle install

cd ..\prism-utils

REM ***********   STOP TOMCAT ************************

net stop "Apache Tomcat 7.0 Tomcat7"

del /Q "C:\Program Files\Apache Software Foundation\Tomcat 7.0\logs\*.log"
del /Q "C:\Program Files\Apache Software Foundation\Tomcat 7.0\prism\logs\*"
del /Q "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\*.war"
rmdir /S /Q "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\dataprovider"
rmdir /S /Q "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\ingestion"
rmdir /S /Q "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\monitor"
rmdir /S /Q "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\publish"
rmdir /S /Q "C:\Program Files\Apache Software Foundation\Tomcat 7.0\webapps\transformation"

REM ***DEPLOY ALL***************************************************

cd ..\dataprovider
call gradle deploy

cd ..\ingestion
call gradle deploy

cd ..\monitor
call gradle deploy

cd ..\publish
call gradle deploy

cd ..\transformation
call gradle deploy

REM ***********   STOP TOMCAT ************************

net start "Apache Tomcat 7.0 Tomcat7"

cd ..\prism-utils

call ping-urls

call start-prism
