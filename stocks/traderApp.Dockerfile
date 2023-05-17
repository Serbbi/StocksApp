FROM maven:latest

COPY . ./

RUN mvn package -Dmaven.test.skip -Dcheckstyle.skip
ENTRYPOINT ["java", "-cp", "traders-application/target/traders-application-1.0-SNAPSHOT-jar-with-dependencies.jar", "nl.rug.aoop.tradersapplication.TradersAppMain"]