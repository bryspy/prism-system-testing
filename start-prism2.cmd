@echo off

del /F /Q "c:\program files\apache software foundation\tomcat 7.0\prism\inbound\*.*"

copy "c:\program files\apache software foundation\tomcat 7.0\prism\closet\Harper_Collins_Medium_1.xml" "c:\program files\apache software foundation\tomcat 7.0\prism\inbound"
@echo Prism Inbound Initiated
