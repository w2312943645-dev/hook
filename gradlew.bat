@rem Gradle Wrapper 8.14.3
@if "%DEBUG%"=="" @echo off

pushd %~dp0

@rem Setup the Java command to use to start the JVM.
if "%JAVA_HOME%"=="" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
set JAVA_COMMAND=%JAVA_HOME%\bin\java.exe
goto okJavaHome

:noJavaHome
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
goto end

:okJavaHome
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

"%JAVA_COMMAND%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

if errorlevel 1 goto end

:end
popd