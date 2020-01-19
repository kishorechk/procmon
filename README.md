## Process Monitor

A simple headless process-orchestrator that will run on your workstation (one of Windows / Mac or Linux). This process monitor is responsible to read a list of processes from a reference file and to keep these processes running at all times.

### Developer environment

MacOS Sierra
Maven 3.5.2
Java 8

### Exceution steps

1. git clone
2. cd procmon
3. compile source code
   ```
   \$MAVEN_HOME/bin/mvn clean package
   ```
4. start the app
   ```
   $JAVA_HOME/bin/java -jar target/procmon-1.0.jar input-file.txt
   ```
5. Testing scenarios

- update the content of “input-file.txt” from first-child-process-v1 to first-child-process-v2
- update the content of “input-file.txt” from second-child-process-v1 to second-child-process-v2
- update the content of “input-file.txt”, remove second-child-process-v2
- update the content of “input-file.txt” from file-child-process-v1
