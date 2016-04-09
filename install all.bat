cd context
call mvn clean install -DskipTests
cd ../rendering
call mvn clean install -DskipTests
cd ../optional-rest-core
call mvn clean install -DskipTests
cd ../optional-rest-rendering
call mvn clean install -DskipTests
cd ../optional-rest-spring
call mvn clean install -DskipTests
cd ../springboot-demo
call mvn clean package -DskipTests
